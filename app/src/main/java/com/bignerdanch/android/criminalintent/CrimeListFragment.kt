package com.bignerdanch.android.criminalintent

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "CrimeListFragment"

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


    // Определение контейнера для представления через внутренний класс, расширяемый от RecyclerView.ViewHolder
    private inner class CrimeHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener { // Наследуешь интерфейс OnClickListener, так как новый класс не знает о нём

        private lateinit var crime: Crime

        private val titleTextView: TextView = itemView.findViewById(R.id.crime_title) // itemView — это корневой View, переданный в конструктор
        private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)

        // Слушатель нажатий - вызывается onClick() у переданного объекта (CrimeHolder)
        init {
            itemView.setOnClickListener(this)
        }

        // Привязываем данные к представлению ViewHolder
        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = this.crime.title
            dateTextView.text = this.crime.date.toString()
        }

        override fun onClick(v:View) {
            Toast.makeText(context, "${crime.title} pressed!", Toast.LENGTH_SHORT).show()
        }

    }


    // Шаблон адаптера
    private inner class CrimeAdapter(var crimes: List<Crime>): RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun getItemCount() = crimes.size

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            // Ищем данные "объектной модели" для этой позиции - данные конкретного элемента списка
            val crime = crimes[position]
            // Передаем элемент списка в холдер
            holder.bind(crime)
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