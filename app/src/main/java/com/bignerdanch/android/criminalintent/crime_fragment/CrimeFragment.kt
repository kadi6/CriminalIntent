package com.bignerdanch.android.criminalintent.crime_fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bignerdanch.android.criminalintent.database.Crime
import com.bignerdanch.android.criminalintent.crime_fragment.dialogs.DatePickerFragment
import com.bignerdanch.android.criminalintent.crime_fragment.dialogs.PhotoPickerFragment
import com.bignerdanch.android.criminalintent.R
import com.bignerdanch.android.criminalintent.crime_fragment.dialogs.TimePickerFragment
import com.bignerdanch.android.criminalintent.getScaledBitmap
import java.io.File
import java.util.Date
import java.util.UUID

private const val ARG_CRIME_ID = "crime_id"
private const val TAG = "CrimeFragment"
private const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME = "DialogTime"
private const val DIALOG_PHOTO = "DialogPhoto"

const val REQUEST_KEY_DATE = "request_key_date"
const val RESULT_KEY_DATE = "result_key_date"

const val REQUEST_KEY_TIME = "request_key_time"
const val RESULT_KEY_TIME = "result_key_time"

private const val DATE_FORMAT = "EEE, MMM, dd"

private const val REQUEST_CONTACT = 1
private const val REQUEST_PHOTO = 2





class CrimeFragment : Fragment() {



    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri


    private lateinit var observer: ViewTreeObserver     // слушатель layout-прохода


    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var timePicker: Button
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var callButton: Button
    private lateinit var photoView: ImageView
    private lateinit var photoButton: ImageButton






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






    // Создание ссылок на объекты представления фрагмента происходит в onCreateView()
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
        photoView = view.findViewById(R.id.crime_photo) as ImageView
        photoButton = view.findViewById(R.id.crime_camera) as ImageButton



        return view
    }











    // Здесь мы делаем updateUI - обновляем пользовательский интерфейс каждый раз, когда создается представление фрагмента
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Здесь мы получаем обновленные данные из базы данных
        crimeViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    photoFile = crimeViewModel.getPhotoFile(crime)
                    photoUri = FileProvider.getUriForFile(      // Функция, которая преобразует local path to file в Uri
                        requireActivity(),
                        "com.bignerdranch.android.criminalintent.fileprovider",
                        photoFile
                    )


                    updateUI()
                    tryUpdatePhotoView()


                }
            }
        )







        // Проверка layout-прохода

        // Объект, через который регистрируются слушатели дерева View
        observer = photoView.viewTreeObserver

        // Создаем слушателя для ImageView
        observer.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {

                override fun onGlobalLayout() {

                    photoView.viewTreeObserver
                        .removeOnGlobalLayoutListener(this)     // Удаляем слушатель

                    tryUpdatePhotoView()
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







        // Запускаем приложение камеры для последующего сохранения снимка в файл нашего приложения

        photoButton.apply {

            val packageManager: PackageManager = requireActivity().packageManager

            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)  // Создаём запрос на создание снимка

            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }


            setOnClickListener {

                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)    // Передаем место сохранения фотографии

                val cameraActivities: List<ResolveInfo> =
                    packageManager.queryIntentActivities(captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY)

                for (cameraActivity in cameraActivities) {      // Даём разрешение на запись в файл нашего приложения
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }

                startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }






        photoView.apply {

            setOnClickListener {
                    if (photoFile.exists()) {
                        PhotoPickerFragment.newInstance(photoFile.path)
                            .show(this@CrimeFragment.parentFragmentManager, DIALOG_PHOTO)
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









    // Страховочный отзыв разрешения при откреплении фрагмента от Activity
    override fun onDetach() {

        requireActivity().revokeUriPermission(
            photoUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        )

        super.onDetach()
    }







    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"CrimeFragment onDestroy()")
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




            requestCode == REQUEST_PHOTO -> {   // Отзываем разрешение
                requireActivity().revokeUriPermission(
                    photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                tryUpdatePhotoView()

            }
        }
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












    // Функция, которая обновляет пользовательский интерфейс данными, текущего преступления
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
        return getString(
            R.string.crime_report,
            crime.title, dateString, solvedString, suspect)
    }











    // Делаем проверку для загрузки изображения в ImageView
    private fun tryUpdatePhotoView() {


        // Был ли инициализирован photoFile - была ли загрузка данных LiveData
        if(!::photoFile.isInitialized) {
            return
        }

        val destWidth = photoView.width
        val destHeight = photoView.height

        if(destWidth == 0 || destHeight == 0) {
            return
        }

        // После всех проверок, безопасно обновляем ImageView
        updatePhotoView(destWidth, destHeight)
    }




    // Загружаем Bitmap в ImageView
    private fun updatePhotoView(destWidth: Int, destHeight: Int) {
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, destWidth, destHeight)     // Создаем Bitmap
            photoView.setImageBitmap(bitmap)       // Загружаем Bitmap
        } else {
            photoView.setImageDrawable(null)
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