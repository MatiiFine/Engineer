package com.example.engenieer.roomManagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsSpinner
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.example.engenieer.databinding.FragmentPreviewBinding
import com.example.engenieer.rooms.Room


class PreviewFragment : Fragment() {

    private lateinit var binding: FragmentPreviewBinding
    private  val args: PreviewFragmentArgs by navArgs()
    private lateinit var name: TextView
    private lateinit var spinner: Spinner
    private lateinit var book_btn: Button
    private lateinit var book_all_btn: Button
    private lateinit var description: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPreviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindElements()
        setValue()
        setListeners()
    }

    private fun bindElements() {
        name = binding.previewName
        spinner = binding.deskSpinner
        book_btn = binding.bookBtn
        book_all_btn = binding.bookAllBtn
        description = binding.previewDescription
    }

    private fun setValue() {
        val roomItem = Room.getItem(args.position)
        name.text = roomItem.name
        description.text = roomItem.description
    }

    private fun setListeners() {
        book_btn.setOnClickListener { book() }
        book_all_btn.setOnClickListener { book_all() }
    }

    private fun book_all() {
        //TODO("Not yet implemented")
    }

    private fun book() {
        //TODO("Not yet implemented")
    }

}