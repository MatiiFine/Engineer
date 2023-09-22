package com.example.engenieer.buildings

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.example.engenieer.databinding.FragmentBuildingBinding

class MyBuildingRecyclerViewAdapter(
    private val values: List<BuildingItem>
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
        holder.buildingShortDescription.text = item.name
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentBuildingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val buildingPicture: ImageView = binding.buildingItemPicture
        val buildingName: TextView = binding.buildingItemName
        val buildingShortDescription: TextView = binding.buildingItemShortDescription

        override fun toString(): String {
            return super.toString() + " '" + buildingName.text + "'"
        }
    }

}