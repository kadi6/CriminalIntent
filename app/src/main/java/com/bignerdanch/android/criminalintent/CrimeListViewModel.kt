package com.bignerdanch.android.criminalintent

import androidx.lifecycle.ViewModel

// СПИСОК ОБЪЕКТНОЙ МОДЕЛИ
class CrimeListViewModel : ViewModel() {

    // Делаем ссылку на репозиторий
    private val crimeRepository = CrimeRepository.get()
    // Вызываем функцию репозитория
    val crimeListLiveData = crimeRepository.getCrimes()

    fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }


}