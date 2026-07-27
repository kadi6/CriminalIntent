package com.bignerdanch.android.criminalintent.crime_list_fragment

import androidx.lifecycle.ViewModel
import com.bignerdanch.android.criminalintent.database.Crime
import com.bignerdanch.android.criminalintent.database.CrimeRepository

// СПИСОК ОБЪЕКТНОЙ МОДЕЛИ
class CrimeListViewModel : ViewModel() {

    // Делаем ссылку на репозиторий
    private val crimeRepository = CrimeRepository.get()
    // Вызываем функцию репозитория
    val crimeListLiveData = crimeRepository.getCrimes() // Здесь происходит всего один запрос и возвращается один объект LiveData<List<Crime>>

    fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }


}