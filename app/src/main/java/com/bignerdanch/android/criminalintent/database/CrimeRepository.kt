package com.bignerdanch.android.criminalintent.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.File
import java.util.UUID
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"

// Миграция обновляет существующую базу данных, добавляя новую колонку в таблицу
private val migration_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Crime ADD COLUMN requiresPolice INTEGER NOT NULL DEFAULT 0")
    }
}

private val migration_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Crime ADD COLUMN suspect TEXT NOT NULL DEFAULT ''")
    }
}

private val migration_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Crime ADD COLUMN suspectId TEXT")
    }
}



// ШАБЛОН РЕПОЗИТОРИЯ - архитектурный посредник между UI и данными

class CrimeRepository private constructor(context: Context) {


    // Помещаем в свойство объект базы данных CrimeDatabase
    private val database : CrimeDatabase = Room
        .databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
        )
        .addMigrations(
            migration_1_2,
            migration_2_3,
            migration_3_4
        )
        .build()

    // Помещаем в свойство объект DAO
    private val crimeDao = database.crimeDao()

    // Создание исполнителя для создания фонового потока
    private val executor = Executors.newSingleThreadExecutor()

    private val filesDir = context.applicationContext.filesDir  // Получаем absolute path к файлам нашего приложения

    // Функции для работы с SQLite-файлом
    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    fun updateCrime(crime: Crime) {
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }
    fun addCrime(crime: Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }


    fun getPhotoFile(crime: Crime): File = File(filesDir, crime.photoFileName) // Creates a new File instance from a parent abstract pathname and a child pathname string







    companion object {
        private var INSTANCE: CrimeRepository? = null
        // Инициализируем экземпляр
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }
        // Обеспечиваем доступ к экземпляру
        fun get(): CrimeRepository {
            return INSTANCE ?:
            throw IllegalStateException("CrimeRepository must be initialized")
        }
    }
}