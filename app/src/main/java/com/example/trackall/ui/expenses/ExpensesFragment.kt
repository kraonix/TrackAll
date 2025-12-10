package com.example.trackall.ui.expenses

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackall.R
import com.example.trackall.data.TrackAllDatabase
import com.example.trackall.data.entity.Expense
import com.example.trackall.repository.ExpenseRepository
import com.example.trackall.util.SessionManager
import com.example.trackall.viewmodel.ExpenseViewModel
import com.example.trackall.viewmodel.ExpenseViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.*
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar

class ExpensesFragment : Fragment() {

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var sessionManager: SessionManager
    private lateinit var expensesRecyclerView: RecyclerView
    private lateinit var weekRecyclerView: RecyclerView
    private lateinit var totalText: TextView
    private lateinit var monthYearText: TextView
    private lateinit var expensesAdapter: ExpensesAdapter
    private lateinit var weekAdapter: WeekAdapter

    private var allExpenses: List<Expense> = emptyList()
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_expenses, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val currentUser = sessionManager.getLoggedInUsername()

        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        // Initialize Views
        expensesRecyclerView = view.findViewById(R.id.expensesRecyclerView)
        weekRecyclerView = view.findViewById(R.id.weekRecyclerView)
        totalText = view.findViewById(R.id.monthlyTotalText)
        monthYearText = view.findViewById(R.id.monthYearText)
        val addExpenseFab = view.findViewById<FloatingActionButton>(R.id.addExpenseFab)

        // Setup for Week RecyclerView
        weekRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Setup for Expenses RecyclerView
        expensesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        expensesAdapter = ExpensesAdapter(
            onEditClick = { expense ->
                EditExpenseDialogFragment.newInstance(expense)
                    .show(parentFragmentManager, "EditExpenseDialog")
            },
            onDeleteClick = { expense -> expenseViewModel.deleteExpense(expense) }
        )
        expensesRecyclerView.adapter = expensesAdapter

        // Swipe to Delete
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val expense = expensesAdapter.getExpenseAt(position)
                expenseViewModel.deleteExpense(expense)

                Snackbar.make(view, "Expense deleted", Snackbar.LENGTH_LONG)
                    .setAction("UNDO") {
                        expenseViewModel.addExpense(expense)
                    }
                    .show()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(expensesRecyclerView)

        // ViewModel Setup
        val db = TrackAllDatabase.getDatabase(requireContext())
        val repository = ExpenseRepository(db.expenseDao())
        val viewModelFactory = ExpenseViewModelFactory(repository)
        expenseViewModel = ViewModelProvider(this, viewModelFactory)[ExpenseViewModel::class.java]

        expenseViewModel.getExpenses(currentUser).observe(viewLifecycleOwner) { expenses ->
            allExpenses = expenses
            updateUi()
        }

        monthYearText.setOnClickListener {
            showDatePickerDialog()
        }

        addExpenseFab.setOnClickListener {
            AddExpenseDialogFragment().show(parentFragmentManager, "AddExpenseDialog")
        }

        updateUi()
    }

    private fun updateUi() {
        updateWeekView()
        updateMonthlyTotal()
        filterExpensesByDate(selectedDate)
    }

    private fun showDatePickerDialog() {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, month)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateUi()
        }

        DatePickerDialog(
            requireContext(),
            listener,
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateWeekView() {
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearText.text = monthYearFormat.format(selectedDate.time)

        val weekDates = getWeekFromDate(selectedDate)

        if (!::weekAdapter.isInitialized) {
            weekAdapter = WeekAdapter(weekDates, selectedDate) { clickedDate ->
                // This logic runs when a day in the week selector is clicked
                selectedDate = clickedDate
                filterExpensesByDate(clickedDate)
                // Use the new, correct function to update the selection
                weekAdapter.updateSelectedDate(clickedDate)
            }
            weekRecyclerView.adapter = weekAdapter
        } else {
            // Use this when the DatePickerDialog sets a new week
            weekAdapter.setWeekData(weekDates, selectedDate)
        }
    }

    private fun getWeekFromDate(date: Calendar): List<Calendar> {
        val weekCalendar = date.clone() as Calendar
        weekCalendar.firstDayOfWeek = Calendar.SUNDAY
        weekCalendar.set(Calendar.DAY_OF_WEEK, weekCalendar.firstDayOfWeek)

        return List(7) {
            val day = weekCalendar.clone() as Calendar
            weekCalendar.add(Calendar.DAY_OF_MONTH, 1)
            day
        }
    }

    private fun updateMonthlyTotal() {
        val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
        val monthName = monthFormat.format(selectedDate.time)

        val total = allExpenses.filter { expense ->
            val expenseDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(expense.date)
            expenseDate?.let {
                val expenseCal = Calendar.getInstance().apply { time = it }
                expenseCal.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                        expenseCal.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR)
            } ?: false
        }.sumOf { it.amount }

        totalText.text = "$monthName's Total: ₹$total"
    }

    private fun filterExpensesByDate(dateToFilter: Calendar) {
        val filtered = allExpenses.filter { expense ->
            try {
                val expenseDateCal = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(expense.date)?.let {
                    Calendar.getInstance().apply { time = it }
                }
                expenseDateCal?.let {
                    it.get(Calendar.YEAR) == dateToFilter.get(Calendar.YEAR) &&
                            it.get(Calendar.DAY_OF_YEAR) == dateToFilter.get(Calendar.DAY_OF_YEAR)
                } ?: false
            } catch (e: Exception) {
                false
            }
        }
        expensesAdapter.submitList(filtered)
    }
}