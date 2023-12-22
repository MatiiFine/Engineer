package com.example.engenieer.booking

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import com.example.engenieer.databinding.FragmentBookingBinding

/**
 * [RecyclerView.Adapter] that can display a [Booking].
 * TODO: Replace the implementation with code for your data type.
 */
class MyBookingRecyclerViewAdapter(
    private val values: List<Booking>
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
 //       holder.idView.text = item.id
  //      holder.contentView.text = item.content
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {
//        val idView: TextView = binding.itemNumber
//        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" //+ contentView.text + "'"
        }
    }

}