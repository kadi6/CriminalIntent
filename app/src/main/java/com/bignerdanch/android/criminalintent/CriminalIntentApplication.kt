package com.bignerdanch.android.criminalintent

import android.app.Application

class CriminalIntentApplication: Application() {

    override fun onCreate() { // подходящее место для разовой инициализации
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}