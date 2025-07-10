package com.example.trackall

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ExpensesActivity : AppCompatActivity() {

    private lateinit var expenseNameEditText: EditText
    private lateinit var expenseAmountEditText: EditText
    private lateinit var addExpenseButton: Button
    private lateinit var expensesRecyclerView: RecyclerView

    private val expensesList = mutableListOf<String>()
    private lateinit var adapter: ExpensesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expenses)

        expenseNameEditText = findViewById(R.id.editExpenseName)
        expenseAmountEditText = findViewById(R.id.editExpenseAmount)
        addExpenseButton = findViewById(R.id.btnAddExpense)
        expensesRecyclerView = findViewById(R.id.recyclerExpenses)

        adapter = ExpensesAdapter(expensesList)
        expensesRecyclerView.layoutManager = LinearLayoutManager(this)
        expensesRecyclerView.adapter = adapter

        addExpenseButton.setOnClickListener {
            val name = expenseNameEditText.text.toString()
            val amount = expenseAmountEditText.text.toString()
            if (name.isNotEmpty() && amount.isNotEmpty()) {
                expensesList.add("$name - ₹$amount")
                adapter.notifyItemInserted(expensesList.size - 1)
                expenseNameEditText.text.clear()
                expenseAmountEditText.text.clear()
            }
        }
    }
}
