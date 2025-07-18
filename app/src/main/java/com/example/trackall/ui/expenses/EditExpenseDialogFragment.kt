package com.example.trackall.ui.expenses

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackall.R
import com.example.trackall.data.TrackAllDatabase
import com.example.trackall.data.entity.Expense
import com.example.trackall.repository.ExpenseRepository
import com.example.trackall.viewmodel.ExpenseViewModel
import com.example.trackall.viewmodel.ExpenseViewModelFactory

class EditExpenseDialogFragment : DialogFragment() {

    private lateinit var expenseViewModel: ExpenseViewModel

    companion object {
        private const val ARG_EXPENSE_ID = "expense_id"
        private const val ARG_EXPENSE_AMOUNT = "expense_amount"
        private const val ARG_EXPENSE_DESCRIPTION = "expense_description"
        private const val ARG_EXPENSE_DATE = "expense_date"
        private const val ARG_EXPENSE_USERNAME = "expense_username"

        fun newInstance(expense: Expense): EditExpenseDialogFragment {
            val fragment = EditExpenseDialogFragment()
            val args = Bundle().apply {
                putInt(ARG_EXPENSE_ID, expense.id)
                putDouble(ARG_EXPENSE_AMOUNT, expense.amount)
                putString(ARG_EXPENSE_DESCRIPTION, expense.description)
                putString(ARG_EXPENSE_DATE, expense.date)
                putString(ARG_EXPENSE_USERNAME, expense.username)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = requireActivity().layoutInflater.inflate(R.layout.edit_expense_dialog, null)

        val descriptionInput = view.findViewById<EditText>(R.id.editExpenseNameInput)
        val amountInput = view.findViewById<EditText>(R.id.editExpenseAmountInput)
        val updateButton = view.findViewById<Button>(R.id.btnUpdateExpense)

        val db = TrackAllDatabase.getDatabase(requireContext())
        val repository = ExpenseRepository(db.expenseDao())
        val viewModelFactory = ExpenseViewModelFactory(repository)
        expenseViewModel = ViewModelProvider(this, viewModelFactory)[ExpenseViewModel::class.java]

        val id = requireArguments().getInt(ARG_EXPENSE_ID)
        val oldAmount = requireArguments().getDouble(ARG_EXPENSE_AMOUNT)
        val oldDescription = requireArguments().getString(ARG_EXPENSE_DESCRIPTION)
        val oldDate = requireArguments().getString(ARG_EXPENSE_DATE)
        val username = requireArguments().getString(ARG_EXPENSE_USERNAME)

        descriptionInput.setText(oldDescription)
        amountInput.setText(oldAmount.toString())

        updateButton.setOnClickListener {
            val newAmount = amountInput.text.toString().toDoubleOrNull()
            val newDescription = descriptionInput.text.toString().trim()

            if (newAmount != null && newDescription.isNotEmpty()) {
                val updatedExpense = Expense(
                    id = id,
                    username = username ?: "",
                    amount = newAmount,
                    description = newDescription,
                    date = oldDate ?: ""
                )
                expenseViewModel.updateExpense(updatedExpense)
                dismiss()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setTitle("Edit Expense")
            .create()
    }
}
