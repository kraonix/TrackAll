package com.example.trackall

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.trackall.data.TrackAllDatabase
import com.example.trackall.databinding.ActivityExpensesBinding
import com.example.trackall.repository.ExpenseRepository
import com.example.trackall.ui.expenses.EditExpenseDialogFragment
import com.example.trackall.ui.expenses.ExpensesAdapter
import com.example.trackall.util.SessionManager
import com.example.trackall.viewmodel.ExpenseViewModel
import com.example.trackall.viewmodel.ExpenseViewModelFactory

class ExpensesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpensesBinding
    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var adapter: ExpensesAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        val currentUser = sessionManager.getLoggedInUsername()

        if (currentUser == null) {
            finish()
            return
        }

        val db = TrackAllDatabase.getDatabase(this)
        val repository = ExpenseRepository(db.expenseDao())
        val factory = ExpenseViewModelFactory(repository)
        expenseViewModel = ViewModelProvider(this, factory)[ExpenseViewModel::class.java]

        adapter = ExpensesAdapter(
            onEditClick = { expense ->
                EditExpenseDialogFragment.newInstance(expense)
                    .show(supportFragmentManager, "EditExpenseDialog")
            },
            onDeleteClick = { expense ->
                expenseViewModel.deleteExpense(expense)
            }
        )

        binding.recyclerExpenses.layoutManager = LinearLayoutManager(this)
        binding.recyclerExpenses.adapter = adapter

        expenseViewModel.getExpenses(currentUser).observe(this) { expenses ->
            adapter.submitList(expenses)
        }

        binding.btnAddExpense.setOnClickListener {
            // AddExpenseDialogFragment or expense form
        }
    }
}
