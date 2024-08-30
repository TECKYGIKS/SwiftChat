package com.example.randomchat.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.randomchat.R
import com.example.randomchat.Utils
import com.example.randomchat.activity.MainActivity
import com.example.randomchat.authactivity.GoogleAuthActivity
import com.example.randomchat.databinding.FragmentProfileBinding
import com.example.randomchat.intro.UserNameActivity
import com.example.randomchat.viewmodel.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var database: DatabaseReference
    private lateinit var userReference: DatabaseReference
    private val viewModel : UserModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        lifecycleScope.launch {
            database = Firebase.database.reference.child("Users").child("UserInfo")
        }
        lifecycleScope.launch {
            userReference = FirebaseDatabase.getInstance().getReference("Users")
        }
        back()
        showUsername()
        logout()
        return binding.root
    }

    private fun back() {
        binding.tbOrderFragment.setOnClickListener{
            findNavController().navigate(R.id.action_profileFragment_to_chatFragment)
        }
    }

    private fun logout() {
        binding.logout.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            updateStatus(currentTime.toString())
            updateStatus(false)
            val builder = AlertDialog.Builder(requireContext())
            val alertDialog = builder.create()
            builder.setTitle("Log out")
                .setMessage("Do you want to logout?")
                .setPositiveButton("Yes"){ _,_->
                    viewModel.logoutUser()
                    startActivity(Intent(requireContext(), GoogleAuthActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("No"){ _,_->
                    alertDialog.dismiss()
                }

                .show()
                .setCancelable(false)



        }
    }

    private fun showUsername() {
        val userRef = database.child(Utils.getCurrentUserId()).child("Name").child("userName")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val username = snapshot.getValue(String::class.java)
                    println("Username exists: $username")
                    binding.tvusername.text=username
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("Failed to read username: ${error.message}")
            }
        })
        val userRef1 = database.child(Utils.getCurrentUserId()).child("Sensitive").child("email")
        userRef1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val username = snapshot.getValue(String::class.java)
                    println("Username exists: $username")
                    if(!username.isNullOrEmpty() && username!="null"){
                        binding.emailphone.text=username
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("Failed to read username: ${error.message}")
            }
        })
        val userRef2 = database.child(Utils.getCurrentUserId()).child("Sensitive").child("userPhoneNumber")
        userRef2.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val username = snapshot.getValue(String::class.java)
                    println("Username exists: $username")
                    if(!username.isNullOrEmpty() || username!="null"){
                        binding.emailphone.text=username
                    }

                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("Failed to read username: ${error.message}")
            }
        })
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