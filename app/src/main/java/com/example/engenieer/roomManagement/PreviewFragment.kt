package com.example.engenieer.roomManagement

import android.R
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsSpinner
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.engenieer.databinding.FragmentPreviewBinding
import com.example.engenieer.helper.FirebaseHandler
import com.example.engenieer.rooms.Room
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class PreviewFragment : Fragment() {

    private lateinit var binding: FragmentPreviewBinding
    private  val args: PreviewFragmentArgs by navArgs()
    private lateinit var name: TextView
    private lateinit var spinner: Spinner
    private lateinit var book_btn: Button
    private lateinit var book_all_btn: Button
    private lateinit var description: TextView
    private var listOfEquipment: ArrayList<String> = ArrayList()

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
        downloadAndSetEquipment()
    }

    private fun downloadAndSetEquipment() {
        val room = Room.getItem(args.position)
        var position: String = ""
        FirebaseHandler.RealtimeDatabase.getRoomsEquipmentRef(room.id).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfEquipment.clear()
                for(equipment in snapshot.children){
                    position = equipment.key.toString()
                    listOfEquipment.add(position)
                }
                val adapter = ArrayAdapter(requireContext(),
                    R.layout.simple_spinner_item,listOfEquipment)
                spinner.adapter = adapter
                if (spinner.visibility == View.GONE){
                    spinner.visibility = View.VISIBLE
                    book_btn.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })
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
        book_btn.visibility = View.GONE
        spinner.visibility = View.GONE
    }

    private fun setListeners() {
        book_btn.setOnClickListener { book() }
        book_all_btn.setOnClickListener { book_all() }
    }

    private fun book_all() {
        val action = PreviewFragmentDirections.actionPreviewToCalendar("nothing",args.position,true)
        findNavController().navigate(action)
    }

    private fun book() {
        val equipment = spinner.selectedItem.toString()
        val action = PreviewFragmentDirections.actionPreviewToCalendar(equipment,args.position,false)
        findNavController().navigate(action)
    }

}