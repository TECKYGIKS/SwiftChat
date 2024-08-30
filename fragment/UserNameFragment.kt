package com.example.randomchat.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.randomchat.R
import com.example.randomchat.Utils
import com.example.randomchat.activity.MainActivity
import com.example.randomchat.databinding.FragmentUserNameBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

class UserNameFragment : Fragment() {
    private lateinit var userReference: DatabaseReference
    private lateinit var binding : FragmentUserNameBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserNameBinding.inflate(layoutInflater)
        lifecycleScope.launch {
            userReference = FirebaseDatabase.getInstance().getReference("Users")
        }

        onSaveButton()
        return binding.root
    }
    private fun onSaveButton() {
        binding.btnSave.setOnClickListener{
            val username = binding.username.text.toString()
            updateUserName(username)
            startActivity(Intent(requireActivity() , MainActivity::class.java))
            requireActivity().finish()
        }
    }
    private fun updateUserName(username: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userInfoReference = userReference
                .child("UserInfo")
                .child(currentUser.uid)
                .child("Name")
                .child("userName")
            userInfoReference.setValue(username)

        }
    }
}