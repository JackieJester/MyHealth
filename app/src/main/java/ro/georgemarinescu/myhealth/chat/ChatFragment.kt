package ro.georgemarinescu.myhealth.chat

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import autodispose2.android.autoDispose
import autodispose2.androidx.lifecycle.autoDispose
import autodispose2.autoDispose
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.reactivex.rxjava3.core.CompletableSource
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_chat_log.*
import kotlinx.android.synthetic.main.fragment_chat_log.view.*
import ro.georgemarinescu.myhealth.KeyboardManager
import ro.georgemarinescu.myhealth.KeyboardStatus
import ro.georgemarinescu.myhealth.MainActivity
import ro.georgemarinescu.myhealth.R
import ro.georgemarinescu.myhealth.models.Conversation
import ro.georgemarinescu.myhealth.models.Message
import ro.georgemarinescu.myhealth.models.Profile
import ro.georgemarinescu.myhealth.videochat.FirebaseData
import ro.georgemarinescu.myhealth.videochat.VideoCallActivity
import java.io.File


class ChatFragment : Fragment() {
    lateinit var list: ArrayList<Message>
    lateinit var adapter: ChatAdapter
    var currentUserId: String? = null
    var otherUserId: String? = null
    var messages = arrayListOf<Message>()
    lateinit var keyboardManager: KeyboardManager
    lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        storageRef = FirebaseStorage.getInstance().getReference("images")
        adapter = ChatAdapter()
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showSimpleDialog(view!!)

                }
            }
            )
        super.onCreate(savedInstanceState)

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.call_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun showSimpleDialog(view: View){
        val builder:AlertDialog.Builder = AlertDialog.Builder(context)
        val linearLayout = LinearLayout(context)
        val ratingBar = RatingBar(context)
        val ref =
            FirebaseDatabase.getInstance().getReference("profiles").child(otherUserId ?: "")

        ratingBar.numStars = 5
        ratingBar.stepSize = 1F


        linearLayout.addView(ratingBar)

        builder.setTitle("Rating")
        builder.setMessage("Please rate you experience with the doctor")
        builder.setView(ratingBar)
        builder.setIcon(R.drawable.call_button)
        builder.setView(linearLayout)

        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            run {
                dialog.dismiss()
                findNavController().popBackStack()
            }
        })
        builder.setNegativeButton("Cancel",DialogInterface.OnClickListener{dialog, which ->
            run {
                dialog.dismiss()
                findNavController().popBackStack()
            } })
        builder.setNeutralButton("No",DialogInterface.OnClickListener{dialog, which ->
            run {
                dialog.dismiss()
                findNavController().popBackStack()
            }
        })

        val alertDialog:AlertDialog = builder.create()
        alertDialog.show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.call_action) {
            Toast.makeText(context, "Call pressed", Toast.LENGTH_SHORT).show()
            startVideoCall()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val localFile = File.createTempFile(currentUserId, "png")
        val userImageRef = storageRef.child("$currentUserId.png")


        return inflater.inflate(R.layout.fragment_chat_log, container, false)
    }

    private fun startVideoCall() {
        FirebaseData.getCallStatusReference(currentUserId!!).setValue(true)
        FirebaseData.getCallIdReference(otherUserId!!).onDisconnect().removeValue()
        FirebaseData.getCallIdReference(otherUserId!!).setValue(currentUserId)
        VideoCallActivity.startCall(context!!, otherUserId!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        keyboardManager = KeyboardManager(activity as Activity)
        list = ArrayList<Message>()
        adapter.items = list
        adapter.currentUserID = currentUserId ?: ""
        view.recyclerview_chatlog.adapter = adapter
        view.recyclerview_chatlog.layoutManager = LinearLayoutManager(context)
        adapter.notifyDataSetChanged()
        val conversationID = arguments?.getString("id")
        val doctorId = arguments?.getString("doctorId")
        otherUserId = doctorId
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
                    if(currentUserId == otherUserId)
                        otherUserId = conversation?.idPacient
                    return
                }
                conversation = Conversation(
                    conversationID!!, doctorId!!, currentUserId!!,
                    arrayListOf(Message("Conversation Started", currentUserId!!))
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
                messages.add(Message(chatText.toString(), currentUserId!!))
                ref.child("messages").setValue(messages)
                editText_chatlog.text.clear()
            }
        }

        keyboardManager.standardHeight = activity!!.findViewById<View>(android.R.id.content).height
        keyboardManager.status().autoDispose(this).subscribe {
            recyclerview_chatlog.scrollToPosition(messages.size - 1)
        }
    }
}
