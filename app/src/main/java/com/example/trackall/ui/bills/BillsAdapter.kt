package com.example.trackall.ui.bills

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.trackall.R
import com.example.trackall.data.entity.Bill

class BillsAdapter(
    private val onEditClick: (Bill) -> Unit,
    private val onDeleteClick: (Bill) -> Unit
) : RecyclerView.Adapter<BillsAdapter.BillViewHolder>() {

    private var bills: List<Bill> = emptyList()

    fun setBills(bills: List<Bill>) {
        this.bills = bills
        notifyDataSetChanged()
    }
    
    fun getBillAt(position: Int): Bill {
        return bills[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bill, parent, false)
        return BillViewHolder(view)
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        holder.bind(bills[position])
    }

    override fun getItemCount(): Int = bills.size

    inner class BillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.tvBillTitle)
        private val amountText: TextView = itemView.findViewById(R.id.tvBillAmount)
        private val recurrenceText: TextView = itemView.findViewById(R.id.tvRecurrenceInfo)
        private val nextDueText: TextView = itemView.findViewById(R.id.tvNextDueDate)
        private val iconType: ImageView = itemView.findViewById(R.id.iconBillType)
        private val editBtn: View = itemView.findViewById(R.id.btnEdit)

        fun bind(bill: Bill) {
            titleText.text = bill.title
            amountText.text = "₹ ${bill.amount}"
            
            // Format Recurrence Text
            val recurrenceLabel = when (bill.recurrenceMode) {
                "ONE_TIME" -> "One-Time"
                "MONTHLY" -> "Monthly"
                "QUARTERLY" -> "Every 3 Months"
                "TRI_ANNUAL" -> "Every 4 Months"
                "YEARLY" -> "Yearly"
                "CUSTOM" -> "Every ${bill.customInterval} Days" // Simplified logic
                else -> bill.recurrenceMode
            }
            recurrenceText.text = recurrenceLabel
            
            // Format Next Due Text
            nextDueText.text = "Due: ${bill.nextDueDate}"
            
            // Icon Logic
            if (bill.recurrenceMode == "ONE_TIME") {
                iconType.setImageResource(R.drawable.ic_receipt)
            } else {
                iconType.setImageResource(R.drawable.ic_calendar)
            }

            editBtn.setOnClickListener { onEditClick(bill) }
        }
    }
}
