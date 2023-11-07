package com.example.engenieer.rooms

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.example.engenieer.databinding.RoomFragmentItemBinding


class MyRoomRecyclerViewAdapter(
    private val values: List<RoomItem>
) : RecyclerView.Adapter<MyRoomRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            RoomFragmentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.roomName.text = item.name
        holder.shortDescription.text = item.shortDescription
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: RoomFragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val roomPicture: ImageView = binding.roomItemPicture
        val roomName: TextView = binding.roomItemName
        val shortDescription: TextView = binding.roomItemShortDescription

        override fun toString(): String {
            return super.toString() + " '" //+ contentView.text + "'"
        }
    }

}