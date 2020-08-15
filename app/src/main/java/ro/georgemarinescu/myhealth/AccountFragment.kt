package ro.georgemarinescu.myhealth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import ro.georgemarinescu.myhealth.models.Profile

public class AccountFragment : Fragment() {
    lateinit var profile:Profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("profiles").child(userId)
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                profile = dataSnapshot.getValue<Profile>(Profile::class.java)!!
                view.profile_name.setText(profile.name)
                view.profile_surname.setText(profile.surname)
                view.profile_phone.setText(profile.phone)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        ref.addValueEventListener(postListener)
        view.btn_update_profile.setOnClickListener{
            saveProfile()
            findNavController().navigate(R.id.mainScreenFragment)

        }
        return view
    }

    private fun saveProfile(){
        val name = profile_name.text.toString().trim()
        val surname = profile_surname.text.toString().trim()
        val phone = profile_phone.text.toString().trim()
        val userId = FirebaseAuth.getInstance().currentUser!!.uid

        if(name.isEmpty()){
            profile_name.error = "Please enter your name"
            return
        }
        if(surname.isEmpty()){
            profile_surname.error = "Please enter your surname"
            return
        }
        if(phone.isEmpty()){
            profile_phone.error = "Please enter your phone number"
            return
        }


        val ref = FirebaseDatabase.getInstance().getReference("profiles")
        profile.name = name
        profile.surname = surname
        profile.phone = phone


        ref.child(userId).setValue(profile).addOnCompleteListener{
            Toast.makeText(context, "Save succesfull.",Toast.LENGTH_SHORT).show()
        }
    }

}