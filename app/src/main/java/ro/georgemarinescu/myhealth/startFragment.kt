package ro.georgemarinescu.myhealth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ro.georgemarinescu.myhealth.models.Profile


class startFragment : Fragment() {

    var firebaseAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    //

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth!!.currentUser
        if (currentUser != null)
        {
            val ref = FirebaseDatabase.getInstance().getReference("profiles").child(currentUser.uid)
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var profile = dataSnapshot.getValue<Profile>(Profile::class.java)!!
                    if(profile.doctor)
                        findNavController().navigate(R.id.doctorFragment)
                    else
                        findNavController().navigate(R.id.mainScreenFragment)
                }

                override fun onCancelled(databaseError: DatabaseError) {

                }
            }
            ref.addValueEventListener(postListener)
        }

        else
            findNavController().navigate(R.id.loginFragment)


        return inflater.inflate(R.layout.fragment_start, container, false)
    }

}