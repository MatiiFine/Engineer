package com.example.engenieer.rooms

import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.example.engenieer.databinding.RoomFragmentItemBinding
import com.example.engenieer.helper.ToDoListener


class MyRoomRecyclerViewAdapter(
    private val values: List<RoomItem>,
    private val photos: ArrayList<Bitmap>,
    private val eventListener: ToDoListener,
    private val isAdmin: Boolean
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
        if(position<photos.size) holder.roomPicture.setImageBitmap(photos[position])
        holder.container.setOnClickListener { eventListener.onItemClick(position) }
        if (isAdmin){
            holder.container.setOnLongClickListener{
                eventListener.onItemLongClick(position)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: RoomFragmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val roomPicture: ImageView = binding.roomItemPicture
        val roomName: TextView = binding.roomItemName
        val shortDescription: TextView = binding.roomItemShortDescription
        val container: View = binding.root

        override fun toString(): String {
            return super.toString() + " '" //+ contentView.text + "'"
        }
    }

}