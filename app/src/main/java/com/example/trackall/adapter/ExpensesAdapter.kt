package com.example.trackall.ui.expenses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.trackall.data.entity.Expense
import com.example.trackall.databinding.ItemExpenseBinding

class ExpensesAdapter(
    private val onEditClick: (Expense) -> Unit,
    val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpensesAdapter.ExpenseViewHolder>() {


    private val expenses = mutableListOf<Expense>()


    fun submitList(newExpenses: List<Expense>) {
        expenses.clear()
        expenses.addAll(newExpenses)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount() = expenses.size

    inner class ExpenseViewHolder(private val binding: ItemExpenseBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(expense: Expense) {
            binding.amountText.text = "₹${expense.amount}"
            binding.descriptionText.text = expense.description

            binding.btnEdit.setOnClickListener {
                onEditClick(expense)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(expense)
            }
        }
    }
}


