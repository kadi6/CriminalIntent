package com.bignerdanch.android.criminalintent.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bignerdanch.android.criminalintent.Crime

// СОЗДАНИЕ ШАБЛОНА КЛАССА БАЗЫ ДАННЫХ - класс для описания всей базы данных

// Сообщаем Room, что это класс базы данных
@Database(entities = [ Crime::class ], version=4)



// Добавляем конвертеры
@TypeConverters(CrimeTypeConverters::class)


abstract class CrimeDatabase : RoomDatabase() {

    abstract fun crimeDao(): CrimeDao


}