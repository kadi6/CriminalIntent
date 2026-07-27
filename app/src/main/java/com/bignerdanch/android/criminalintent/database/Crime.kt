package com.bignerdanch.android.criminalintent.database

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

// ОБЪЕКТНАЯ МОДЕЛЬ — класс, описывающий сущность (Объект из реального мира, который мы моделируем в коде, задавая свойства)
@Entity  // Определяем сущность для Room - структуру таблиц
data class Crime (
    // Объяввление первичного ключа в базе данных
    @PrimaryKey val id: UUID = UUID.randomUUID(), // способ генерирования универсально-уникальных идентификаторов
    var title: String = "",
    var date: Date = Date(), // по умолчанию задаёт текущую дату
    var isSolved: Boolean = false,
    var requiresPolice: Boolean = false,
    var suspect: String = "",
    var suspectId: String? = null
) {
    @get:Ignore     // Помечаем, что свойство не относится к базе
    val photoFileName
        get() = "IMG_$id.jpg"
}