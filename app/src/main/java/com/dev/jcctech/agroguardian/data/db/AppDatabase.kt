package com.dev.jcctech.agroguardian.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dev.jcctech.agroguardian.data.db.dao.ForumDao
import com.dev.jcctech.agroguardian.data.db.dao.WeatherDao
import com.dev.jcctech.agroguardian.data.db.entity.AnswerEntity
import com.dev.jcctech.agroguardian.data.db.entity.QuestionEntity
import com.dev.jcctech.agroguardian.data.db.entity.WeatherEntity

@Database(
    entities = [WeatherEntity::class, QuestionEntity::class, AnswerEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun forumDao(): ForumDao
}