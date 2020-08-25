package ro.georgemarinescu.myhealth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_main_screen.*
import ro.georgemarinescu.myhealth.videochat.FirebaseData
import ro.georgemarinescu.myhealth.videochat.VideoCallActivity

class MainActivity : AppCompatActivity() {

     lateinit var auth: FirebaseAuth
     var currentUser:FirebaseUser? = null
     lateinit var callRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        FirebaseData.myID = currentUser?.uid ?: ""
        callRef = FirebaseData.database.getReference("calls/${currentUser?.uid}/id")

        }

    private fun receiveVideoCall(key: String) {
        VideoCallActivity.receiveCall(this, key)
    }
    override fun onResume() {
        super.onResume()
        callRef.addValueEventListener(callListener)
    }

    override fun onPause() {
        super.onPause()
        callRef.removeEventListener(callListener)
    }

    private val callListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            if (dataSnapshot.exists()) {
                receiveVideoCall(dataSnapshot.getValue(String::class.java)!!)
                callRef.removeValue()
            }
        }

        override fun onCancelled(p0: DatabaseError) {
            TODO("Not yet implemented")
        }


    }

    override fun onStart() {
        super.onStart()
        currentUser = auth.currentUser
    }
}
