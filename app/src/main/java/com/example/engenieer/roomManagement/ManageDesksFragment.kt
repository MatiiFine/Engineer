package com.example.engenieer.roomManagement

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.navArgs
import com.example.engenieer.R
import com.example.engenieer.databinding.FragmentManageDesksBinding
import com.example.engenieer.helper.FirebaseHandler
import com.example.engenieer.rooms.Room
import com.example.engenieer.rooms.RoomItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ManageDesksFragment : Fragment() {

    private lateinit var binding: FragmentManageDesksBinding
    private val args: ManageDesksFragmentArgs by navArgs()
    private lateinit var roomName: TextView
    private lateinit var equipmentInput: EditText
    private lateinit var addBtn: Button
    private lateinit var removeButton: Button
    private lateinit var spinner: Spinner
    private var listOfEquipment: ArrayList<String> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentManageDesksBinding.inflate(layoutInflater, container, false)
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
        FirebaseHandler.RealtimeDatabase.getRoomsEquipmentRef(room.id).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfEquipment.clear()
                for(equipment in snapshot.children){
                    position = equipment.key.toString()
                    listOfEquipment.add(position)
                }
                val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,listOfEquipment)
                spinner.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })
    }

    private fun bindElements() {
        roomName = binding.manageDesksName
        equipmentInput = binding.newEquipmentInput
        addBtn = binding.addNewBtn
        removeButton = binding.removeBtn
        spinner = binding.spinnerManage
    }

    private fun setValue() {
        val roomItem = Room.getItem(args.position)
        roomName.text = roomItem.name
    }

    private fun setListeners() {
        addBtn.setOnClickListener { add() }
        removeButton.setOnClickListener { remove() }
    }

    private fun remove() {
        val equipment = spinner.selectedItem
        if (equipment != null) {
            listOfEquipment.removeAt(listOfEquipment.indexOf(equipment))
            val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,listOfEquipment)
            spinner.adapter = adapter
            FirebaseHandler.RealtimeDatabase.deleteEquipment(
                Room.getItem(args.position).id,
                equipment.toString()
            )
        }
    }

    private fun add() {
        if (equipmentInput.text.toString().isNotEmpty()) {
            FirebaseHandler.RealtimeDatabase.addNewEquipment(
                Room.getItem(args.position).id,
                equipmentInput.text.toString()
            )
            listOfEquipment.add(equipmentInput.text.toString())
            val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,listOfEquipment)
            spinner.adapter = adapter
            equipmentInput.setText("")
            val view: View? = binding.root
            if (view != null) {
                val inputMethodManager =
                    requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }
        }
    }
}