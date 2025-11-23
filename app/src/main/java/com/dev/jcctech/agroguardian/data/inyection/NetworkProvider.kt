package com.dev.jcctech.agroguardian.data.inyection

import android.content.Context
import com.dev.jcctech.agroguardian.data.remote.provider.token.AndroidTokenProvider
import com.dev.jcctech.agroguardian.data.remote.provider.token.TokenProvider

object NetworkProvider {
    lateinit var tokenProvider: TokenProvider

    fun init(appContext: Context) {
        tokenProvider = AndroidTokenProvider(appContext.authDataStore)
    }
}