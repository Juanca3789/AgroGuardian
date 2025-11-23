package com.dev.jcctech.agroguardian.data.ai

object LlamaBridge {
    init {
        System.loadLibrary("llama_jni")
    }

    external fun init(modelPath: String): Boolean

    external fun infer(prompt: String, callback: TokenCallback)

    external fun release()
}

interface TokenCallback {
    fun onToken(piece: String)
    fun onComplete()
    fun onError(error: String)
}