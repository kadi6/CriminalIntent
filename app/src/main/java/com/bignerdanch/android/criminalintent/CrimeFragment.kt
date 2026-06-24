package com.bignerdanch.android.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.util.Date
import java.util.UUID

private const val ARG_CRIME_ID = "crime_id"
private const val TAG = "CrimeFragment"
private const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME = "DialogTime"

const val REQUEST_KEY_DATE = "request_key_date"
const val RESULT_KEY_DATE = "result_key_date"

const val REQUEST_KEY_TIME = "request_key_time"
const val RESULT_KEY_TIME = "result_key_time"


class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var timePicker: Button

    private val crimeViewModel: CrimeViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"CrimeFragment onCreate()")

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
        timePicker = view.findViewById(R.id.crime_time) as Button



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

        // ПОЛУЧАТЕЛЬ - получаем данные от FragmentManager - регистрируем слушателя
        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_DATE, // Получаем данные по ключу
            viewLifecycleOwner // Пока данный объект находится в активном состоянии, слушай результаты
        ) { _, bundle -> // Контейнер с переданными данными

            val selectedDate = bundle.getSerializable(RESULT_KEY_DATE) as Date // Извлекаем значения из bundle
            crime.date = selectedDate
            updateUI()
        }



        parentFragmentManager.setFragmentResultListener(
            REQUEST_KEY_TIME,
            viewLifecycleOwner
        ) { _, bundle ->

            val selectedTime = bundle.getSerializable(RESULT_KEY_TIME) as Date
            crime.date = selectedTime
            updateUI()
        }
    }








    // Добавление слушателя происходит в onStart()
    // Фрагмент становится видимым для пользователя
    override fun onStart() {
        super.onStart()
        Log.d(TAG,"CrimeFragment onStart()")


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

        dateButton.apply {
            setOnClickListener {
                // Передаем объект Date в аргументы DialogPickerFragment
                DatePickerFragment.newInstance(crime.date).apply {
                    // Отображаем и добавляем DatePickerFragment во FragmentManager
                    show(this@CrimeFragment.parentFragmentManager, DIALOG_DATE)
                }

            }
        }


        timePicker.apply {
            setOnClickListener {

                TimePickerFragment.newInstance(crime.date).apply {

                    show(this@CrimeFragment.parentFragmentManager, DIALOG_TIME)
                }
            }
        }
    }









    override fun onResume() {
        super.onResume()
        Log.d(TAG, "CrimeFragment onResume")
    }


    override fun onPause() {
        super.onPause()
        Log.d(TAG,"CrimeFragment onPause()")
    }


    // Сохраняем изменения в объекте каждый раз когда фрагмент переходит в onStop()
    override fun onStop() {
        super.onStop()
        Log.d(TAG,"CrimeFragment onStop()")
        crimeViewModel.saveCrime(crime)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"CrimeFragment onDestroy()")
    }












    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        timePicker.text = crime.date.toString()
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