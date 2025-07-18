package com.example.trackall.ui.expenses

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.trackall.R
import com.example.trackall.databinding.WeekItemBinding // We can reuse the same item layout
import java.text.SimpleDateFormat
import java.util.*

class CalendarAdapter(
    private var days: List<Calendar?>,
    private var selectedDate: Calendar,
    private val onDateClick: (Calendar) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    inner class CalendarViewHolder(val binding: WeekItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(day: Calendar?) {
            if (day == null) {
                // This is an empty cell before the 1st of the month
                binding.dateText.text = ""
                binding.root.visibility = View.INVISIBLE // Hide the cell
            } else {
                binding.root.visibility = View.VISIBLE
                val dayFormat = SimpleDateFormat("d", Locale.getDefault())
                binding.dateText.text = dayFormat.format(day.time)

                // Check if this date is the selected one
                val isSelected = (day.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) &&
                        day.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR))

                // Style the cell based on selection
                if (isSelected) {
                    binding.dateText.setTextColor(Color.WHITE)
                    binding.cardView.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.date_selected))
                } else {
                    binding.dateText.setTextColor(Color.BLACK)
                    binding.cardView.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, R.color.date_unselected))
                }

                binding.root.setOnClickListener {
                    selectedDate = day
                    onDateClick(day)
                    // We need to notify the adapter that the data has changed to redraw selections
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = WeekItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(days[position])
    }

    override fun getItemCount(): Int = days.size

    /**
     * A helper function to update the calendar's data when the month or year changes.
     */
    fun updateData(newDays: List<Calendar?>, newSelectedDate: Calendar) {
        days = newDays
        selectedDate = newSelectedDate
        notifyDataSetChanged()
    }
}