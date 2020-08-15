package ro.georgemarinescu.myhealth.chat

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import autodispose2.android.autoDispose
import autodispose2.androidx.lifecycle.autoDispose
import autodispose2.autoDispose
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.rxjava3.core.CompletableSource
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.android.synthetic.main.fragment_chat_log.*
import kotlinx.android.synthetic.main.fragment_chat_log.view.*
import ro.georgemarinescu.myhealth.KeyboardManager
import ro.georgemarinescu.myhealth.KeyboardStatus
import ro.georgemarinescu.myhealth.MainActivity
import ro.georgemarinescu.myhealth.R
import ro.georgemarinescu.myhealth.models.Conversation
import ro.georgemarinescu.myhealth.models.Message
import ro.georgemarinescu.myhealth.models.Profile


class ChatFragment : Fragment() {
    lateinit var list: ArrayList<Message>
    lateinit var adapter: ChatAdapter
    var currentUser: Profile? = null
    var otherUser: Profile? = null
    var messages = arrayListOf<Message>()
    lateinit var keyboardManager: KeyboardManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var currentUserUID = FirebaseAuth.getInstance().currentUser?.uid
        keyboardManager = KeyboardManager(activity as Activity)
        list = ArrayList<Message>()
        adapter = ChatAdapter()
        adapter.items = list
        adapter.currentUserID = currentUserUID ?: ""
        view.recyclerview_chatlog.adapter = adapter
        view.recyclerview_chatlog.layoutManager = LinearLayoutManager(context)
        adapter.notifyDataSetChanged()
        val conversationID = arguments?.getString("id")
        val doctorId = arguments?.getString("doctorId")
        val ref =
            FirebaseDatabase.getInstance().getReference("conversations").child(conversationID ?: "")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var conversation = dataSnapshot.getValue<Conversation>(Conversation::class.java)
                conversation?.messages?.let {
                    messages = it
                    adapter.items = it
                    adapter.notifyDataSetChanged()
                    recyclerview_chatlog.scrollToPosition(messages.size - 1)
                    return
                }
                conversation = Conversation(
                    conversationID!!, doctorId!!, currentUserUID!!,
                    arrayListOf(Message("Conversation Started", currentUserUID))
                )
                ref.setValue(conversation).addOnCompleteListener {
                    Toast.makeText(context, "Save succesfull.", Toast.LENGTH_SHORT).show()
                    messages = conversation.messages
                    adapter.items = conversation.messages
                    adapter.notifyDataSetChanged()
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }
        ref.addValueEventListener(postListener)
        send_button_chat_log.setOnClickListener {
            val chatText = editText_chatlog.text
            if (chatText != null && chatText.toString() != "") {
                messages.add(Message(chatText.toString(), currentUserUID!!))
                ref.child("messages").setValue(messages)
                editText_chatlog.text.clear()
            }
        }
        val mainActivity = activity as MainActivity

        //todo: fix this
        keyboardManager.status().autoDispose(this).subscribe {
            Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()
        }
    }
}
