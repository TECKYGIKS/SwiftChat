package com.example.randomchat.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.randomchat.R
import com.example.randomchat.Utils
import com.example.randomchat.adapter.SavedUserListAdapter
import com.example.randomchat.databinding.FragmentSavedChatBinding
import com.example.randomchat.models.CombinedData
import com.example.randomchat.models.UserList
import com.example.randomchat.viewmodel.UserModel
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class SavedChatFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var adapterOrder : SavedUserListAdapter
    private val viewModel : UserModel by viewModels()
    private lateinit var binding: FragmentSavedChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSavedChatBinding.inflate(layoutInflater)
        back()
        getAllUsers()
        return binding.root
    }
    private fun back() {
        binding.tbProfileFragment.setOnClickListener{
            findNavController().navigate(R.id.action_savedChatFragment_to_chatFragment)
        }
    }
    private fun getAllUsers() {
        binding.shimmerViewContainer.visibility = View.VISIBLE
        lifecycleScope.launch {
            combine(
                viewModel.getAllUsers(),
                viewModel.getAllUsersWithMessageID()
            ) { userList, roomIDs ->
                val orderedList = ArrayList<UserList>()
                for (orders in userList) {
                    if (Utils.getCurrentUserId() != orders.uid) {
                        val userlist = UserList(
                            orders.uid,
                            orders.userName,
                            orders.status,
                            orders.lastseen
                        )
                        for(str in roomIDs){
                            if(str.contains(userlist.uid!!) && str.contains(Utils.getCurrentUserId())){
                                orderedList.add(userlist)
                                break
                            }
                        }

                    }
                }

                CombinedData(orderedList, roomIDs)
            }.collect { combinedData ->
                // After processing, update UI if needed
                adapterOrder = SavedUserListAdapter(requireContext(), ::onOrderItemViewClicked , ::onItemLongClicked)
                binding.rv.adapter = adapterOrder
                adapterOrder.differ.submitList(combinedData.users)
                binding.shimmerViewContainer.visibility = View.GONE
            }
        }
    }
    private fun onItemLongClicked(user1: String , user2: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Chat")
            .setMessage("Are you sure you want to delete this chat?")
            .setPositiveButton("Delete") { dialog, _ ->
                deleteChat(user1,user2)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteChat(user1: String, user2:String) {
        lifecycleScope.launch {
            viewModel.deleteUser(user1,user2)
            getAllUsers()
        }
    }

    private fun onOrderItemViewClicked(orderedItems: UserList){
        val bundle = Bundle()
        bundle.putString("uid","${orderedItems.uid}")
        bundle.putString("username", "${orderedItems.username}")
        bundle.putString("status", "${orderedItems.status}")
        viewModel.setSharedData(bundle)
        findNavController().navigate(R.id.action_savedChatFragment_to_chatRoomFragment , bundle)
    }

}
