package com.example.lab_week_10

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.viewmodels.TotalViewModel
import com.example.lab_week_10.database.Total

class MainActivity : AppCompatActivity() {
    // Create database instance lazily
    private val db by lazy { prepareDatabase() }

    // ViewModel
    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        prepareViewModel()

        // Initialize the value of the total from the database
        initializeValueFromDatabase()
    }

    override fun onPause() {
        super.onPause()
        db.totalDao().update(Total(ID, viewModel.total.value!!))
    }

    // Create and build the TotalDatabase with the name 'total-database'
    // allowMainThreadQueries() is used to allow queries to be run on the main thread (not recommended in production)
    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java, "total-database"
        ).allowMainThreadQueries().build()
    }

    // Initialize the value of the total from the database
    private fun initializeValueFromDatabase() {
        val total = db.totalDao().getTotal(ID)
        if (total.isEmpty()) {
            db.totalDao().insert(Total(id = 1, total = 0))
        } else {
            viewModel.setTotal(total.first().total)
        }
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        // Observe the LiveData object
        viewModel.total.observe(this) { total ->
            updateText(total)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    // The ID of the Total object in the database
    // For simplicity, we only have one Total object in the database
    // So the ID is always 1
    companion object {
        const val ID: Long = 1
    }
}