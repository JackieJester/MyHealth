package ro.georgemarinescu.myhealth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_register.*
import kotlinx.android.synthetic.main.fragment_register.btn_sign_up
import ro.georgemarinescu.myhealth.models.Profile


public class LoginFragment : Fragment()   {

    private var backPressedTime = 0L
    var buttonClick = AlphaAnimation(1F,0.5F)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.setTitle("MyHealth")
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
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        //(activity as MainActivity).auth
        view.btn_sign_up.setOnClickListener{
            btn_sign_up.startAnimation(buttonClick)
            findNavController().navigate(R.id.registerFragment)

        }
        view.btn_log_in.setOnClickListener{
            btn_log_in.startAnimation(buttonClick)
            hideKeyboard()
            doLogin()
                }

        return view
    }


    private fun doLogin() {
        if(view?.username?.text.toString().isEmpty()){
            view?.username?.error = "Please enter email"
            view?.username?.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(view?.username?.text.toString()).matches()){
            view?.username?.error = "Please enter valid email"
            view?.username?.requestFocus()
            return
        }
        if(view?.password?.text.toString().isEmpty()){
            view?.password?.error = "Please enter password"
            view?.password?.requestFocus()
            return
        }
        (activity as MainActivity).auth.signInWithEmailAndPassword(view?.username?.text.toString(), view?.password?.text.toString())
            .addOnCompleteListener(activity as MainActivity) { task ->
                if (task.isSuccessful) {
                    val user =  (activity as MainActivity).auth.currentUser
                    updateUI(user)
                } else {
                    Toast.makeText(context, "Login failed.",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }


            }

    }
    private fun updateUI(currentUser: FirebaseUser?) {

        if (currentUser != null) {
            if(currentUser.isEmailVerified){
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
                        // Getting Post failed, log a message
                        //Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                        // ...
                    }
                }
                ref.addValueEventListener(postListener)
            } else{
                Toast.makeText(
                    context,"Please verify your email address.",Toast.LENGTH_SHORT
                ).show()
            }
        }else {
            Toast.makeText(
                context, "login failed.", Toast.LENGTH_SHORT
            ).show()
        }
    }

}
