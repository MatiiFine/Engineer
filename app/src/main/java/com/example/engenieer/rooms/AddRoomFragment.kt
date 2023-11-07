package com.example.engenieer.rooms

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import com.example.engenieer.R
import com.example.engenieer.databinding.FragmentAddRoomBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.UUID


class AddRoomFragment : Fragment() {

    private lateinit var binding: FragmentAddRoomBinding
    private lateinit var saveButton: FloatingActionButton
    private lateinit var selectPhotoButton: Button
    private lateinit var roomPhoto: AppCompatImageView
    private lateinit var roomNameInput: EditText
    private lateinit var roomShortDescInput: EditText
    private lateinit var roomDescInput: EditText
    private lateinit var photoID: String
    private lateinit var roomPhotoUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment1
        binding = FragmentAddRoomBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindElements()
        setButtons()
        setDefaultPhoto()
    }

    private fun setDefaultPhoto() {
        roomPhoto.setImageResource(R.drawable.ic_room)
    }

    private fun setButtons() {
        setSaveButtonOnClickListener()
        setSelectPhotoButtonOnClickListener()
    }

    private fun setSelectPhotoButtonOnClickListener() {
        selectPhotoButton.setOnClickListener { selectPhoto() }
    }

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            roomPhotoUri = uri
            roomPhoto.setImageURI(uri)
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

    private fun setSaveButtonOnClickListener() {
        saveButton.setOnClickListener {save()}
    }

    private fun save() {
        //TODO("Not yet implemented")
    }

    private fun bindElements() {
        roomPhoto = binding.roomPhoto
        saveButton = binding.saveButton
        selectPhotoButton = binding.selectPhotoButton
        roomNameInput = binding.nameInput
        roomShortDescInput = binding.shortDescInput
        roomDescInput = binding.descInput
    }

}