package com.bignerdanch.android.criminalintent

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.UUID

private const val TAG = "MainActivity"


class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Использование отступов от системных панелей
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.fragment_container)) { view, insets -> // Мы регистрируем слушатель для fragment_container
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        // Добавление фрагмента в activity
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            val fragment = CrimeListFragment.newInstance()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container,fragment)
                .commit()
        }


    }


    override fun onCrimeSelected(crimeId: UUID) {
        val fragment = CrimeFragment.newInstance(crimeId) // Создание экземпляра фрагмента
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment) // Замена фрагментов
            .addToBackStack(null) // Транзакция замены в обратный стек
            .commit()
    }
}