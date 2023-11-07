package com.example.engenieer.rooms

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.engenieer.databinding.RoomFragmentItemListBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * A fragment representing a list of Items.
 */
class RoomFragment : Fragment() {

    private lateinit var binding: RoomFragmentItemListBinding
    private val args: RoomFragmentArgs by navArgs()
    private lateinit var addRoomButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RoomFragmentItemListBinding.inflate(inflater,container,false)

        // Set the adapter
        with(binding.list){
            layoutManager = LinearLayoutManager(context)
            //clearBuildings()
            //downloadBuildingsData()
            adapter = MyRoomRecyclerViewAdapter(Room.ITEMS)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindElements()
        setButtons()
    }

    private fun setButtons() {
        setAddRoomButtonListener()
        setAddRoomButtonVisibility()
    }

    private fun setAddRoomButtonVisibility() {
        if (args.isAdmin) addRoomButton.visibility = View.VISIBLE
        else addRoomButton.visibility = View.GONE
    }

    private fun setAddRoomButtonListener() {
        addRoomButton.setOnClickListener {
            addNewRoom()
        }
    }

    private fun addNewRoom() {
        //TODO("Not yet implemented")
    }

    private fun bindElements() {
        addRoomButton = binding.addRoomButton
    }

}