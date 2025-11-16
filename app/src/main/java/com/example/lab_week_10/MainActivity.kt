package com.example.lab_week_10

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.database.TotalObject
import com.example.lab_week_10.viewmodels.TotalViewModel
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val db by lazy { prepareDatabase() }

    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prepareViewModel()
        initializeValueFromDatabase()
    }

    override fun onStart() {
        super.onStart()

        val total = db.totalDao().getTotal(ID).firstOrNull()
        total?.let {
            Toast.makeText(this, "Last updated: ${it.total.date}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()

        val currentValue = viewModel.total.value ?: 0
        val newDate = Date().toString()

        val updatedTotal = Total(
            id = ID,
            total = TotalObject(
                value = currentValue,
                date = newDate
            )
        )

        db.totalDao().update(updatedTotal)
    }

    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java, "total-database"
        ).allowMainThreadQueries().build()
    }

    private fun initializeValueFromDatabase() {
        val savedTotal = db.totalDao().getTotal(ID).firstOrNull()

        if (savedTotal == null) {
            // Insert default value
            val newTotal = Total(
                id = ID,
                total = TotalObject(
                    value = 0,
                    date = Date().toString()
                )
            )
            db.totalDao().insert(newTotal)
            viewModel.setTotal(0)
        } else {
            // Load value from DB ke ViewModel
            viewModel.setTotal(savedTotal.total.value)
        }
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        viewModel.total.observe(this) { total ->
            updateText(total)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    companion object {
        const val ID: Long = 1
    }
}
