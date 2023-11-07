package com.example.engenieer

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.findNavController
import com.example.engenieer.databinding.ActivityMainBinding
import com.example.engenieer.helper.FirebaseHandler

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater

        inflater.inflate(R.menu.app_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navDestination = navController.currentDestination ?: return super.onPrepareOptionsMenu(menu)
        val isVisible = navDestination.id != R.id.loginRegisterFragment
        menu?.findItem(R.id.logout)?.isVisible = isVisible

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.blue)))
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                FirebaseHandler.Authentication.logout()
                findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.loginRegisterFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}