package com.example.engenieer

import com.example.engenieer.buildings.BuildingItem
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

object FirebaseHandler {
    object RealtimeDatabase{
        private const val usersPath: String = "users"
        private const val buildingsPath: String = "buildings"

        private val firebaseDatabase by lazy {
            Firebase.database("https://engenieer-45947-default-rtdb.europe-west1.firebasedatabase.app/")
        }

        private fun getUsersReference(): DatabaseReference{
            return firebaseDatabase.reference.child(usersPath)
        }

        private fun getUserReference(userUID: String): DatabaseReference{
            return getUsersReference().child(userUID)
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

        fun addNewBuilding(building: BuildingItem){
            getBuildingsRef().setValue(building)
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