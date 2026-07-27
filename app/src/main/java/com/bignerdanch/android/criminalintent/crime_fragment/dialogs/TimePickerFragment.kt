package com.bignerdanch.android.criminalintent.crime_fragment.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.bignerdanch.android.criminalintent.crime_fragment.REQUEST_KEY_TIME
import com.bignerdanch.android.criminalintent.crime_fragment.RESULT_KEY_TIME
import java.util.Calendar
import java.util.Date

private const val ARG_DATE_2 = "date_2"




class TimePickerFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {



        val date = arguments?.getSerializable(ARG_DATE_2) as Date



        val calendar = Calendar.getInstance().apply {
            time = date
        }



        val initialHour = calendar.get(Calendar.HOUR_OF_DAY)
        val initialMinute = calendar.get(Calendar.MINUTE)


        val timeListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->

            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)

            val resultTime = calendar.time

            parentFragmentManager.setFragmentResult(
                REQUEST_KEY_TIME,
                Bundle().apply {
                    putSerializable(RESULT_KEY_TIME, resultTime)
                }
            )
        }





        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHour,
            initialMinute,
            true
        )
    }










    companion object {
        fun newInstance(date: Date): TimePickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_DATE_2, date)
            }
            return TimePickerFragment().apply {
                arguments = args
            }
        }
    }
}