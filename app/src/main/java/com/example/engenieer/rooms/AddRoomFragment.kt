package com.example.engenieer.rooms

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
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
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.engenieer.R
import com.example.engenieer.buildings.Building
import com.example.engenieer.databinding.FragmentAddRoomBinding
import com.example.engenieer.helper.FirebaseHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
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
    private var roomPhotoUri: Uri = "".toUri()
    private val args: AddRoomFragmentArgs by navArgs()
    private val defaultID = "default"

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
        var isValid: Boolean = false
        isValid = validateInput()
        if (!isValid) displaySaveFailedMessage()
        else{
            uploadData()
            returnToBuildingList()
        }
    }

    private fun returnToBuildingList() {
        val action = AddRoomFragmentDirections.actionAddRoomFragmentToRoomFragment(args.buildingPosition,true)
        findNavController().navigate(action)
    }

    private fun uploadData() {
        val roomId = System.currentTimeMillis().toString()
        val buildingID = Building.ITEMS[args.buildingPosition].buildingID
        val roomItem = RoomItem(
            roomId,
            roomNameInput.text.toString(),
            roomDescInput.text.toString(),
            roomShortDescInput.text.toString(),
            photoID,
            buildingID
        )
        Room.addFromLocalData(roomItem)
        compressAndSendPhoto(roomItem,buildingID)
    }

    private fun compressAndSendPhoto(roomItem: RoomItem, buildingID: String) {
        if (roomPhotoUri.toString().isEmpty()){
            Room.addPhoto(resources.getDrawable(R.drawable.ic_room).toBitmap())
            FirebaseHandler.RealtimeDatabase.addNewRoom(roomItem)
        }else {
            Glide.with(requireActivity())
                .asBitmap()
                .override(300, 100)
                .load(roomPhotoUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        Room.addPhoto(resource)
                        FirebaseHandler.RealtimeDatabase.uploadRoomsPhoto(
                            photoID,
                            buildingID,
                            resource
                        )
                        FirebaseHandler.RealtimeDatabase.addNewRoom(roomItem)
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
            R.string.save_room_failed,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun validateInput(): Boolean {
        if(roomPhotoUri.toString().isEmpty()) photoID = defaultID
        return roomNameInput.text.toString().isNotEmpty()
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