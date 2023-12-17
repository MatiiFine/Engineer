package com.example.engenieer.roomManagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
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
    private val args: RoomManagementFragmentArgs by navArgs()
    private lateinit var roomName: TextView
    private lateinit var equipmentInput: EditText
    private lateinit var addBtn: Button
    private lateinit var removeButton: Button
    private lateinit var spinner: Spinner
    private lateinit var listOfEquipment: List<String>

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
        FirebaseHandler.RealtimeDatabase.getRoomsEquipmentRef(room.id).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(roomsEquipment in snapshot.children){
                    for(equipment in roomsEquipment.children){
                        position = equipment.key.toString()
                        listOfEquipment = listOfEquipment + position
                    }
                }
                //val adapter: ArrayAdapter = ArrayAdapter(context,spinner,listOfEquipment)
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
        spinner = binding.spinner
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
        //TODO("Not yet implemented")
    }

    private fun add() {
        //TODO("Not yet implemented")
    }
}