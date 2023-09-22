package com.example.engenieer.buildings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.example.engenieer.FirebaseHandler
import com.example.engenieer.R
import com.example.engenieer.databinding.FragmentAddBuildingBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar


class AddBuildingFragment : Fragment() {

    private lateinit var binding: FragmentAddBuildingBinding
    private lateinit var saveButton: FloatingActionButton
    private lateinit var selectPhotoButton: Button
    private lateinit var buildingPhoto: AppCompatImageView
    private lateinit var buildingNameInput: EditText
    private lateinit var buildingShortDescInput: EditText
    private lateinit var buildingDescInput: EditText

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
//        TODO("Not yet implemented")
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
        }
    }

    private fun uploadData() {
        val buildingId = System.currentTimeMillis().toString()
        FirebaseHandler.RealtimeDatabase.addNewBuilding(
            BuildingItem(
                buildingId,
                buildingNameInput.text.toString(),
                buildingDescInput.text.toString(),
                buildingShortDescInput.text.toString(),
                "" //TODO("photo")
            )
        )
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