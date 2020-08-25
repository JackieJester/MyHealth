package ro.georgemarinescu.myhealth.conversations

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_doctor.view.*
import ro.georgemarinescu.myhealth.R
import ro.georgemarinescu.myhealth.models.Conversation
import ro.georgemarinescu.myhealth.models.Profile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class doctorFragment : Fragment(), OnConversationClickListner {
    lateinit var auth: FirebaseAuth
    private var backPressedTime = 0L
    lateinit var list : ArrayList<Conversation>
    lateinit var adapter: ConversationAdapter
    lateinit var profile:Profile
    val REQUEST_IMAGE_CAPTURE = 1
    var imageInputStream: ByteArrayInputStream? = null
    var userId: String = ""
    lateinit var storageRef: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        storageRef = FirebaseStorage.getInstance().getReference("images")
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser!!.uid
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d("MainScreen", "Fragment back pressed invoked")
                    if(backPressedTime + 2000 > System.currentTimeMillis()){
                        activity!!.finish()

                    }else{
                        Toast.makeText(context,"Press back again to exit", Toast.LENGTH_SHORT).show()
                    }
                    backPressedTime = System.currentTimeMillis()
                }
            }
            )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

            imageInputStream?.let {
                val userImageRef = storageRef.child("$userId.png")
                userImageRef.putStream(it)
            }


        return inflater.inflate(R.layout.fragment_doctor, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == R.id.logOut_action){
            Toast.makeText(context,"Logout pressed", Toast.LENGTH_SHORT).show()
            auth.signOut()
            findNavController().navigate(R.id.loginFragment)
        }
        if(id == R.id.profile_action){
            findNavController().navigate(R.id.accountFragment)
        }

        return super.onOptionsItemSelected(item)
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = ArrayList<Conversation>()
        adapter = ConversationAdapter(this)
       // adapter.items = list
        view.recyclerview_conversation.adapter = adapter
        view.recyclerview_conversation.layoutManager = LinearLayoutManager(context)
      //  adapter.notifyDataSetChanged()
        //todo: get only current user conversation
        val ref = FirebaseDatabase.getInstance().getReference("conversations")
        val currentUser = FirebaseAuth.getInstance().currentUser
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (snapshot in dataSnapshot.children) {
                    val data = snapshot.getValue(Conversation::class.java)!!
                    if(data.idDoctor == currentUser?.uid)
                        list.add(data)
                }
                getProfilesInfo()


            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        ref.addValueEventListener(postListener)
    }

    override fun onClick(item: String) {
        val bundle = bundleOf("id" to item, "doctorId" to userId)
        findNavController().navigate(R.id.chat_logFragment,bundle)
    }
    fun getProfilesInfo(){
        var profileConversationMap = hashMapOf<String,Conversation>()

        for(conversation in list) {
            profileConversationMap.put(conversation.idPacient,conversation)
            profileConversationMap.put(conversation.idDoctor,conversation)
        }
        var profileNumber = profileConversationMap.count()
        val profileListner = object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                profile = dataSnapshot.getValue<Profile>(Profile::class.java)!!
                if(profile.doctor)
                    profileConversationMap[profile.id]!!.doctor = profile
                else
                    profileConversationMap[profile.id]!!.pacient = profile
                profileNumber--
                if(profileNumber == 0) {
                    for(conversation in list) {
                        if(conversation.pacient == null)
                            conversation.pacient = profileConversationMap[conversation.idPacient]!!.pacient
                        if(conversation.doctor == null)
                            conversation.doctor = profileConversationMap[conversation.idDoctor]!!.doctor
                    }
                    adapter.items = list
                    adapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        }
        val ref = FirebaseDatabase.getInstance().getReference("profiles")
        for(id in profileConversationMap.keys){
            ref.child(id).addValueEventListener(profileListner)
        }
    }
}
