package com.bignerdanch.android.criminalintent

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar

private const val ARG_DATE = "date"


// ДИАЛОГОВОЕ ОКНО



class DatePickerFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        // Извлечение объекта Date, переданного от CrimeFragment
        val date = arguments?.getSerializable(ARG_DATE) as Date




        // Создание объекта календаря (редактора Date) для передачи в DatePickerDialog()
        val calendar = Calendar.getInstance().apply {
            time = date // «Календарь, возьми этот момент времени и разложи его на год, месяц, день, часы и минуты»
        }


        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)


        // Получаем дату, которую выбрал пользователь
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            // Оборачиваем дату в объект Date
            val resultDate = GregorianCalendar(year, month, dayOfMonth).time



            // ОТПРАВИТЕЛЬ - отправляем объект Date во FragmentManager
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY_DATE, // Ключ канала связи
                Bundle().apply { // Пакет данных, которые будут отправлены с ключом
                    putSerializable(RESULT_KEY_DATE, resultDate)
                }
            )
        }




        // Создание диалогового окна выбора даты
        return DatePickerDialog(
            requireContext(),
            dateListener, // Указываем слушателя
            initialYear,
            initialMonth,
            initialDay
        )
    }









    // Функция для получения аргументов при передачи объекта Date от CrimeFragment
    companion object {
        fun newInstance(date: Date): DatePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE, date)
            }
            return DatePickerFragment().apply {
                arguments = args
            }
        }
    }
}