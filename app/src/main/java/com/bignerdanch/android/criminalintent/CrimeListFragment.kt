package com.bignerdanch.android.criminalintent

import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID

// Constant Pattern — когда «магические числа» заменяются именованными константами. Это улучшает читаемость

private const val TAG = "CrimeListFragment"

// Коды типов строк (Type Code — когда целое число представляет некий тип)
private const val TYPE_ORDINARY = 0
private const val TYPE_HARD = 1



class CrimeListFragment : Fragment() {


    /**
     * Требуемый интерфейс
     */
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    // Свойство, чтобы удерживать объект, реализующий Callbacks
    private var callbacks: Callbacks? = null


    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = null
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var addCrimeButton: Button



    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }





    // Установка свойства callbacks
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks? // Мы требуем, чтобы Activity реализовывала наш интерфейс Callbacks


    }


    // ЖИЗНЕННЫЙ ЦИКЛ ПРЕДСТАВЛЕНИЯ VIEW 
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)

        // Создание объекта представления - умного контейнера RecyclerView
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
        // Настройка объекта
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)

        emptyStateLayout = view.findViewById(R.id.empty_state_layout)
        addCrimeButton = view.findViewById(R.id.add_crime_button)


        adapter = CrimeAdapter()
        crimeRecyclerView.adapter = adapter


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Получаем MenuHost (activity, внутри которой находится данный фрагмент)
        val menuHost: MenuHost = requireActivity()

        // 2. Добавляем провайдер меню
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Раздуваем меню
                menuInflater.inflate(R.menu.fragment_crime_list, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Обработка кликов
                return when (menuItem.itemId) {
                    R.id.new_crime -> {
                        val crime = Crime()
                        crimeListViewModel.addCrime(crime)
                        callbacks?.onCrimeSelected(crime.id)
                        true
                    }
                    else -> false
                }
            }
        },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED // Меню живет только когда фрагмент виден
        )



        addCrimeButton.setOnClickListener {
            val crime = Crime()
            crimeListViewModel.addCrime(crime)
            callbacks?.onCrimeSelected(crime.id)
        }



        // Вешаем наблюдателя за LiveData
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            Observer { crimes -> // В crimes приходит ссылка на список от LiveData
                crimes?.let {
                    Log.i(TAG, "Got crimes ${crimes.size}")
                    updateUI(crimes)
                }
            })
    }

    // Когда фрагмент отсоединяется, ссылка на Activity больше недействительна
    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }



    // Создаем CrimeAdapter и устанавливаем его на RecyclerView
    private fun updateUI(crimes: List<Crime>) {
        adapter?.submitList(crimes)

        if (crimes.isEmpty()) {
            crimeRecyclerView.visibility = View.GONE
            emptyStateLayout.visibility = View.VISIBLE
        } else {
            crimeRecyclerView.visibility = View.VISIBLE
            emptyStateLayout.visibility = View.GONE
        }
    }


    // Шаблон первого Holder
    private inner class CrimeHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener { // Наследуем интерфейс OnClickListener - Этот класс сам будет слушателем нажатий

        // Создаём объект списка для использования его данных
        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title) // itemView — это корневой View, переданный в конструктор
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

        // Слушатель нажатий - вызывается onClick() у переданного объекта (CrimeHolder)
        init {
            itemView.setOnClickListener(this) // Передает сам CrimeHolder как слушатель
        }

        // Привязываем данные к представлению ViewHolder
        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title

            // Форматируем дату
            dateTextView.text = DateFormat.format("EEEE, MMM dd, yyyy", this.crime.date)

            // Устанавливаем видимость для картинки
            solvedImageView.visibility = if(crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v:View) {
            callbacks?.onCrimeSelected(crime.id)
        }

    }



    // Шаблон второго Holder
    private inner class HardCrimeHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
        private val policeButton: Button = itemView.findViewById(R.id.contact_police)

        init {
            itemView.setOnClickListener(this)
        }



        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = DateFormat.format("EEEE, MMM dd, yyyy", this.crime.date)
            policeButton.setOnClickListener{ // Для
                Toast.makeText(context, "Police was called for ${crime.title}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onClick(v: View?) {
            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
        }


    }



    // Шаблон Адаптера
    private inner class CrimeAdapter:  ListAdapter<Crime, RecyclerView.ViewHolder>(CrimeDiffCallback()) {


        // Определяет по позиции, какой «тип» у данного элемента, и возвращает целое число — код этого типа
        override fun getItemViewType(position: Int): Int {
            val crime = getItem(position)
            if(crime.requiresPolice) {
                return TYPE_HARD
            } else {
                return TYPE_ORDINARY
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            // when предпочтительный способ для множественного выбора
            return when(viewType) {
                TYPE_HARD -> {
                    val view = layoutInflater.inflate(R.layout.list_item_hard_crime, parent, false)
                    HardCrimeHolder(view)
                }
                else -> {
                    val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
                    CrimeHolder(view)
                }
            }
        }





        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            // Ищем данные "объектной модели" для этой позиции - данные конкретного элемента списка
            val crime = getItem(position)
            // Передаем элемент списка в холдер
            when (holder) {
                // Проверка типа (TYPE CHECK) и приведение типа (SMART CAST) - Узнаем кто именно прилетел, и приводим его к нужному типу
                is HardCrimeHolder -> holder.bind(crime)
                is CrimeHolder -> holder.bind(crime)
            }
        }


    }



    private class CrimeDiffCallback : DiffUtil.ItemCallback<Crime>() {

        // Проверяем, один и тот же ли это объект
        override fun areItemsTheSame(
            oldItem: Crime,
            newItem: Crime
        ): Boolean {

            return oldItem.id == newItem.id
        }

        // Проверяем, изменилось ли содержимое
        override fun areContentsTheSame(
            oldItem: Crime,
            newItem: Crime
        ): Boolean {

            return oldItem == newItem
        }
    }













    override fun onStart() {
        super.onStart()
        Log.d(TAG,"CrimeListFragment onStart()")
    }

    override fun onResume() {
        Log.d(TAG, "CrimeListFragment onResume()")
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "CrimeListFragment onPause()")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG,"CrimeListFragment onStop()")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"CrimeListFragment onDestroy()")
    }






    // Функция для получения экземпляра фрагмента
    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }
}