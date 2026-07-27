package com.bignerdanch.android.criminalintent.crime_fragment.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.bignerdanch.android.criminalintent.R
import com.bignerdanch.android.criminalintent.getScaledBitmap


class PhotoPickerFragment: DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        // Извлекаем, переданные данные
        val photoFilePath: String = arguments?.getSerializable(ARG_PHOTO_PATH) as String


        // Устанавливаем связь с объектом представления
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialogfragment_photo, null)
        val photoView: ImageView = view.findViewById(R.id.photoView)



        // Помещаем Bitmap в ImageView
        val bitmap = getScaledBitmap(photoFilePath, requireActivity())
        photoView.setImageBitmap(bitmap)







        // Строим AlertDialog
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setPositiveButton(android.R.string.ok, null)
            .create()

    }








    companion object {

        private const val ARG_PHOTO_PATH = "photo"

        fun newInstance(path: String): PhotoPickerFragment {
            val args = Bundle().apply {
                putSerializable(ARG_PHOTO_PATH, path)
            }
            return PhotoPickerFragment().apply {
                arguments = args
            }
        }

    }
}