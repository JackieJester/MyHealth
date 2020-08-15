package ro.georgemarinescu.myhealth.conversations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_doctor.view.*
import kotlinx.android.synthetic.main.fragment_specialisation.view.*
import ro.georgemarinescu.myhealth.R
import ro.georgemarinescu.myhealth.models.Conversation
import ro.georgemarinescu.myhealth.models.Profile


class specialisationFragment : Fragment(), OnSpecialisationClickListner {
    lateinit var list : ArrayList<Profile>
    lateinit var adapter: SpecialisationAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_specialisation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser!!.uid
        list = ArrayList<Profile>()
        adapter = SpecialisationAdapter(this,currentUser)
        adapter.items = list
        view.recyclerview_doctor.adapter = adapter
        view.recyclerview_doctor.layoutManager = LinearLayoutManager(context)
        adapter.notifyDataSetChanged()

        val ref = FirebaseDatabase.getInstance().getReference("profiles")
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (snapshot in dataSnapshot.children) {
                    val data = snapshot.getValue(Profile::class.java)!!
                    if(data.doctor && data.specialisation == arguments?.getString("title"))
                        list.add(data)

                }
                adapter.items = list
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        }
        ref.addValueEventListener(postListener)

    }

    override fun onClick(convesationId: String,doctorId: String) {
        val bundle = bundleOf("id" to convesationId,"doctorId" to doctorId)
        findNavController().navigate(R.id.chat_logFragment,bundle)
    }

}