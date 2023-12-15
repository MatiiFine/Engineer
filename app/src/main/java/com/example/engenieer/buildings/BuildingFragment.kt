package com.example.engenieer.buildings

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.findNavController
import com.example.engenieer.helper.FirebaseHandler
import com.example.engenieer.R
import com.example.engenieer.databinding.FragmentBuildingListBinding
import com.example.engenieer.helper.ToDoListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class BuildingFragment : Fragment(), ToDoListener {

    private lateinit var binding: FragmentBuildingListBinding
    private lateinit var addBuildingButton: FloatingActionButton
    private var isAdmin: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBuildingListBinding.inflate(inflater,container,false)
        verifyAccess()
        with(binding.list){
            layoutManager = LinearLayoutManager(context)
            downloadBuildingsData()
            adapter = MyBuildingRecyclerViewAdapter(Building.ITEMS,Building.PHOTOS,this@BuildingFragment, Building.getAccess())
        }

        return binding.root
    }

    private fun verifyAccess() {
        FirebaseHandler.RealtimeDatabase.getUserAccessRef().get().addOnSuccessListener {
            isAdmin = it.value as Boolean
            Building.setAccess(isAdmin)
            if (isAdmin) addBuildingButton.visibility = View.VISIBLE
            else addBuildingButton.visibility = View.GONE
        }
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
        var addedNewItem: Boolean = false
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
                    addedNewItem = addBuildingItem(buildingID,buildingName,buildingDescription,buildingShortDescription,buildingPhoto)
                    if (addedNewItem){
                        if(Building.PHOTOS.size<Building.ITEMS.size)prepareDefaultBuildingsPhoto()
                    }
                }
                downloadBuildingsPhoto()
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented wtf")
            }
        })
    }

    private fun prepareDefaultBuildingsPhoto() {
              Building.PHOTOS.add(resources.getDrawable(R.drawable.ic_default_building).toBitmap())
    }

    private fun downloadBuildingsPhoto() {
        for (building in Building.ITEMS){
            val status = findDownloadStatus(building)
            if (!status){ //if false photo hasn't been downloaded
                if (building.photo == "default"){
                    val index = Building.ITEMS.indexOf(building)
                    Building.PHOTOS[index] = resources.getDrawable(R.drawable.ic_default_building).toBitmap()
                    changeState(building)
                    reloadAdapter()
                }else {
                    FirebaseHandler.RealtimeDatabase.getBuildingStorageRef(building.photo)
                        .getBytes(4196 * 4196).addOnSuccessListener {
                            var image = it.toBitmap()
                            val index = Building.ITEMS.indexOf(building)
                            Building.PHOTOS[index] = image
                            changeState(building)
                            reloadAdapter()
                        }
                }
            }
        }
        reloadAdapter()
    }

    private fun changeState(building: BuildingItem) {
        for (element in Building.DOWNLOAD){
            if(element.first == building.buildingID) {
                val index = Building.DOWNLOAD.indexOf(element)
                Building.DOWNLOAD[index] = Pair(building.buildingID,true)
            }
        }
    }

    private fun findDownloadStatus(building: BuildingItem): Boolean {
        for (element in Building.DOWNLOAD){
            if(element.first == building.buildingID)
                return element.second
        }
        return true //if nothing has been found return true means "don't download"
    }


    private fun addBuildingItem(id: String, name: String, desc: String, shortDesc: String, photo: String): Boolean {
        return Building.addItem(
            BuildingItem(id,name,desc,shortDesc,photo)
        )
    }

    private fun reloadAdapter(){
        with(binding.list){
            layoutManager = LinearLayoutManager(context)
            adapter = MyBuildingRecyclerViewAdapter(Building.ITEMS,Building.PHOTOS,this@BuildingFragment,Building.getAccess())
        }
    }

    private fun ByteArray.toBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }

    override fun onItemClick(position: Int) {
        val action = BuildingFragmentDirections.actionBuildingFragmentRoomFragment(position,Building.getAccess())
        findNavController().navigate(action)
    }

    override fun onItemLongClick(position: Int) {
        startDialog(position)
    }

    private fun startDialog(position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.apply {
            setMessage(R.string.delete_building_msg)
            setTitle(R.string.delete_building_title)

            setPositiveButton(getString(R.string.delete_positive_btn)){ dialog, id ->
                deleteBuilding(position)
            }

            setNegativeButton(getString(R.string.delete_negative_btn)){ dialog, id ->
                showMessage(2)
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteBuilding(position: Int){
        Building.deleteBuilding(position)
        Building.PHOTOS.removeAt(position)
        reloadAdapter()
        showMessage(1)
    }

    private fun showMessage(i: Int) {
        var message: String = ""
        when(i){
            1 -> message = getString(R.string.building_delete_confirmed)
            2 -> message = getString(R.string.building_delete_declined)
        }
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

}