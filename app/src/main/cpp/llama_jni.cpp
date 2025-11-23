#include <jni.h>
#include <string>
#include <vector>
#include <algorithm>
#include <cstdint>
#include <android/log.h>
#include <thread>
#include <mutex>
#include <atomic>
#include <random>
#include "llama.h"

#define LOG_TAG "LlamaJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,  LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static llama_model*   g_model = nullptr;
static llama_context* g_ctx   = nullptr;
static std::mutex     g_mutex;
static JavaVM*        g_jvm = nullptr;
static std::vector<llama_token> g_history_tokens;
static int    g_n_ctx       = 512;
static int    g_n_threads   = 6;
static int    g_max_tokens  = 256;
static float  g_temperature = 0.7f;
static int    g_top_k       = 40;
static float  g_top_p       = 0.9f;

static std::string jstring_to_utf8(JNIEnv* env, jstring js) {
    if (!js) return {};
    const char* cstr = env->GetStringUTFChars(js, nullptr);
    std::string out = cstr ? cstr : "";
    env->ReleaseStringUTFChars(js, cstr);
    return out;
}

static std::string tokens_to_text(const std::vector<llama_token>& tokens) {
    std::string out;
    out.reserve(tokens.size() * 4);
    const llama_vocab* vocab = llama_model_get_vocab(g_model);
    char buf[256];
    for (llama_token t : tokens) {
        int32_t n = llama_token_to_piece(vocab, t, buf, (int32_t)sizeof(buf), 0, false);
        if (n > 0) out.append(buf, (size_t)n);
    }
    return out;
}

static void clean_output(std::string& output) {
    size_t start = output.find_first_not_of(" \n\r\t");
    size_t end   = output.find_last_not_of(" \n\r\t");
    if (start == std::string::npos || end == std::string::npos) {
        output.clear();
        return;
    }
    output = output.substr(start, end - start + 1);
    for (char& c : output) if (c == '\r') c = '\n';
    std::string cleaned;
    cleaned.reserve(output.size());
    bool last_space = false;
    for (char c : output) {
        if (c == ' ') {
            if (!last_space) { cleaned.push_back(c); last_space = true; }
        } else { cleaned.push_back(c); last_space = false; }
    }
    output.swap(cleaned);
}

static void trim_history_to_fit(int n_ctx) {
    const int margin = 256;
    const int allowed = std::max(32, n_ctx - margin);
    if ((int)g_history_tokens.size() > allowed) {
        int drop = (int)g_history_tokens.size() - allowed;
        g_history_tokens.erase(g_history_tokens.begin(), g_history_tokens.begin() + drop);
    }
}

static int sample_token(const float* logits, int vocab_n, float temperature, int top_k, float top_p, std::mt19937& rng) {
    if (temperature <= 0.0f) {
        int best = 0;
        float bestv = logits[0];
        for (int i = 1; i < vocab_n; ++i) {
            if (logits[i] > bestv) { bestv = logits[i]; best = i; }
        }
        return best;
    }
    std::vector<std::pair<int,float>> cand;
    cand.reserve(std::min(vocab_n, top_k));
    for (int i = 0; i < vocab_n; ++i) cand.emplace_back(i, logits[i]);
    std::partial_sort(cand.begin(), cand.begin() + std::min((int)cand.size(), top_k), cand.end(), [](auto &a, auto &b){ return a.second > b.second; });
    int k = std::min((int)cand.size(), top_k);
    std::vector<float> probs(k);
    float maxl = cand[0].second;
    double sum = 0.0;
    for (int i = 0; i < k; ++i) {
        double v = cand[i].second - maxl;
        double p = exp(v / (double)temperature);
        probs[i] = (float)p;
        sum += p;
    }
    for (int i = 0; i < k; ++i) probs[i] /= (float)sum;
    if (top_p < 1.0f) {
        float accum = 0.0f;
        int limit = 0;
        for (int i = 0; i < k; ++i) {
            accum += probs[i];
            limit = i;
            if (accum >= top_p) break;
        }
        k = std::max(1, limit + 1);
        float s = 0.0f;
        for (int i = 0; i < k; ++i) s += probs[i];
        for (int i = 0; i < k; ++i) probs[i] /= s;
    }
    std::discrete_distribution<int> dist(probs.begin(), probs.begin() + k);
    int idx = dist(rng);
    return cand[idx].first;
}

extern "C" {

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    g_jvm = vm;
    return JNI_VERSION_1_6;
}

JNIEXPORT jboolean JNICALL
Java_com_dev_jcctech_agroguardian_data_ai_LlamaBridge_init(
        JNIEnv* env, jobject, jstring jModelPath) {

    std::lock_guard<std::mutex> lock(g_mutex);

    // Limpiar recursos previos
    if (g_ctx) {
        llama_free(g_ctx);
        g_ctx = nullptr;
    }
    if (g_model) {
        llama_model_free(g_model);  // Tu API usa esta
        g_model = nullptr;
    }

    const std::string modelPath = jstring_to_utf8(env, jModelPath);
    if (modelPath.empty()) {
        LOGE("Model path is empty");
        return JNI_FALSE;
    }

    LOGI("Loading model from: %s", modelPath.c_str());
    llama_backend_init();

    llama_model_params mparams = llama_model_default_params();
    mparams.n_gpu_layers = 0;

    g_model = llama_model_load_from_file(modelPath.c_str(), mparams);
    if (!g_model) {
        LOGE("Failed to load model");
        llama_backend_free();
        return JNI_FALSE;
    }

    LOGI("Model loaded, creating context...");

    llama_context_params cparams = llama_context_default_params();
    cparams.n_ctx             = g_n_ctx;       // 512
    cparams.n_batch           = g_n_ctx;       // ESTO ES LO IMPORTANTE
    cparams.n_threads         = g_n_threads;   // 6
    cparams.n_threads_batch   = g_n_threads;   // ESTO TAMBIÉN
    cparams.embeddings        = false;

    g_ctx = llama_init_from_model(g_model, cparams);
    if (!g_ctx) {
        LOGE("Failed to create context");
        llama_model_free(g_model);
        g_model = nullptr;
        llama_backend_free();
        return JNI_FALSE;
    }

    g_history_tokens.clear();

    LOGI("✅ Init OK - ctx:%d threads:%d batch:%d",
         llama_n_ctx(g_ctx),
         llama_n_threads(g_ctx),
         llama_n_batch(g_ctx));

    return JNI_TRUE;
}

JNIEXPORT void JNICALL
Java_com_dev_jcctech_agroguardian_data_ai_LlamaBridge_infer(
        JNIEnv* env, jobject, jstring jPrompt, jobject jCallback) {

    if (!g_ctx || !g_model) {
        LOGE("Infer aborted: context/model null");
        return;
    }

    const std::string user_prompt = jstring_to_utf8(env, jPrompt);
    if (user_prompt.empty()) {
        LOGE("Infer aborted: empty prompt");
        return;
    }

    jobject gCallback = env->NewGlobalRef(jCallback);
    if (!gCallback) {
        LOGE("Failed to create GlobalRef for callback");
        return;
    }

    LOGI("Launching inference thread…");

    std::thread([user_prompt, gCallback]() {
        LOGI("Thread started, attaching JVM…");

        JNIEnv* threadEnv = nullptr;
        jint res = g_jvm->AttachCurrentThread(&threadEnv, nullptr);
        if (res != JNI_OK || threadEnv == nullptr) {
            LOGE("Failed to attach thread to JVM (res=%d)", res);
            JNIEnv* tmpEnv = nullptr;
            if (g_jvm->GetEnv(reinterpret_cast<void**>(&tmpEnv), JNI_VERSION_1_6) == JNI_OK && tmpEnv) {
                tmpEnv->DeleteGlobalRef(gCallback);
            }
            return;
        }

        jclass cbClass = threadEnv->GetObjectClass(gCallback);
        if (!cbClass) {
            LOGE("Failed to GetObjectClass");
            threadEnv->DeleteGlobalRef(gCallback);
            g_jvm->DetachCurrentThread();
            return;
        }

        jmethodID onToken = threadEnv->GetMethodID(cbClass, "onToken", "(Ljava/lang/String;)V");
        jmethodID onComplete = threadEnv->GetMethodID(cbClass, "onComplete", "()V");
        jmethodID onError = threadEnv->GetMethodID(cbClass, "onError", "(Ljava/lang/String;)V");

        if (!onToken) {
            LOGE("onToken method not found");
            threadEnv->DeleteLocalRef(cbClass);
            threadEnv->DeleteGlobalRef(gCallback);
            g_jvm->DetachCurrentThread();
            return;
        }

        // Helper para enviar mensajes de progreso
        auto sendProgress = [&](const char* msg) {
            if (onToken) {
                jstring jMsg = threadEnv->NewStringUTF(msg);
                threadEnv->CallVoidMethod(gCallback, onToken, jMsg);
                threadEnv->DeleteLocalRef(jMsg);
            }
        };

        // NO USAR SYSTEM PROMPT - solo el prompt del usuario
        const std::string full_prompt = user_prompt;

        auto try_tokenize = [&](const char* text, std::vector<llama_token>& out) -> int {
            std::lock_guard<std::mutex> lock(g_mutex);
            const llama_vocab* vocab = llama_model_get_vocab(g_model);
            if (!vocab) return 0;

            int32_t nt = llama_tokenize(vocab, text, (int32_t)strlen(text), nullptr, 0, false, false);
            if (nt > 0) {
                out.resize(nt);
                int32_t rc = llama_tokenize(vocab, text, (int32_t)strlen(text), out.data(), nt, false, false);
                if (rc == nt) return nt;
            }

            nt = llama_tokenize(vocab, text, (int32_t)strlen(text), nullptr, 0, true, true);
            if (nt > 0) {
                out.resize(nt);
                int32_t rc = llama_tokenize(vocab, text, (int32_t)strlen(text), out.data(), nt, true, true);
                if (rc == nt) return nt;
            }

            int estimated = (int)strlen(text) * 2 + 512;
            out.resize(estimated);
            int32_t rc = llama_tokenize(vocab, text, (int32_t)strlen(text), out.data(), estimated, false, false);
            if (rc > 0) {
                out.resize(rc);
                return rc;
            }

            out.clear();
            return 0;
        };

        std::vector<llama_token> prompt_tokens;
        int nt = try_tokenize(full_prompt.c_str(), prompt_tokens);

        LOGI("Tokenized: %d tokens", nt);
        if (nt == 0) {
            LOGE("Tokenization failed");
            if (onError) {
                jstring errMsg = threadEnv->NewStringUTF("Error de tokenización");
                threadEnv->CallVoidMethod(gCallback, onError, errMsg);
                threadEnv->DeleteLocalRef(errMsg);
            }
            threadEnv->DeleteLocalRef(cbClass);
            threadEnv->DeleteGlobalRef(gCallback);
            g_jvm->DetachCurrentThread();
            return;
        }

        // Procesar prompt en chunks PEQUEÑOS (32 tokens) con feedback visual
        sendProgress("[Procesando entrada");

        LOGI("Starting decode of %d tokens in small chunks...", nt);
        {
            std::lock_guard<std::mutex> lock(g_mutex);
            const int CHUNK_SIZE = 32;  // Muy pequeño para dispositivos lentos
            int total_chunks = (nt + CHUNK_SIZE - 1) / CHUNK_SIZE;

            for (int chunk_start = 0; chunk_start < nt; chunk_start += CHUNK_SIZE) {
                int chunk_end = std::min(chunk_start + CHUNK_SIZE, nt);
                int chunk_size = chunk_end - chunk_start;
                int chunk_num = (chunk_start / CHUNK_SIZE) + 1;

                LOGI("→ Chunk %d/%d (%d tokens)", chunk_num, total_chunks, chunk_size);

                // Feedback visual cada chunk
                if (chunk_num % 2 == 0) {
                    sendProgress(".");
                }

                std::vector<llama_token> tok(chunk_size);
                std::vector<llama_pos> pos(chunk_size);
                std::vector<int32_t> nseq(chunk_size, 1);
                std::vector<int32_t> seq_storage(chunk_size, 0);
                std::vector<llama_seq_id*> seq_ptrs(chunk_size);
                std::vector<int8_t> logits_flags(chunk_size, 0);

                for (int i = 0; i < chunk_size; ++i) {
                    tok[i] = prompt_tokens[chunk_start + i];
                    pos[i] = chunk_start + i;
                    seq_ptrs[i] = reinterpret_cast<llama_seq_id*>(&seq_storage[i]);
                }

                if (chunk_end == nt) {
                    logits_flags[chunk_size - 1] = 1;
                }

                llama_batch batch{};
                batch.n_tokens = chunk_size;
                batch.token    = tok.data();
                batch.pos      = pos.data();
                batch.n_seq_id = nseq.data();
                batch.seq_id   = seq_ptrs.data();
                batch.logits   = logits_flags.data();

                auto start = std::chrono::steady_clock::now();
                int decode_result = llama_decode(g_ctx, batch);
                auto end = std::chrono::steady_clock::now();
                auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();

                LOGI("Chunk %d done in %lld ms, result=%d", chunk_num, (long long)duration, decode_result);

                if (decode_result != 0) {
                    LOGE("Decode failed at chunk %d", chunk_num);
                    if (onError) {
                        jstring errMsg = threadEnv->NewStringUTF("Error procesando entrada");
                        threadEnv->CallVoidMethod(gCallback, onError, errMsg);
                        threadEnv->DeleteLocalRef(errMsg);
                    }
                    threadEnv->DeleteLocalRef(cbClass);
                    threadEnv->DeleteGlobalRef(gCallback);
                    g_jvm->DetachCurrentThread();
                    return;
                }
            }
        }

        sendProgress("]\n");
        LOGI("✓ Prompt processed. Generating...");

        // Generación
        std::vector<llama_token> generated;
        generated.reserve(g_max_tokens);
        std::mt19937 rng((uint32_t)std::hash<std::thread::id>{}(std::this_thread::get_id()));
        const llama_vocab* vocab = llama_model_get_vocab(g_model);
        int32_t vocab_n = vocab ? llama_vocab_n_tokens(vocab) : 0;
        llama_token eos = vocab ? llama_vocab_eos(vocab) : LLAMA_TOKEN_NULL;

        bool emitted_any = false;
        bool generation_error = false;

        for (int step = 0; step < g_max_tokens; ++step) {
            const float* logits = llama_get_logits(g_ctx);
            if (!logits) {
                LOGE("Null logits at step %d", step);
                generation_error = true;
                break;
            }

            int next = sample_token(logits, vocab_n, g_temperature, g_top_k, g_top_p, rng);

            if (eos != LLAMA_TOKEN_NULL && next == eos) {
                LOGI("EOS at step %d", step);
                break;
            }

            generated.push_back((llama_token)next);

            // Decode siguiente token
            {
                std::lock_guard<std::mutex> lock(g_mutex);
                int pos_val = nt + (int)generated.size() - 1;
                if (pos_val >= llama_n_ctx(g_ctx)) {
                    LOGE("Context limit");
                    break;
                }

                int32_t seq_storage = 0;
                llama_token tok1[1] = { (llama_token)next };
                llama_pos pos1[1] = { (llama_pos)pos_val };
                int32_t nseq1[1] = { 1 };
                llama_seq_id* seq_ptrs1[1] = { reinterpret_cast<llama_seq_id*>(&seq_storage) };
                int8_t logits_flags1[1] = { 1 };

                llama_batch batch1{};
                batch1.n_tokens = 1;
                batch1.token    = tok1;
                batch1.pos      = pos1;
                batch1.n_seq_id = nseq1;
                batch1.seq_id   = seq_ptrs1;
                batch1.logits   = logits_flags1;

                if (llama_decode(g_ctx, batch1)) {
                    LOGE("Decode failed at step %d", step);
                    generation_error = true;
                    break;
                }
            }

            // Token a texto
            char piece_buf[512];
            int lstrip_flag = generated.size() == 1 ? 1 : 0;
            int n = llama_token_to_piece(vocab, (llama_token)next, piece_buf, (int)sizeof(piece_buf), lstrip_flag, false);
            if (n <= 0) continue;

            std::string piece(piece_buf, n);
            std::string safe;
            safe.reserve(piece.size());
            for (unsigned char c : piece) {
                if (c == 0) continue;
                if (c >= 32 || c == 10 || c == 9) safe.push_back((char)c);
                else safe.push_back('?');
            }
            if (safe.empty()) safe = piece;

            jstring jPiece = threadEnv->NewStringUTF(safe.c_str());
            if (jPiece) {
                threadEnv->CallVoidMethod(gCallback, onToken, jPiece);

                if (threadEnv->ExceptionCheck()) {
                    LOGE("Exception in onToken");
                    threadEnv->ExceptionDescribe();
                    threadEnv->ExceptionClear();
                    threadEnv->DeleteLocalRef(jPiece);
                    generation_error = true;
                    break;
                }

                threadEnv->DeleteLocalRef(jPiece);
                emitted_any = true;
            }

            // Log cada 10 tokens
            if ((step + 1) % 10 == 0) {
                LOGI("Generated %d tokens", step + 1);
            }
        }

        LOGI("Generation finished: emitted=%d, error=%d", emitted_any, generation_error);

        if (generation_error && onError) {
            jstring errMsg = threadEnv->NewStringUTF("Error en generación");
            threadEnv->CallVoidMethod(gCallback, onError, errMsg);
            threadEnv->DeleteLocalRef(errMsg);
        } else if (onComplete) {
            threadEnv->CallVoidMethod(gCallback, onComplete);
            if (threadEnv->ExceptionCheck()) {
                threadEnv->ExceptionDescribe();
                threadEnv->ExceptionClear();
            }
        }

        threadEnv->DeleteLocalRef(cbClass);
        threadEnv->DeleteGlobalRef(gCallback);
        g_jvm->DetachCurrentThread();
        LOGI("Thread finished");

    }).detach();
}

JNIEXPORT void JNICALL Java_com_dev_jcctech_agroguardian_data_ai_LlamaBridge_release(JNIEnv* env, jobject) {
    std::lock_guard<std::mutex> lock(g_mutex);
    if (g_ctx) { llama_free(g_ctx); g_ctx = nullptr; }
    if (g_model) { llama_model_free(g_model); g_model = nullptr; }
    llama_backend_free();
    g_history_tokens.clear();
}

}