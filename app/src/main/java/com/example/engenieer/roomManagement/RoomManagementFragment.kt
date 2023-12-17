package com.example.engenieer.roomManagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.engenieer.databinding.FragmentRoomManagementBinding
import com.example.engenieer.rooms.RoomFragmentDirections

class RoomManagementFragment : Fragment() {

    private lateinit var binding: FragmentRoomManagementBinding
    private lateinit var previewButton: Button
    private lateinit var managementButton: Button
    private lateinit var bookingsButton: Button
    private val args: RoomManagementFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRoomManagementBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindElements()
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        previewButton.setOnClickListener { preview() }
        managementButton.setOnClickListener { manage() }
        bookingsButton.setOnClickListener { bookings() }
    }

    private fun bookings() {
        //TODO("Not yet implemented")
    }

    private fun manage() {
        //TODO("Not yet implemented")
    }

    private fun preview() {
        val action = RoomManagementFragmentDirections.actionRoomManagementFragmentToPreviewFragment(args.position)
        findNavController().navigate(action)
    }

    private fun bindElements() {
        previewButton = binding.previewBtn
        managementButton = binding.manageBtn
        bookingsButton = binding.bookingsBtn
    }
}