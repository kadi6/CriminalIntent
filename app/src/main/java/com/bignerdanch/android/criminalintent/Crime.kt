package com.bignerdanch.android.criminalintent

import androidx.room.Entity
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
    var requiresPolice: Boolean = false
)