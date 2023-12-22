package com.example.engenieer.booking

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.engenieer.R

import com.example.engenieer.databinding.FragmentBookingBinding
import com.example.engenieer.helper.FirebaseHandler
import com.example.engenieer.helper.ToDoListener


class MyBookingRecyclerViewAdapter(
    private val values: List<BookingItem>,
    private val isAdmin: Boolean,
    private val eventListener: ToDoListener,
) : RecyclerView.Adapter<MyBookingRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentBookingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.roomName.text = item.roomName
        if (item.equipment == "nothing"){
            holder.equipmentName.text = item.roomName
            holder.icon.setImageResource(R.drawable.ic_whole_room)
        }else{
            holder.equipmentName.text = item.equipment
            holder.icon.setImageResource(R.drawable.ic_equipment)
        }
        holder.dateField.text = item.date
        holder.startHour.text = item.startHour
        holder.endHour.text = item.endHour
        if (item.ownerID == FirebaseHandler.Authentication.getUserUid() || isAdmin){
            holder.deleteButton.setOnClickListener { eventListener.onItemClick(position) }
        }else{
            holder.deleteButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val roomName: TextView = binding.roomName
        val equipmentName: TextView = binding.equipmentName
        val dateField: TextView = binding.dateField
        val startHour: TextView = binding.startHour
        val endHour: TextView = binding.endHour
        val deleteButton: ImageButton = binding.button
        val icon: ImageView = binding.icon

        override fun toString(): String {
            return super.toString() + " '" //+ contentView.text + "'"
        }
    }

}