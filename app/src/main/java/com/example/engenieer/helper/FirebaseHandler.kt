package com.example.engenieer.helper

import android.graphics.Bitmap
import android.net.Uri
import com.example.engenieer.buildings.BuildingDB
import com.example.engenieer.buildings.BuildingItem
import com.example.engenieer.rooms.RoomDB
import com.example.engenieer.rooms.RoomItem
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

object FirebaseHandler {
    object RealtimeDatabase{
        private const val usersPath: String = "users"
        private const val buildingsPath: String = "buildings"
        private const val storagePath: String = "storage"
        private const val roomsPath: String = "rooms"

        private val firebaseDatabase by lazy {
            Firebase.database("https://engenieer-45947-default-rtdb.europe-west1.firebasedatabase.app/")
        }

        private fun getUsersReference(): DatabaseReference{
            return firebaseDatabase.reference.child(usersPath)
        }

        private fun getUserReference(userUID: String): DatabaseReference{
            return getUsersReference().child(userUID)
        }

        private fun getStorageRef(): StorageReference {
            return FirebaseStorage.getInstance().getReference(storagePath)
        }

        private fun getBuildingsStorageRef(): StorageReference{
            return getStorageRef().child(buildingsPath)
        }

        fun getBuildingStorageRef(buildingID: String): StorageReference{
            return getBuildingsStorageRef().child(buildingID)
        }

        fun registerNewUserInDatabase(){
            val userUID: String = Authentication.getUserUid().toString()
            val userEmail: String = Authentication.getUserEmail().toString()
            val userReference = getUserReference(userUID)
            userReference.child("email").setValue(userEmail)
            userReference.child("isAdmin").setValue(false)
        }

        fun getUserAccessRef(): DatabaseReference{
            val userUID: String = Authentication.getUserUid().toString()
            val userReference: DatabaseReference = getUserReference(userUID)
            return userReference.child("isAdmin")
        }

        fun getBuildingsRef(): DatabaseReference{
            return firebaseDatabase.reference.child(buildingsPath)
        }

        fun getBuildingRef(buildingID: String): DatabaseReference{
            return getBuildingsRef().child(buildingID)
        }

        fun addNewBuilding(building: BuildingItem){
            getBuildingsRef().child(building.buildingID).setValue(
                BuildingDB(
                    building.name,
                    building.description,
                    building.shortDescription,
                    building.photo
                )
            )
        }

        fun uploadBuildingsPhoto(photoID: String, resource: Bitmap){
            getBuildingsStorageRef().child(photoID).putBytes(bitmapToByte(resource))
        }

        private fun getRoomsStorageRef(): StorageReference{
            return getStorageRef().child(roomsPath)
        }

        fun getRoomStorageRef(buildingID: String): StorageReference{
            return getRoomsStorageRef().child(buildingID)
        }

        fun uploadRoomsPhoto(photoID: String,buildingID: String, resource: Bitmap){
            getRoomStorageRef(buildingID).child(photoID).putBytes(bitmapToByte(resource))
        }

        fun getRoomsRef(): DatabaseReference{
            return firebaseDatabase.reference.child(roomsPath)
        }

        fun addNewRoom(room: RoomItem){
            getRoomsRef().child(room.buildingID).child(room.id).setValue(
                RoomDB(
                    room.name,
                    room.description,
                    room.shortDescription,
                    room.photo
                )
            )
        }

        private fun bitmapToByte(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            return stream.toByteArray()
        }

    }

    object Authentication{
        private val firebaseAuth by lazy { Firebase.auth }

        fun login(email: String, password: String): Task<AuthResult> {
            return firebaseAuth.signInWithEmailAndPassword(email, password)
        }

        fun register(email: String, password: String): Task<AuthResult> {
            return firebaseAuth.createUserWithEmailAndPassword(email, password)
        }

        fun getUserEmail():String?{
            return firebaseAuth.currentUser?.email
        }

        fun getUserUid():String?{
            return firebaseAuth.currentUser?.uid
        }

        fun isLoggedIn(): Boolean {
            return firebaseAuth.currentUser != null
        }

        fun logout() {
            firebaseAuth.signOut()
        }
    }
}