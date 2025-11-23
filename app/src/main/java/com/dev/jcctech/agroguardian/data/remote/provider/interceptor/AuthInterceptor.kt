package com.dev.jcctech.agroguardian.data.remote.provider.interceptor

import com.dev.jcctech.agroguardian.data.remote.model.auth.RefreshRequest
import com.dev.jcctech.agroguardian.data.remote.provider.RetrofitProvider.authApi
import com.dev.jcctech.agroguardian.data.remote.provider.token.TokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: TokenProvider
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenProvider.getAccessToken()
        }
        val request = chain.request().newBuilder().apply {
            if(!token.isNullOrEmpty()){
                addHeader("Authorization", "Bearer $token")
            }
        }.build()
        var response = chain.proceed(request)
        if(response.code == 401){
            response.close()
            val newToken = runBlocking {
                val refreshToken = tokenProvider.getRefreshToken()
                if(!refreshToken.isNullOrEmpty()){
                    try {
                        val tokenResponse = authApi.refresh(RefreshRequest(refreshToken))
                        tokenProvider.saveAccessToken(tokenResponse.accessToken)
                        tokenProvider.saveRefreshToken(tokenResponse.refreshToken)
                        tokenResponse.accessToken
                    } catch (e: Exception) {
                        null
                    }
                } else null
            }

            if (!newToken.isNullOrEmpty()) {
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $newToken")
                    .build()
                response = chain.proceed(newRequest)
            }
        }
        return response
    }
}