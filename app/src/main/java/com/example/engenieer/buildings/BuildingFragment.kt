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
import androidx.navigation.fragment.navArgs
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
    private val photos: ArrayList<Bitmap> = ArrayList()
    private var isAdmin: Boolean = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBuildingListBinding.inflate(inflater,container,false)
        with(binding.list){
            layoutManager = LinearLayoutManager(context)
            clearBuildings()
            downloadBuildingsData()
            adapter = MyBuildingRecyclerViewAdapter(Building.ITEMS,photos,this@BuildingFragment, isAdmin)
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
        FirebaseHandler.RealtimeDatabase.getUserAccessRef().get().addOnSuccessListener {
            isAdmin = it.value as Boolean
            if (isAdmin) addBuildingButton.visibility = View.VISIBLE
            else addBuildingButton.visibility = View.GONE
        }
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
                reloadAdapter()
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
                FirebaseHandler.RealtimeDatabase.getBuildingStorageRef(photoID)
                    .getBytes(4196 * 4196).addOnSuccessListener {
                        var image = it.toBitmap()
                        photos[Building.getIter()] = image
                        reloadAdapter()
                        downloadBuildingsPhoto()
                    }
            }
        }else{
            reloadAdapter()
        }
    }


    private fun addBuildingItem(id: String, name: String, desc: String, shortDesc: String, photo: String) {
        Building.addItem(
            BuildingItem(id,name,desc,shortDesc,photo)
        )
    }

    private fun reloadAdapter(){
        with(binding.list){
            layoutManager = LinearLayoutManager(context)
            adapter = MyBuildingRecyclerViewAdapter(Building.ITEMS,photos,this@BuildingFragment,isAdmin)
        }
    }

    private fun ByteArray.toBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }

    override fun onItemClick(position: Int) {
        val action = BuildingFragmentDirections.actionBuildingFragmentRoomFragment(position,isAdmin)
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
        photos.removeAt(position)
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