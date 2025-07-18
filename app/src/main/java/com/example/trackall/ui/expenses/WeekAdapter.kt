package com.example.trackall.ui.expenses

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.trackall.R
import com.example.trackall.databinding.WeekItemBinding
import java.text.SimpleDateFormat
import java.util.*

class WeekAdapter(
    private var dates: List<Calendar>,
    private var selectedDate: Calendar,
    private val onDateClick: (Calendar) -> Unit
) : RecyclerView.Adapter<WeekAdapter.WeekViewHolder>() {

    inner class WeekViewHolder(val binding: WeekItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: Calendar) {
            val context = binding.root.context
            val dayFormat = SimpleDateFormat("d", Locale.getDefault())
            binding.dateText.text = dayFormat.format(date.time)

            val isSelected = isSameDay(date, selectedDate)

            if (isSelected) {
                binding.dateText.setTextColor(Color.WHITE)
                binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.date_selected))
            } else {
                binding.dateText.setTextColor(Color.BLACK)
                binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.date_unselected))
            }

            binding.root.setOnClickListener {
                onDateClick(date)
            }
        }

        private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        val binding = WeekItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeekViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        holder.bind(dates[position])
    }

    override fun getItemCount() = dates.size

    /**
     * Use this when the entire week's data needs to be replaced.
     */
    fun setWeekData(newDates: List<Calendar>, newSelectedDate: Calendar) {
        dates = newDates
        selectedDate = newSelectedDate
        notifyDataSetChanged()
    }

    /**
     * Use this when only the selected date highlight needs to be updated.
     */
    fun updateSelectedDate(newSelectedDate: Calendar) {
        selectedDate = newSelectedDate
        notifyDataSetChanged()
    }
}