package com.bignerdanch.android.criminalintent

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Constant Pattern  — когда «магические числа» заменяются именованными константами. Это улучшает читаемость

private const val TAG = "CrimeListFragment"

// Коды типов строк (Type Code — когда целое число представляет некий тип)
private const val TYPE_ORDINARY = 0
private const val TYPE_HARD = 1



class CrimeListFragment : Fragment() {

    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter: CrimeAdapter? = null



    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total crimes: ${crimeListViewModel.crimes.size}")
    }


    // Функция для получения экземпляра фрагмента
    companion object {
        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }


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

        updateUI()

        return view
    }


    // Создаем CrimeAdapter и устанавливаем его на RecyclerView
    private fun updateUI() {
        val crimes = crimeListViewModel.crimes
        adapter = CrimeAdapter(crimes)
        crimeRecyclerView.adapter = adapter
    }


    // Шаблон первого Холдера
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

            solvedImageView.visibility = if(crime.isSolved) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        override fun onClick(v:View) {
            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
        }

    }



    // Шаблон второго Холдера
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
            dateTextView.text = this.crime.date.toString()
            policeButton.setOnClickListener{ // Для
                Toast.makeText(context, "Police was called for ${crime.title}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onClick(v: View?) {
            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
        }


    }



    // Шаблон Адаптера
    private inner class CrimeAdapter(var crimes: List<Crime>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


        // Определяет по позиции, какой «тип» у данного элемента, и возвращает целое число — код этого типа
        override fun getItemViewType(position: Int): Int {
            val crime = crimes[position]
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


        override fun getItemCount() = crimes.size


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            // Ищем данные "объектной модели" для этой позиции - данные конкретного элемента списка
            val crime = crimes[position]
            // Передаем элемент списка в холдер
            when (holder) {
                // Проверка типа (TYPE CHECK) и приведение типа (SMART CAST) - Узнаем кто именно прилетел, и приводим его к нужному типу
                is HardCrimeHolder -> holder.bind(crime)
                is CrimeHolder -> holder.bind(crime)
            }
        }


    }













    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}