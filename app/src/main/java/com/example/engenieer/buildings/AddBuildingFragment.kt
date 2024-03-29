package com.example.engenieer.buildings

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.engenieer.helper.FirebaseHandler
import com.example.engenieer.R
import com.example.engenieer.databinding.FragmentAddBuildingBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream
import java.util.UUID


class AddBuildingFragment : Fragment() {

    private lateinit var binding: FragmentAddBuildingBinding
    private lateinit var saveButton: FloatingActionButton
    private lateinit var selectPhotoButton: Button
    private lateinit var buildingPhoto: AppCompatImageView
    private lateinit var buildingNameInput: EditText
    private lateinit var buildingShortDescInput: EditText
    private lateinit var buildingDescInput: EditText
    private var photoID: String = ""
    private var buildingPhotoUri: Uri = "".toUri()
    private var photoHasBeenChanged: Boolean = false
    private val defaultID = "default"
    private val args: AddBuildingFragmentArgs by navArgs()

    //variables for Editing building
    private var position: Int = 0
    private lateinit var photo: Bitmap
    private lateinit var name: String
    private lateinit var shortDesc: String
    private lateinit var desc: String
    private lateinit var buildingToEdit: String
    private lateinit var oldPhotoID: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddBuildingBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindElements()
        setButtons()
        setDefaultPhoto()
        checkIfFragmentIsInEditState()
    }

    private fun checkIfFragmentIsInEditState() {
        if (args.editStatus)
            loadLocalData()
    }

    private fun loadLocalData() {
        position = args.position
        photo = Building.PHOTOS[position]
        name = Building.ITEMS[position].name
        shortDesc = Building.ITEMS[position].shortDescription
        desc = Building.ITEMS[position].description
        buildingToEdit = Building.ITEMS[position].buildingID
        oldPhotoID = Building.ITEMS[position].photo

        buildingPhoto.setImageBitmap(photo)
        buildingNameInput.setText(name)
        buildingShortDescInput.setText(shortDesc)
        buildingDescInput.setText(desc)
    }

    private fun setDefaultPhoto() {
        buildingPhoto.setImageResource(R.drawable.ic_building)
    }

    private fun bindElements() {
        saveButton = binding.saveButton
        selectPhotoButton = binding.selectPhotoButton
        buildingPhoto = binding.buildingPhoto
        buildingNameInput = binding.nameInput
        buildingShortDescInput = binding.shortDescInput
        buildingDescInput = binding.descInput
    }

    private fun setButtons() {
        setSaveButtonListener()
        setSelectPhotoButtonListener()
    }

    private fun setSelectPhotoButtonListener() {
        selectPhotoButton.setOnClickListener { selectPhoto() }
    }

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            buildingPhotoUri = uri
            buildingPhoto.setImageURI(uri)
        } else {

        }
    }

    private fun selectPhoto() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        createPhotoID()
    }

    private fun createPhotoID() {
        var photoID = UUID.randomUUID().toString()
        this.photoID = photoID
    }

    private fun setSaveButtonListener() {
        saveButton.setOnClickListener { save() }
    }

    private fun save() {
        var isValid: Boolean = false
        isValid = validateInput()
        if (!isValid) displaySaveFailedMessage()
        else{
            if (args.editStatus){
                updateData()
                returnToBuildingList()
            }
            else {
                uploadData()
                returnToBuildingList()
            }
        }
    }

    private fun updateData() {
        if (buildingPhotoUri.toString().isNotEmpty())
            createPhotoID()
        else
            photoID = oldPhotoID
        val buildingItem = BuildingItem(
            buildingToEdit,
            buildingNameInput.text.toString(),
            buildingDescInput.text.toString(),
            buildingShortDescInput.text.toString(),
            photoID
        )
        val oldPhotoID = Building.editLocalData(buildingItem)
        if(buildingPhotoUri.toString().isNotEmpty())
            Building.deleteBuildingPhoto(oldPhotoID) //if not empty means new photo has been chosen
        compressAndSendPhotoAndEditData(buildingItem)

    }

    private fun compressAndSendPhotoAndEditData(buildingItem: BuildingItem) {
        if (buildingPhotoUri.toString().isEmpty()){
            FirebaseHandler.RealtimeDatabase.addNewBuilding(buildingItem)
        }else{
            Glide.with(requireActivity())
                .asBitmap()
                .override(300,100)
                .load(buildingPhotoUri)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        Building.editPhoto(resource,buildingItem)
                        FirebaseHandler.RealtimeDatabase.uploadBuildingsPhoto(photoID,resource)
                        FirebaseHandler.RealtimeDatabase.addNewBuilding(buildingItem)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        //TODO("Not yet implemented")
                    }
                })
        }   
    }

    private fun returnToBuildingList() {
        val action = AddBuildingFragmentDirections.actionAddBuildingFragmentToBuildingFragment()
        findNavController().navigate(action)
    }

    private fun uploadData() {
        val buildingId = System.currentTimeMillis().toString()
        val buildingItem = BuildingItem(
            buildingId,
            buildingNameInput.text.toString(),
            buildingDescInput.text.toString(),
            buildingShortDescInput.text.toString(),
            photoID
        )
        Building.addItemFromLocalData(buildingItem)
        compressAndSendPhoto(buildingItem)
    }

    private fun compressAndSendPhoto(buildingItem: BuildingItem) {
        if (buildingPhotoUri.toString().isEmpty()){
            Building.addPhoto(resources.getDrawable(R.drawable.ic_default_building).toBitmap())
            FirebaseHandler.RealtimeDatabase.addNewBuilding(buildingItem)
        }else{
        Glide.with(requireActivity())
            .asBitmap()
            .override(300,100)
            .load(buildingPhotoUri)
            .into(object : CustomTarget<Bitmap>(){
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    Building.addPhoto(resource)
                    FirebaseHandler.RealtimeDatabase.uploadBuildingsPhoto(photoID,resource)
                    FirebaseHandler.RealtimeDatabase.addNewBuilding(buildingItem)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    //TODO("Not yet implemented")
                }
            })
        }
    }

    private fun displaySaveFailedMessage() {
        Snackbar.make(
            binding.root,
            R.string.save_building_failed,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun validateInput(): Boolean {
        if(buildingPhotoUri.toString().isEmpty()) photoID = defaultID
        return buildingNameInput.text.toString().isNotEmpty()
    }
}