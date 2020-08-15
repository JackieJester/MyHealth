package ro.georgemarinescu.myhealth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_register.*
import ro.georgemarinescu.myhealth.models.Profile

/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        view.btn_sign_up.setOnClickListener {

            signUpUser()
        }
        return view
    }
    fun signUpUser(){
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
        (activity as MainActivity).auth.createUserWithEmailAndPassword(view?.username?.text.toString(), view?.password?.text.toString())
            .addOnCompleteListener(activity as MainActivity) { task ->
                if (task.isSuccessful) {
                    val user = (activity as MainActivity).auth.currentUser

                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                //salvarea baza de date
                                val ref = FirebaseDatabase.getInstance().getReference("profiles")
                                val profile = Profile(user.uid,"","", "",switch_btn.isChecked)
                                ref.child(user.uid).setValue(profile)
                                findNavController().navigate(R.id.loginFragment)
                            }
                        }

                } else {
                    Toast.makeText(context,"Sign up failed", Toast.LENGTH_SHORT).show()
                }


            }

}
}

