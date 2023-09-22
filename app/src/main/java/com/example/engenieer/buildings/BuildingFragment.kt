package com.example.engenieer.buildings

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.engenieer.FirebaseHandler
import com.example.engenieer.databinding.FragmentBuildingListBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BuildingFragment : Fragment() {

    private lateinit var binding: FragmentBuildingListBinding
    private lateinit var addBuildingButton: FloatingActionButton
    private val args: BuildingFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBuildingListBinding.inflate(inflater,container,false)
        with(binding.list){
            layoutManager = LinearLayoutManager(context)
            downloadBuildingsData()
            adapter = MyBuildingRecyclerViewAdapter(Building.ITEMS)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindElements()
        setButtons()
    }

    private fun setButtons() {
        setAddBuildingButtonListener()
        setAddBuildingButtonVisibility()
    }

    private fun setAddBuildingButtonVisibility() {
        val isAdmin = args.isAdmin
        if (isAdmin) addBuildingButton.visibility = View.VISIBLE
        else addBuildingButton.visibility = View.GONE
    }

    private fun setAddBuildingButtonListener() {
        addBuildingButton.setOnClickListener { addNewBuilding() }
    }

    private fun addNewBuilding() {
        val action = BuildingFragmentDirections.actionBuildingFragmentToAddBuildingFragment()
        findNavController().navigate(action)
    }

    private fun bindElements() {
        addBuildingButton = binding.addBuildingButton
    }

    private fun downloadBuildingsData() {
        var buildingID: String = ""
        var buildingName: String = ""
        var buildingDescription: String = ""
        var buildingShortDescription: String = ""
        var buildingPhoto: String = ""
        FirebaseHandler.RealtimeDatabase.getBuildingsRef().addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (building in snapshot.children){
                    buildingID = building.key.toString()
                    for (buildingInfo in building.children){
                        when(buildingInfo.key.toString()){
                            "name" -> buildingName = buildingInfo.value.toString()
                            "description" -> buildingDescription = buildingInfo.value.toString()
                            "shortDescription" -> buildingShortDescription = buildingInfo.value.toString()
                            "photo" -> buildingInfo.value.toString()
                        }
                    }
                    addBuildingItem(buildingID,buildingName,buildingDescription,buildingShortDescription,buildingPhoto)
                }
                //TODO("downloadBuildingsPicture()")
                // downloadBuildingsPicture()
                loadAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })
    }

    private fun addBuildingItem(id: String, name: String, desc: String, shortDesc: String, photo: String) {
        Building.addItem(
            BuildingItem(id,name,desc,shortDesc,photo)
        )
    }

    private fun loadAdapter(){
        with(binding.list){
            layoutManager = LinearLayoutManager(context)
            adapter = MyBuildingRecyclerViewAdapter(Building.ITEMS)
        }
    }

}