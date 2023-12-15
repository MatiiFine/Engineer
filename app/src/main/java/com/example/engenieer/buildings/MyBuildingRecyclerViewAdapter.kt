package com.example.engenieer.buildings

import android.graphics.Bitmap
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.engenieer.R
import com.example.engenieer.databinding.FragmentBuildingBinding
import com.example.engenieer.helper.ToDoListener

class MyBuildingRecyclerViewAdapter(
    private val values: List<BuildingItem>,
    private val photos: ArrayList<Bitmap>,
    private val eventListener: ToDoListener,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<MyBuildingRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentBuildingBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.buildingName.text = item.name
        holder.buildingShortDescription.text = item.shortDescription
        if(position<photos.size)holder.buildingPicture.setImageBitmap(photos[position])
        holder.container.setOnClickListener {
            eventListener.onItemClick(position)
        }
        if (isAdmin) {
            holder.container.setOnLongClickListener {
                eventListener.onItemLongClick(position)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentBuildingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val buildingPicture: ImageView = binding.buildingItemPicture
        val buildingName: TextView = binding.buildingItemName
        val buildingShortDescription: TextView = binding.buildingItemShortDescription
        val container: View = binding.root

        override fun toString(): String {
            return super.toString() + " '" + buildingName.text + "'"
        }
    }

}