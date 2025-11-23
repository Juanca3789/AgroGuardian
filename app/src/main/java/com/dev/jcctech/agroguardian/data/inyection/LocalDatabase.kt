package com.dev.jcctech.agroguardian.data.inyection

import com.dev.jcctech.agroguardian.data.db.AppDatabase

object LocalDatabase {
    lateinit var instance: AppDatabase
}