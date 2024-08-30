package com.example.randomchat.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.randomchat.R
import com.example.randomchat.Utils
import com.example.randomchat.adapter.UserAdpater
import com.example.randomchat.databinding.FragmentChatBinding
import com.example.randomchat.models.UserList
import com.example.randomchat.models.Users
import com.example.randomchat.viewmodel.UserModel
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {
    private val viewModel : UserModel by viewModels()
    private lateinit var adapterOrder : UserAdpater
    private lateinit var savedBundle: Bundle
    private lateinit var binding: FragmentChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBinding.inflate(layoutInflater)
        lifecycleScope.launch {
            getAllUsers()
            savedChat()
        }
        search()

        onProfileClick()
        setStatusBarColor()
        return binding.root
    }




    private fun search() {
        binding.search.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_searchFragment)
        }
    }

    private fun onProfileClick() {
        binding.profile.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_profileFragment)
        }

    }

    private fun getAllUsers() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            viewModel.getAllUsers().collect { orderList ->
                if (orderList.isNotEmpty()) {
                    val orderedList = ArrayList<UserList>()
                    for (orders in orderList) {
                        if(Utils.getCurrentUserId()!=orders.uid){
                            val userlist = UserList(
                                orders.uid,
                                orders.userName,
                                orders.status,
                                orders.lastseen,
                            )
                            orderedList.add(userlist)
                        }

                    }
                    adapterOrder = UserAdpater(requireContext())
                    binding.rvOrders.adapter = adapterOrder
                    adapterOrder.differ.submitList(orderedList)
                    binding.shimmerViewContainer.visibility = View.GONE
                }
            }

        }
    }

    private fun savedChat() {
        binding.chats.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_savedChatFragment)
        }
    }

    private fun setStatusBarColor(){
        activity?.window?.apply {
            val statusBarColors = ContextCompat.getColor(requireContext(), R.color.grey)
            statusBarColor = statusBarColors
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }




}
