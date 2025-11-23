package com.dev.jcctech.agroguardian

import android.app.Application
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dev.jcctech.agroguardian.data.db.AppDatabase
import com.dev.jcctech.agroguardian.data.inyection.LocalDatabase
import com.dev.jcctech.agroguardian.data.inyection.NetworkProvider
import com.dev.jcctech.agroguardian.data.ai.ObbModelLoader
import com.dev.jcctech.agroguardian.data.ai.LlamaBridge
import com.dev.jcctech.agroguardian.data.ai.TokenCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.CompletableDeferred

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkProvider.init(this)
        LocalDatabase.instance = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "agroguardian.db"
        )
            .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .fallbackToDestructiveMigration(true)
            .build()
        Log.d("MyApp", "Base de datos inicializada.")
        /*
        CoroutineScope(Dispatchers.IO).launch {
            val ok = ObbModelLoader.loadModel(this@MyApp)
            Log.d("AgroGuardianApp", "Modelo cargado: $ok")

            if (ok) {
                val sb = StringBuilder()
                val deferred = CompletableDeferred<String>()

                val prompt = """
    <|im_start|>user
    Tengo manchas amarillas en mis plantas de maíz, ¿qué puede ser?
    <|im_end|>
    <|im_start|>assistant
    """.trimIndent()

                LlamaBridge.infer(prompt, object : TokenCallback {
                    override fun onToken(piece: String) {
                        sb.append(piece)
                        Log.d("LlamaBridge", "Token: $piece")
                    }

                    override fun onComplete() {
                        Log.d("LlamaBridge", "Generación completa")
                        deferred.complete(sb.toString())
                    }

                    override fun onError(error: String) {
                        Log.e("LlamaBridge", "Error: $error")
                        deferred.completeExceptionally(Exception(error))
                    }
                })

                // Espera a que termine la inferencia
                try {
                    val response = deferred.await()
                    Log.d("LlamaBridge", "Respuesta final: $response")
                } catch (e: Exception) {
                    Log.e("LlamaBridge", "Error esperando respuesta: ${e.message}")
                }
            } else {
                Log.e("AgroGuardianApp", "No se pudo inicializar el modelo")
            }
        }
         */
    }
}
