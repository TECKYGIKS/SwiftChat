package com.example.randomchat.fragment

import ChatAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.randomchat.R
import com.example.randomchat.Utils
import com.example.randomchat.adapter.UserAdpater
import com.example.randomchat.databinding.FragmentChatRoomBinding
import com.example.randomchat.databinding.FragmentSearchBinding
import com.example.randomchat.models.Message
import com.example.randomchat.models.UserList
import com.example.randomchat.models.Users
import com.example.randomchat.viewmodel.UserModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var database1: DatabaseReference
    private lateinit var binding: FragmentSearchBinding
    private lateinit var randomList: ArrayList<UserList>
    private var currentChatUser: UserList? = null
    private lateinit var sender: String
    private lateinit var chatAdapter: ChatAdapter
    private val messageList: MutableList<Message> = mutableListOf()
    private lateinit var receiver: String
    private val viewModel : UserModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSearchBinding.inflate(layoutInflater)
        search()
        setupRecyclerView()
        lifecycleScope.launch {
            database = Firebase.database.reference.child("messages")
            database1 = Firebase.database.reference.child("messages")
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
                            randomList = orderedList

                        }

                    }
                    getRandomChat()

                }
            }

        }


        return binding.root
    }

    private fun search() {
        binding.skip.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_self)
        }
    }

    private fun setupSendButton() {
        binding.sendButton.setOnClickListener {
            val message = binding.messageInputEditText.text.toString()
            val mes = Message(sender,receiver,message)
            val newMessageRef = database.push()
            val newMessageRef1 = database1.push()
            newMessageRef.setValue(mes)
            newMessageRef1.setValue(mes)
            binding.messageInputEditText.text.clear()
            scrollToBottom()
        }
        database.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    chatAdapter.addMessage(it)
                    messageList.add(it)
                    scrollToBottom()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    private fun scrollToBottom() {
        binding.chatRecyclerView.scrollToPosition(messageList.size - 1)
    }
    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messageList)
        binding.chatRecyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
    private fun getRandomChat() {
        if (currentChatUser == null) {
            randomList.shuffle()
            var flag = false
            for (user in randomList) {
                if (user.status == true) {
                    flag = true
                    binding.user.title = user.username
                    binding.user.subtitle = "ONLINE"
                    sender = Utils.getCurrentUserId()
                    receiver = user.uid.toString()
                    val chatRoomID = receiver + sender
                    val chatRoomID1 = sender + receiver
                    database = Firebase.database.reference.child("messages").child(chatRoomID)
                    database1 = Firebase.database.reference.child("messages").child(chatRoomID1)
                    setupSendButton()
                    currentChatUser = user
                    break
                }
            }
            if (flag == false) {
                for (user in randomList) {
                    if (user.status == false) {
                        flag = true
                        binding.user.title = user.username
                        binding.user.subtitle = "OFFLINE"
                        sender = Utils.getCurrentUserId()
                        receiver = user.uid.toString()
                        val chatRoomID = receiver + sender
                        val chatRoomID1 = sender + receiver
                        database = Firebase.database.reference.child("messages").child(chatRoomID)
                        database1 = Firebase.database.reference.child("messages").child(chatRoomID1)
                        setupSendButton()
                        currentChatUser = user
                        break
                    }
                }
            }

        }
    }


}

