package com.example.randomchat.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.randomchat.R
import com.example.randomchat.Utils
import com.example.randomchat.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
class MainActivity : AppCompatActivity() {
    private lateinit var userReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        lifecycleScope.launch {
            userReference = FirebaseDatabase.getInstance().getReference("Users")
        }


    }
    override fun onStart() {
        super.onStart()

        Toast.makeText(this, "Activity started", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        updateStatus(true)
        super.onResume()
        Toast.makeText(this, "Activity resume", Toast.LENGTH_SHORT).show()
        // Add your onResume logic here
    }

    override fun onPause() {
        val currentTime = System.currentTimeMillis()
        updateStatus(currentTime.toString())
        updateStatus(false)
        super.onPause()
        Toast.makeText(this, "Activity pause", Toast.LENGTH_SHORT).show()
        // Add your onPause logic here
    }
    private fun updateStatus(status: Boolean) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userInfoReference = userReference
                .child("UserInfo")
                .child(currentUser.uid)
                .child("Sensitive")
                .child("status")
            userInfoReference.setValue(status)

        }
    }
    private fun updateStatus(lastseen: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userInfoReference = userReference
                .child("UserInfo")
                .child(currentUser.uid)
                .child("Sensitive")
                .child("lastseen")
            userInfoReference.setValue(lastseen)

        }
    }
}
