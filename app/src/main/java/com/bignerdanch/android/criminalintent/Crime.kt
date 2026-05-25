package com.bignerdanch.android.criminalintent

import java.util.Date
import java.util.UUID

data class Crime (
    val id: UUID = UUID.randomUUID(), // способ генерирования универсально-уникальных идентификаторов
    var title: String = "",
    var date: Date = Date(), // по умолчанию задаёт текущую дату
    var isSolved: Boolean = false
)