package com.example.engenieer.rooms

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.engenieer.R
import com.example.engenieer.buildings.Building
import com.example.engenieer.buildings.MyBuildingRecyclerViewAdapter
import com.example.engenieer.databinding.RoomFragmentItemListBinding
import com.example.engenieer.helper.FirebaseHandler
import com.example.engenieer.helper.ToDoListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RoomFragment : Fragment(), ToDoListener {

    private lateinit var binding: RoomFragmentItemListBinding
    private val args: RoomFragmentArgs by navArgs()
    private lateinit var addRoomButton: FloatingActionButton
    private lateinit var currentBuildingID: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RoomFragmentItemListBinding.inflate(inflater,container,false)
        currentBuildingID = Building.ITEMS[args.buildingPosition].buildingID
        checkBuilding()

        // Set the adapter
        with(binding.list){
            layoutManager = LinearLayoutManager(context)
            downloadRoomsData()
            adapter = MyRoomRecyclerViewAdapter(Room.ITEMS,Room.PHOTOS, this@RoomFragment, args.isAdmin)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindElements()
        setButtons()
    }

    private fun checkBuilding() {
        Room.checkBuilding(currentBuildingID)
    }

    private fun downloadRoomsData() {
        currentBuildingID = Building.ITEMS[args.buildingPosition].buildingID
        var roomID: String = ""
        var roomName: String = ""
        var roomDescription: String = ""
        var roomShortDescription: String = ""
        var roomPhoto: String = ""
        var addedNewItem: Boolean = false
        FirebaseHandler.RealtimeDatabase.getRoomsRef().addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (building in snapshot.children){
                    if (building.key.toString() == currentBuildingID){
                        for (room in building.children){
                            roomID = room.key.toString()
                            for (roomInfo in room.children){
                                when(roomInfo.key.toString()){
                                    "name" -> roomName = roomInfo.value.toString()
                                    "description" -> roomDescription = roomInfo.value.toString()
                                    "shortDescription" -> roomShortDescription = roomInfo.value.toString()
                                    "photo" -> roomPhoto = roomInfo.value.toString()
                                }
                            }
                            addedNewItem = addRoomItem(roomID,roomName,roomDescription,roomShortDescription,roomPhoto,currentBuildingID)
                            if (addedNewItem){
                                if(Room.PHOTOS.size<Room.ITEMS.size)prepareDefaultRoomPhoto()
                            }
                        }
                    }
                }
                downloadRoomsPhoto()
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })
    }

    private fun reloadAdapter() {
        with(binding.list){
            layoutManager = LinearLayoutManager(context)
            adapter = MyRoomRecyclerViewAdapter(Room.ITEMS,Room.PHOTOS,this@RoomFragment,args.isAdmin)
        }
    }

    private fun downloadRoomsPhoto() {
        for (room in Room.ITEMS){
            val status = findDownloadStatus(room)
            if(!status){
                if (room.photo == "default"){
                    val index = Room.ITEMS.indexOf(room)
                    Room.PHOTOS[index] = resources.getDrawable(R.drawable.ic_room).toBitmap()
                    changeState(room)
                    reloadAdapter()
                }else{
                    FirebaseHandler.RealtimeDatabase.getRoomStorageRef(currentBuildingID).child(room.photo)
                        .getBytes(4196 * 4196).addOnSuccessListener{
                            var image = it.toBitmap()
                            val index = Room.ITEMS.indexOf(room)
                            Room.PHOTOS[index] = image
                            changeState(room)
                            reloadAdapter()
                        }
                }
            }
        }
        reloadAdapter()
    }

    private fun changeState(room: RoomItem) {
        val index = Room.ITEMS.indexOf(room)
        Room.DOWNLOAD[index] = Pair(room.id,true)
    }

    private fun findDownloadStatus(room: RoomItem): Boolean {
        for (element in Room.DOWNLOAD){
            if(element.first == room.id)
                return element.second
        }
        return true //if nothing has been found return true means "don't download"
    }

    private fun prepareDefaultRoomPhoto() {
            Room.PHOTOS.add(resources.getDrawable(R.drawable.ic_room).toBitmap())
    }

    private fun addRoomItem(id: String, name: String, desc: String, shortDesc: String, photo: String, buildingID: String): Boolean {
        return Room.addItem(
            RoomItem(
                id,
                name,
                desc,
                shortDesc,
                photo,
                buildingID
            )
        )
    }

    private fun setButtons() {
        setAddRoomButtonListener()
        setAddRoomButtonVisibility()
    }

    private fun setAddRoomButtonVisibility() {
        if (args.isAdmin) addRoomButton.visibility = View.VISIBLE
        else addRoomButton.visibility = View.GONE
    }

    private fun setAddRoomButtonListener() {
        addRoomButton.setOnClickListener {
            addNewRoom(args.buildingPosition)
        }
    }

    private fun addNewRoom(position: Int) {
        val action = RoomFragmentDirections.actionRoomFragmentToAddRoomFragment(position)
        findNavController().navigate(action)
    }

    private fun bindElements() {
        addRoomButton = binding.addRoomButton
    }

    private fun ByteArray.toBitmap(): Bitmap {
        return BitmapFactory.decodeByteArray(this, 0, this.size)
    }

    override fun onItemClick(position: Int) {
        //TODO("Not yet implemented")
    }

    override fun onItemLongClick(position: Int) {
        startDialog(position)
    }

    private fun startDialog(position: Int) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        builder.apply {
            setMessage(R.string.delete_room_msg)
            setTitle(R.string.delete_room_title)

            setPositiveButton(getString(R.string.delete_positive_btn)){ dialog, id ->
                deleteRoom(position)
            }

            setNegativeButton(getString(R.string.delete_negative_btn)){ dialog, id ->
                showMessage(2)
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteRoom(position: Int) {
        Room.deleteRoom(position)
        Room.PHOTOS.removeAt(position)
        reloadAdapter()
        showMessage(1)
    }

    private fun showMessage(i: Int) {
        var message: String = ""
        when(i){
            1 -> message = getString(R.string.room_delete_confirmed)
            2 -> message = getString(R.string.room_delete_declined)
        }
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }

}