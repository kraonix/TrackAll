package com.example.trackall.ui.expenses

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackall.R
import com.example.trackall.data.TrackAllDatabase
import com.example.trackall.data.entity.Expense
import com.example.trackall.repository.ExpenseRepository
import com.example.trackall.util.SessionManager
import com.example.trackall.viewmodel.ExpenseViewModel
import com.example.trackall.viewmodel.ExpenseViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseDialogFragment : DialogFragment() {

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dialog_add_expense, null)

        val amountInput = view.findViewById<EditText>(R.id.amountInput)
        val descInput = view.findViewById<EditText>(R.id.descriptionInput)
        val dateInput = view.findViewById<EditText>(R.id.dateInput)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateInput.setText(dateFormat.format(calendar.time))

        dateInput.setOnClickListener {
            DatePickerDialog(requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    dateInput.setText(dateFormat.format(calendar.time))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        sessionManager = SessionManager(requireContext())
        val db = TrackAllDatabase.getDatabase(requireContext())
        val repository = ExpenseRepository(db.expenseDao())
        val factory = ExpenseViewModelFactory(repository)
        expenseViewModel = ViewModelProvider(this, factory)[ExpenseViewModel::class.java]

        builder.setView(view)
            .setTitle("Add Expense")
            .setPositiveButton("Save") { _, _ ->
                val amountText = amountInput.text.toString().trim()
                val description = descInput.text.toString().trim()
                val date = dateInput.text.toString().trim()
                val username = sessionManager.getLoggedInUsername()

                if (amountText.isEmpty() || description.isEmpty() || date.isEmpty() || username == null) {
                    Toast.makeText(context, "Fill all fields!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val amount = amountText.toDoubleOrNull()
                if (amount == null) {
                    Toast.makeText(context, "Invalid amount!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val expense = Expense(
                    username = username,
                    amount = amount,
                    description = description,
                    date = date
                )

                expenseViewModel.addExpense(expense)
                Toast.makeText(context, "Expense added!", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)

        return builder.create()
    }
}
