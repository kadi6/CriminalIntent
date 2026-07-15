package com.bignerdanch.android.criminalintent

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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

private const val DATE_FORMAT = "EEE, MMM, dd"

private const val REQUEST_CONTACT = 1



class CrimeFragment : Fragment() {

    private lateinit var crime: Crime
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var timePicker: Button
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var callButton: Button


    private val crimeViewModel: CrimeViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"CrimeFragment onCreate()")

        // Получаем ссылку на объект модели
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
        reportButton = view.findViewById(R.id.send_crime_report) as Button
        suspectButton = view.findViewById(R.id.choose_suspect) as Button
        callButton = view.findViewById(R.id.call) as Button



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






        reportButton.setOnClickListener {
            // Создаём и настраиваем  implicit intent
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.crime_report_subject))
            }.also { intent ->
                // Создаем обязательное предоставление выбора activity приложений, которые работают с нашим intent
                val chooserIntent =
                    Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }






        suspectButton.apply {

            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI) // описание того, с каким набором данных должна работать запускаемая Activity
            // Запускаем activity по переданному intent для возвращения результата
            setOnClickListener {
                startActivityForResult(pickContactIntent, REQUEST_CONTACT)
            }

            // Проверка реагирующих activities
            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(
                    pickContactIntent,
                    PackageManager.MATCH_DEFAULT_ONLY
                )
            if (resolvedActivity == null) {
                isEnabled = false
            }
        }





        callButton.setOnClickListener {

            // Проверяем дал ли пользователь разрешение
            val permissionGranted =
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED

            if (permissionGranted) {
                dialSuspect()
            } else {
                requestPermissionLauncher.launch(       // Показ системного окна разрешения (асинхронный процесс)
                    Manifest.permission.READ_CONTACTS
                )
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












    // Функция, которая обновляет пользовательский интерфейс данными
    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        timePicker.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState() // Android Немедленно перейди в конечное состояние без проигрывания анимации
        }
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
    }



    // Эта функция вызывается перед onViewCreated( ) - ОБРАБАТЫВАЕМ РЕЗУЛЬТАТ
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_CONTACT && data != null -> {

                // Сохраняем в переменную значение свойства data переданного интента от другой activity
                val contactUri: Uri = data.data ?: return

                // Указываем, какой столбец мы хотим получить
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID)

                val cursor = requireActivity().contentResolver
                    .query(contactUri, queryFields, null, null, null)

                cursor?.use { // после окончания работы Cursor обязательно нужно закрыть

                    // проверка того, что возвращенный курсор содержит хотя бы одну строку
                    if (it.count == 0) {
                        return
                    }

                    it.moveToFirst()
                    val suspect = it.getString(0)
                    val suspectId = it.getString(1)
                    crime.suspect = suspect
                    crime.suspectId = suspectId
                    crimeViewModel.saveCrime(crime)
                    suspectButton.text = suspect
                }
            }
        }
    }





    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        var suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(R.string.crime_report,
            crime.title, dateString, solvedString, suspect)
    }


    // Создаем ActivityResultLauncher
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()     // Contract
        ) { isGranted ->        // Callback

            if (isGranted) {
                dialSuspect()
            }
        }




    private fun dialSuspect() {

        val contactId =
            crime.suspectId ?: return

        val queryFields = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val selection =
            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"

        val selectionArgs =
            arrayOf(contactId)

        val cursor = requireActivity()
            .contentResolver
            .query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,     // Из какой таблицы или набора данных читать
                queryFields,    // Какие столбцы вернуть
                selection,  // По каким условиям искать строки WHERE CONTACT_ID = ?
                selectionArgs,  // значения для ?
                null
            )
        // Найди среди телефонных записей строки, у которых CONTACT_ID равен 42, и верни из них колонку NUMBER



        cursor?.use {
            if (it.count == 0) {
                return
            }

            it.moveToFirst()

            val phoneNumber = it.getString(0)

            startActivity(
                Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:$phoneNumber")
                )
            )
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