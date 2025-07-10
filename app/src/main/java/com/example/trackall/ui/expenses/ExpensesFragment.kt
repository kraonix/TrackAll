package com.example.trackall.ui.expenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.trackall.R
import com.example.trackall.data.TrackAllDatabase
import com.example.trackall.repository.ExpenseRepository
import com.example.trackall.util.SessionManager
import com.example.trackall.viewmodel.ExpenseViewModel
import com.example.trackall.viewmodel.ExpenseViewModelFactory
import kotlinx.coroutines.launch

class ExpensesFragment : Fragment() {

    private lateinit var expenseViewModel: ExpenseViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_expenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = TrackAllDatabase.getDatabase(requireContext())
        val repository = ExpenseRepository(db.expenseDao())
        val viewModelFactory = ExpenseViewModelFactory(repository)
        expenseViewModel = ViewModelProvider(this, viewModelFactory).get(ExpenseViewModel::class.java)

        sessionManager = SessionManager(requireContext())
        val currentUser = sessionManager.getLoggedInUsername()

        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        // Now safely fetch this user's expenses
        expenseViewModel.getExpenses(currentUser).observe(viewLifecycleOwner) { expenses ->
            // TODO: Bind expenses to your RecyclerView adapter
            // e.g., adapter.submitList(expenses)
        }
    }
}
