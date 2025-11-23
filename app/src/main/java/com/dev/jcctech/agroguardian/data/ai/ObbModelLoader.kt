package com.dev.jcctech.agroguardian.data.ai

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.util.zip.ZipFile

object ObbModelLoader {

    private const val TAG = "ObbModelLoader"
    private const val MODEL_FOLDER = "expansion/"
    private const val MODEL_FILENAME = "qwen2.5-1.5b-instruct-q3_k_m.gguf"
    private const val PACKAGE_NAME = "com.dev.jcctech.agroguardian"
    private const val VERSION_CODE = 1

    fun loadModel(context: Context): Boolean {
        try {
            val obbDir = File(
                Environment.getExternalStorageDirectory(),
                "Android/obb/$PACKAGE_NAME"
            )
            val obbFile = File(obbDir, "main.$VERSION_CODE.$PACKAGE_NAME.obb")
            if (!obbFile.exists()) throw IllegalStateException("OBB no encontrado en ${obbFile.absolutePath}")

            ZipFile(obbFile).use { zipFile ->
                val entry = zipFile.getEntry(MODEL_FOLDER + MODEL_FILENAME)
                    ?: throw IllegalStateException("No se encontró ${MODEL_FOLDER + MODEL_FILENAME} en el OBB")

                val modelFile = File(context.filesDir, MODEL_FILENAME)
                modelFile.parentFile?.mkdirs()

                if (modelFile.exists() && modelFile.length() == entry.size) {
                    Log.d(TAG, "Modelo ya existe y está completo: ${modelFile.absolutePath}")
                } else {
                    Log.d(TAG, "Copiando modelo desde OBB...")
                    zipFile.getInputStream(entry).use { input ->
                        modelFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    Log.d(TAG, "Modelo copiado a ${modelFile.absolutePath}")
                }

                return LlamaBridge.init(modelFile.absolutePath)
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar modelo desde OBB", e)
            return false
        }
    }
}