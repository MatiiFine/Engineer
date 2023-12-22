package com.example.engenieer.booking

import android.os.Bundle
import androidx.fragment.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.engenieer.buildings.Building
import com.example.engenieer.databinding.FragmentBookingsListBinding
import com.example.engenieer.helper.FirebaseHandler
import com.example.engenieer.helper.ToDoListener
import com.example.engenieer.rooms.Room
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class BookingFragment : Fragment(), ToDoListener {

    private lateinit var binding: FragmentBookingsListBinding
    private val args: BookingFragmentArgs by navArgs()
    private var isAdmin: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookingsListBinding.inflate(layoutInflater,container,false)
        verifyAccess()
        Booking.checkRoom(Room.getItem(args.position).id)
        with(binding.list){
            layoutManager = LinearLayoutManager(context)
            downloadBookingsData()
            adapter = MyBookingRecyclerViewAdapter(Booking.ITEMS,isAdmin,this@BookingFragment)
        }
        return binding.root
    }

    private fun verifyAccess() {
        FirebaseHandler.RealtimeDatabase.getUserAccessRef().get().addOnSuccessListener {
            isAdmin = it.value as Boolean
            reloadAdapter()
        }
    }

    private fun downloadBookingsData() {
        val roomItem = Room.getItem(args.position)
        val roomID: String = roomItem.id
        var bookingID: String = ""
        var ownerID: String = ""
        var roomName: String = ""
        var equipment: String = ""
        var date: String = ""
        var startHour: String = ""
        var endHour: String = ""
        FirebaseHandler.RealtimeDatabase.getRoomBookingsRef(roomItem.id).addValueEventListener(object :
            ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(bookings in snapshot.children){
                    bookingID = bookings.key.toString()
                    for (booking in bookings.children){
                        when(booking.key.toString()){
                            "date" -> date = booking.value.toString()
                            "endHour" -> endHour = booking.value.toString()
                            "equipment" -> equipment = booking.value.toString()
                            "owner" -> ownerID = booking.value.toString()
                            "roomName" -> roomName = booking.value.toString()
                            "startHour" -> startHour = booking.value.toString()
                        }
                    }
                    val newBookingItem = BookingItem(
                        bookingID,
                        ownerID,
                        roomID,
                        roomName,
                        equipment,
                        date,
                        startHour,
                        endHour
                    )
                    Booking.addItem(newBookingItem)
                }
                reloadAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }
        })
    }

    private fun reloadAdapter() {
        with(binding.list){
            layoutManager = LinearLayoutManager(context)
            adapter = MyBookingRecyclerViewAdapter(Booking.ITEMS,isAdmin,this@BookingFragment)
        }
    }

    override fun onItemClick(position: Int) {
        val bookingItem = Booking.getItem(position)
        Booking.delete(position)
        FirebaseHandler.RealtimeDatabase.deleteBooking(Room.getItem(args.position).id, bookingItem.bookingID)
        reloadAdapter()
    }

    override fun onItemLongClick(position: Int) {
        //TODO("Not yet implemented")
    }
}