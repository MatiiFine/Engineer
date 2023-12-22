package com.example.engenieer.booking

import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.TimePicker
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.engenieer.R
import com.example.engenieer.databinding.FragmentCalendarBinding
import com.example.engenieer.helper.FirebaseHandler
import com.example.engenieer.rooms.Room
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date
import java.util.UUID

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private val args: CalendarFragmentArgs by navArgs()
    private lateinit var calendar: CalendarView
    private lateinit var startButton: Button
    private lateinit var endButton: Button
    private lateinit var bookmarkButton: ImageButton
    private lateinit var bookingsButton: Button
    private var day: Int = 1
    private var month: Int = 1
    private var year: Int = 2023
    private var startHourTime = 12
    private var startMinuteTime = 0
    private var endHourTime = 13
    private var endMinuteTime = 0
    private var startTime: String = ""
    private var endTime: String = ""
    private lateinit var startTimePickerDialog: TimePickerDialog
    private lateinit var endTimePickerDialog: TimePickerDialog
    private lateinit var pickedDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindElements()
        initializeCalendar()
        initializeStartPicker()
        initializeEndPicker()
        setOnClickListeners()
        Booking.checkRoom(Room.getItem(args.position).id)
        downloadBookings()
    }

    private fun downloadBookings() {
        val roomItem = Room.getItem(args.position)
        var roomID: String = roomItem.id
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
                    var newBookingItem = BookingItem(
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
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }
        })
    }

    private fun initializeEndPicker() {
        val currentTime = Calendar.getInstance()
        endTimePickerDialog = TimePickerDialog(requireContext(), object : TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                endHourTime = hourOfDay
                endMinuteTime = minute
                setEndTime(endHourTime,endMinuteTime)

            }
        },currentTime.get(Calendar.HOUR_OF_DAY+1),currentTime.get(Calendar.MINUTE),true)
    }

    private fun initializeStartPicker() {
        val currentTime = Calendar.getInstance()
        startTimePickerDialog = TimePickerDialog(requireContext(), object : TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                startHourTime = hourOfDay
                startMinuteTime = minute
                setStartTime(startHourTime,startMinuteTime)

            }
        },currentTime.get(Calendar.HOUR_OF_DAY),currentTime.get(Calendar.MINUTE),true)
    }

    private fun initializeCalendar() {
        val calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        startHourTime = calendar.get(Calendar.HOUR_OF_DAY)
        startMinuteTime = calendar.get(Calendar.MINUTE)
        endHourTime = calendar.get(Calendar.HOUR_OF_DAY) + 1
        endMinuteTime = startMinuteTime

        setStartTime(startHourTime,startMinuteTime)

        setEndTime(endHourTime,endMinuteTime)

        pickedDate = "$day.$month.$year"

        this.calendar.setOnDateChangeListener(CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
            validateDate(dayOfMonth,month,year)
        })
    }

    private fun validateDate(dayOfMonth: Int, month: Int, year: Int) {
        val current = Calendar.getInstance()
        if (dayOfMonth < current.get(Calendar.DAY_OF_MONTH) ||
                month < current.get(Calendar.MONTH) ||
                year < current.get(Calendar.YEAR)){
            displayCalendarFailedMessage()
        }else{
            this.day = dayOfMonth
            this.month = month
            this.year = year
            pickedDate = "$dayOfMonth.$month.$year"
        }
    }


    private fun displayCalendarFailedMessage() {
        Snackbar.make(
            binding.root,
            R.string.date_pick_failed,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun setOnClickListeners() {
        startButton.setOnClickListener { startButton() }
        endButton.setOnClickListener { endButton() }
        bookmarkButton.setOnClickListener { booking() }
        bookingsButton.setOnClickListener { showBookings() }
    }

    private fun showBookings() {
        val action = CalendarFragmentDirections.actionCalendarFragmentToBookingFragment(args.position)
        findNavController().navigate(action)
    }

    private fun startButton(){
        startTimePickerDialog.show()
    }

    private fun endButton(){
        endTimePickerDialog.show()
    }

    private fun booking() {
        val isValid: Boolean = validateTime() && validateFreeDates()
        if (isValid)newBooking()
    }

    private fun validateFreeDates(): Boolean {
        val arrayList: ArrayList<BookingItem> = ArrayList()
        val start: Int = (startHourTime * 100) + startMinuteTime
        val end: Int = (endHourTime * 100) + endMinuteTime
        if (args.equipment == "nothing"){
            for (booking in Booking.ITEMS){
                if (booking.date == pickedDate)arrayList.add(booking)
            }
        }else{
            for (booking in Booking.ITEMS){
                if (booking.date == pickedDate && (booking.equipment == args.equipment || booking.equipment == "nothing"))arrayList.add(booking)
            }
        }
        for (element in arrayList) {
            var elementStart: Int = element.startHour.subSequence(0, 2).toString().toInt() * 100 + element.startHour.subSequence(3, 5).toString().toInt()
            var elementEnd: Int = element.endHour.subSequence(0,2).toString().toInt() * 100 + element.endHour.subSequence(3,5).toString().toInt()
            if (elementStart in start .. end || elementEnd in start .. end) {
                showFailedFreeDates()
                return false
            }
        }
        return true
    }

    private fun showFailedFreeDates(){
        Snackbar.make(
            binding.root,
            R.string.room_booking_failed,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun newBooking() {
        val bookingID = UUID.randomUUID().toString()
        val roomItem = Room.getItem(args.position)
        val date = "$day.$month.$year"
        val bookingItem = BookingItem(
            bookingID,
            FirebaseHandler.Authentication.getUserUid().toString(),
            roomItem.id,
            roomItem.name,
            args.equipment,
            date,
            startTime,
            endTime
        )
        Booking.addItem(bookingItem)
        FirebaseHandler.RealtimeDatabase.addNewBooking(bookingItem)
        showSuccessMessage()
        findNavController().popBackStack()
    }

    private fun showSuccessMessage() {
        Snackbar.make(
            binding.root,
            R.string.booking_completed,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun validateTime(): Boolean {
        val start = (startHourTime * 100) + startMinuteTime
        val end = (endHourTime * 100) + endMinuteTime
        if (start >=  end){
            displayTimeFailedMessage()
            return false
        }
        return true
    }

    private fun displayTimeFailedMessage() {
        Snackbar.make(
            binding.root,
            R.string.time_pick_failed,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun bindElements() {
        calendar = binding.calendarView
        startButton = binding.startHourBtn
        endButton = binding.endHourBtn
        bookmarkButton = binding.bookBtn
        bookingsButton = binding.bookingsBtn
        bookingsButton = binding.bookingsBtn
    }

    private fun setStartTime(hour: Int,minute: Int){
        var min: String = minute.toString()
        if (minute<10)min = "0$minute"
        startTime = "$hour:$min"
        startButton.text = startTime
    }

    private fun setEndTime(hour: Int,minute: Int){
        var min: String = minute.toString()
        if (minute<10)min = "0$minute"
        endTime = "$hour:$min"
        endButton.text = endTime
    }
}