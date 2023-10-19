package com.example.engenieer.buildings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.engenieer.helper.FirebaseHandler
import com.example.engenieer.R
import com.example.engenieer.databinding.FragmentAddBuildingBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.util.UUID


class AddBuildingFragment : Fragment() {

    private lateinit var binding: FragmentAddBuildingBinding
    private lateinit var saveButton: FloatingActionButton
    private lateinit var selectPhotoButton: Button
    private lateinit var buildingPhoto: AppCompatImageView
    private lateinit var buildingNameInput: EditText
    private lateinit var buildingShortDescInput: EditText
    private lateinit var buildingDescInput: EditText
    private lateinit var photoID: String
    private lateinit var buildingPhotoUri: Uri
    private var photoHasBeenChange: Boolean = false

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
            uploadData()
            returnToBuildingList()
        }
    }

    private fun returnToBuildingList() {
        val action = AddBuildingFragmentDirections.actionAddBuildingFragmentToBuildingFragment(true)
        findNavController().navigate(action)
    }

    private fun uploadData() {
        val buildingId = System.currentTimeMillis().toString()
        FirebaseHandler.RealtimeDatabase.addNewBuilding(
            BuildingItem(
                buildingId,
                buildingNameInput.text.toString(),
                buildingDescInput.text.toString(),
                buildingShortDescInput.text.toString(),
                photoID
            )
        )
        FirebaseHandler.RealtimeDatabase.uploadPhoto(photoID,buildingPhotoUri)
    }

    private fun displaySaveFailedMessage() {
        Snackbar.make(
            binding.root,
            R.string.save_building_failed,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun validateInput(): Boolean {
        return buildingNameInput.text.toString().isNotEmpty()
    }
}