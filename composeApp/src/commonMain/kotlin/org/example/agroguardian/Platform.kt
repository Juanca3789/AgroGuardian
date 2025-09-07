package org.example.agroguardian

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform