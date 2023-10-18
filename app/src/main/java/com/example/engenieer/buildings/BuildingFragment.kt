package com.example.engenieer.buildings

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.engenieer.FirebaseHandler
import com.example.engenieer.R
import com.example.engenieer.databinding.FragmentBuildingListBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.ktx.storage

class BuildingFragment : Fragment() {

    private lateinit var binding: FragmentBuildingListBinding
    private lateinit var addBuildingButton: FloatingActionButton
    private val photos: ArrayList<Bitmap> = ArrayList()
    private val args: BuildingFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBuildingListBinding.inflate(inflater,container,false)
        with(binding.list){
            layoutManager = LinearLayoutManager(context)
            clearBuildings()
            downloadBuildingsData()
            adapter = MyBuildingRecyclerViewAdapter(Building.ITEMS,photos)
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

    private fun clearBuildings() {
        Building.clearItems()
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
                            "photo" -> buildingPhoto = buildingInfo.value.toString()
                        }
                    }
                    addBuildingItem(buildingID,buildingName,buildingDescription,buildingShortDescription,buildingPhoto)
                }
                prepareDefaultBuildingsPhoto()
                downloadBuildingsPhoto()
                loadAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented wtf")
            }
        })
    }

    private fun prepareDefaultBuildingsPhoto() {
        for (item in Building.ITEMS){
              photos.add(resources.getDrawable(R.drawable.ic_default_building).toBitmap())
        }
    }

    private fun downloadBuildingsPhoto() {
        if(Building.isAbleToDownload()){
            val photoID = Building.getPhotoID()
            if (photoID.isNotEmpty()){
                FirebaseHandler.RealtimeDatabase.getBuildingRef(photoID)
                    .getBytes(4196 * 4196).addOnSuccessListener {
                        var image = it.toBitmap()
                        photos[Building.getIter()] = image
                        loadAdapter()
                        downloadBuildingsPhoto()
                    }
            }
        }else{
            loadAdapter()
        }
    }


    private fun addBuildingItem(id: String, name: String, desc: String, shortDesc: String, photo: String) {
        Building.addItem(
            BuildingItem(id,name,desc,shortDesc,photo)
        )
    }

    private fun loadAdapter(){
        with(binding.list){
            layoutManager = LinearLayoutManager(context)
            adapter = MyBuildingRecyclerViewAdapter(Building.ITEMS,photos)
        }
    }

    private fun ByteArray.toBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }

}