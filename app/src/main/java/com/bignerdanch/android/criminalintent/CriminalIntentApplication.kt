package com.bignerdanch.android.criminalintent

import android.app.Application
import com.bignerdanch.android.criminalintent.database.CrimeRepository

class CriminalIntentApplication: Application() {

    override fun onCreate() { // подходящее место для разовой инициализации
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}