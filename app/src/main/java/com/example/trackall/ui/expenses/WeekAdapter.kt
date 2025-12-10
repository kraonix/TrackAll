package com.example.trackall.ui.expenses

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
            val isWeekend = date.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || 
                            date.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY

            if (isSelected) {
                binding.dateText.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary))
                binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.date_selected))
                
                // Scale Animation
                binding.root.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
            } else {
                binding.root.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                
                if (isWeekend) {
                    binding.dateText.setTextColor(ContextCompat.getColor(context, R.color.colorTeal))
                    binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorInputBg))
                } else {
                    binding.dateText.setTextColor(ContextCompat.getColor(context, R.color.colorTextPrimary))
                    binding.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.date_unselected))
                }
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

    fun setWeekData(newDates: List<Calendar>, newSelectedDate: Calendar) {
        dates = newDates
        selectedDate = newSelectedDate
        notifyDataSetChanged()
    }

    fun updateSelectedDate(newSelectedDate: Calendar) {
        selectedDate = newSelectedDate
        notifyDataSetChanged()
    }
}