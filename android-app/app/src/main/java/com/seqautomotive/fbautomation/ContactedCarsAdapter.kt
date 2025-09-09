package com.seqautomotive.fbautomation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ContactedCarsAdapter(
    private val onDeleteClick: (ContactedCar) -> Unit
) : ListAdapter<ContactedCar, ContactedCarsAdapter.ViewHolder>(ContactedCarDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contacted_car, parent, false)
        return ViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val carModelText: TextView = itemView.findViewById(R.id.car_model_text)
        private val sellerNameText: TextView = itemView.findViewById(R.id.seller_name_text)
        private val contactedTimeText: TextView = itemView.findViewById(R.id.contacted_time_text)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
        
        fun bind(car: ContactedCar) {
            carModelText.text = car.carModel
            sellerNameText.text = "Seller: ${car.sellerName}"
            
            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            contactedTimeText.text = dateFormat.format(Date(car.contactedAt))
            
            deleteButton.setOnClickListener {
                onDeleteClick(car)
            }
        }
    }
    
    class ContactedCarDiffCallback : DiffUtil.ItemCallback<ContactedCar>() {
        override fun areItemsTheSame(oldItem: ContactedCar, newItem: ContactedCar): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: ContactedCar, newItem: ContactedCar): Boolean {
            return oldItem == newItem
        }
    }
}