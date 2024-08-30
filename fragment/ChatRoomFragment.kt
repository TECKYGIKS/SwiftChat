package com.example.randomchat.fragment

import ChatAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.randomchat.Utils
import com.example.randomchat.databinding.FragmentChatRoomBinding
import com.example.randomchat.models.Message
import com.example.randomchat.viewmodel.UserModel
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
class ChatRoomFragment : Fragment() {
    private lateinit var chatAdapter: ChatAdapter
    private val viewModel: UserModel by viewModels()
    private lateinit var database: DatabaseReference
    private lateinit var database1: DatabaseReference
    private lateinit var binding: FragmentChatRoomBinding
    private lateinit var sender: String
    private lateinit var receiver: String
    private val messageList: MutableList<Message> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        setupRecyclerView()
        receiver = arguments?.getString("uid")!!
        sender = Utils.getCurrentUserId()
        val chatRoomID = receiver + sender
        val chatRoomID1 = sender + receiver
        lifecycleScope.launch {
            database = Firebase.database.reference.child("messages").child(chatRoomID)
            database1 = Firebase.database.reference.child("messages").child(chatRoomID1)
        }
        setupSendButton()
        getValues()
        return binding.root
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(messageList)
        binding.chatRecyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun scrollToBottom() {
        binding.chatRecyclerView.scrollToPosition(messageList.size - 1)
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

    private fun getValues() {
        val username = arguments?.getString("username")
        val status = arguments?.getString("status")
        Utils.showToast(requireContext(), username ?: "Username not found")
        username?.let { binding.user.title = it.uppercase() }
        status?.let { binding.user.subtitle = if (it == "true") "ONLINE" else "OFFLINE" }
    }
}
