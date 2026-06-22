package com.bignerdanch.android.criminalintent

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import java.util.UUID


class CrimeViewModel() : ViewModel() {


    // связь с CrimeRepository
    private val crimeRepository = CrimeRepository.get()

    // хранит идентификатор отображаемого в данный момент преступления / Ведём  наблюдение за идентификатором
    private val crimeIdLiveData = MutableLiveData<UUID>()


    // преобразуем MutableLiveData<UUID> в LiveData<Crime?>
    val crimeLiveData: LiveData<Crime?> = // объект LiveData создается только один ! Подписчики довольны
        crimeIdLiveData.switchMap { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

    // инкапсулируем передачу данных - фрагмент просто передает аргумент (идентификатор) и дальше его внутренняя кухня ViewModel не интересует
    fun loadCrime(crimeId: UUID) {
        crimeIdLiveData.value = crimeId // передаем идентификатор
    }

    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }


}



