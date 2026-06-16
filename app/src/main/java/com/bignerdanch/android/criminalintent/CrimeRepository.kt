package com.bignerdanch.android.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdanch.android.criminalintent.database.CrimeDatabase
import java.util.UUID

private const val DATABASE_NAME = "crime-database"


// ШАБЛОН РЕПОЗИТОРИЯ - архитектурный посредник между UI и данными

class CrimeRepository private constructor(context: Context) {


    // Помещаем в свойство объект базы данных CrimeDatabase
    private val database : CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        DATABASE_NAME
    ).build()

    // Помещаем в свойство объект DAO
    private val crimeDao = database.crimeDao()

    // Функции для работы с SQLite-файлом
    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)







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