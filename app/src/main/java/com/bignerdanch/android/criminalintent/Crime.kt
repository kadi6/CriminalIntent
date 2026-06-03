package com.bignerdanch.android.criminalintent

import java.util.Date
import java.util.UUID

// ОБЪЕКТНАЯ МОДЕЛЬ — класс, описывающий сущность (Объект из реального мира, который мы моделируем в коде, задавая свойства)
data class Crime (
    val id: UUID = UUID.randomUUID(), // способ генерирования универсально-уникальных идентификаторов
    var title: String = "",
    var date: Date = Date(), // по умолчанию задаёт текущую дату
    var isSolved: Boolean = false,
    var requiresPolice: Boolean = false
)