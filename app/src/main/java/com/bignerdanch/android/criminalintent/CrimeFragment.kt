package com.bignerdanch.android.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.util.UUID

private const val ARG_CRIME_ID = "crime_id"
private const val TAG = "CrimeFragment"


class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox

    private val crimeViewModel: CrimeViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
        // Получение аргументов
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID

        Log.d(TAG, "args bundle crime ID: $crimeId")

        // Передача идентификатора во ViewModel
        crimeViewModel.loadCrime(crimeId)
    }

    // Заполнение представления фрагмента происходит в onCreateView()
    // Фрагмент ещё не виден пользователю
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Получение корневого View - объекта макета
        val view = inflater.inflate(R.layout.fragment_crime, container, false)

        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox

        // Устанавливаем статические свойства
        dateButton.apply {
            text = crime.date.toString()
            isEnabled = false
        }

        return view

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            }
        )
    }




    // Добавление слушателя происходит в onStart()
    // Фрагмент становится видимым для пользователя
    override fun onStart() {
        super.onStart()


        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // Это пространство оставлено пустым специально
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // И это
            }
        }

        titleField.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
    }


    // Сохраняем изменения в объекте каждый раз когда фрагмент переходит в onStop()
    override fun onStop() {
        super.onStop()
        crimeViewModel.saveCrime(crime)
    }



    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState() // Android Немедленно перейди в конечное состояние без проигрывания анимации
        }
    }


    companion object {
        // Статическая функция - Инкапсуляция создания фрагмента
        fun newInstance(crimeId: UUID): CrimeFragment {
            // Создание аргументов фрагмента
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            // Присоединение аргументов фрагменту
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}