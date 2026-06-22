package com.bignerdanch.android.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdanch.android.criminalintent.database.CrimeDatabase
import java.util.UUID
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"

// Миграция обновляет существующую базу данных, добавляя новую колонку в таблицу
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE Crime ADD COLUMN requiresPolice INTEGER NOT NULL DEFAULT 0")
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
        .addMigrations(MIGRATION_1_2)
        .build()

    // Помещаем в свойство объект DAO
    private val crimeDao = database.crimeDao()

    // Создание исполнителя для создания фонового потока
    private val executor = Executors.newSingleThreadExecutor()

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