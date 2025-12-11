package com.example.trackall.ui.bills

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.trackall.R
import com.example.trackall.data.TrackAllDatabase
import com.example.trackall.data.entity.Bill
import com.example.trackall.repository.BillRepository
import com.example.trackall.repository.ExpenseRepository
import com.example.trackall.util.SessionManager
import com.example.trackall.viewmodel.BillsViewModel
import com.example.trackall.viewmodel.BillsViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AddBillDialogFragment : DialogFragment() {

    private lateinit var billsViewModel: BillsViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add_bill, null)

        // Initialize Views
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val spinnerRecurrence = view.findViewById<Spinner>(R.id.spinnerRecurrence)
        val containerCustom = view.findViewById<View>(R.id.containerCustomRecurrence)
        val etCustomInterval = view.findViewById<EditText>(R.id.etCustomInterval)
        val spinnerCustomUnit = view.findViewById<Spinner>(R.id.spinnerCustomUnit)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val spinnerCategory = view.findViewById<Spinner>(R.id.spinnerCategory)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        // Setup Spinner Data
        val recurrenceOptions = arrayOf("One-Time", "Monthly", "Every 3 Months", "Every 9 Months", "Yearly", "Custom")
        val recurrenceMap = mapOf(
            "One-Time" to "ONE_TIME",
            "Monthly" to "MONTHLY",
            "Every 3 Months" to "EVERY_3_MONTHS",
            "Every 9 Months" to "EVERY_9_MONTHS",
            "Yearly" to "YEARLY",
            "Custom" to "CUSTOM"
        )
        spinnerRecurrence.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, recurrenceOptions)

        val unitOptions = arrayOf("Days", "Months")
        val unitMap = mapOf("Days" to "DAYS", "Months" to "MONTHS")
        spinnerCustomUnit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, unitOptions)

        val categoryOptions = arrayOf("Food", "Transport", "Entertainment", "Other", "Bills", "Rent", "Subscription")
        spinnerCategory.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categoryOptions)
        spinnerCategory.setSelection(4) // Default to "Bills"

        // Handle Recurrence Change
        spinnerRecurrence.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = recurrenceOptions[position]
                if (selected == "Custom") {
                    containerCustom.visibility = View.VISIBLE
                } else {
                    containerCustom.visibility = View.GONE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Handle Date Selection
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        tvDate.text = dateFormat.format(calendar.time)
        tvDate.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                calendar.set(year, month, day)
                tvDate.text = dateFormat.format(calendar.time)
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        // ViewModel Setup
        sessionManager = SessionManager(requireContext())
        val db = TrackAllDatabase.getDatabase(requireContext())
        val billRepo = BillRepository(db.billDao())
        val expenseRepo = ExpenseRepository(db.expenseDao())
        val factory = BillsViewModelFactory(billRepo, expenseRepo)
        billsViewModel = ViewModelProvider(this, factory)[BillsViewModel::class.java]

        // Save Logic
        btnSave.setOnClickListener {
            btnSave.isEnabled = false // Prevent double clicks
            val title = etTitle.text.toString().trim()
            val amountStr = etAmount.text.toString().trim()
            val username = sessionManager.getLoggedInUsername()

            if (title.isEmpty() || amountStr.isEmpty() || username == null) {
                Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull() ?: 0.0
            val recurrenceLabel = spinnerRecurrence.selectedItem.toString()
            val recurrenceMode = recurrenceMap[recurrenceLabel] ?: "ONE_TIME"
            val startDate = tvDate.text.toString()
            val category = spinnerCategory.selectedItem.toString()
            val notes = etNotes.text.toString()

            var customInterval = 0
            var customUnit: String? = null

            if (recurrenceMode == "CUSTOM") {
                val intervalStr = etCustomInterval.text.toString().trim()
                if (intervalStr.isEmpty()) {
                    Toast.makeText(context, "Enter custom interval", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                customInterval = intervalStr.toIntOrNull() ?: 0
                val unitLabel = spinnerCustomUnit.selectedItem.toString()
                customUnit = unitMap[unitLabel]
            }

            val bill = Bill(
                username = username,
                title = title,
                amount = amount,
                recurrenceMode = recurrenceMode,
                customInterval = customInterval,
                customIntervalUnit = customUnit,
                startDate = startDate,
                nextDueDate = startDate, // Initial next due date is the start date
                category = category,
                notes = notes
            )

            billsViewModel.addBill(bill)
            Toast.makeText(context, "Bill added", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        btnCancel.setOnClickListener { dismiss() }

        builder.setView(view)
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
}
