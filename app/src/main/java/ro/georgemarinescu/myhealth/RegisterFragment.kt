package ro.georgemarinescu.myhealth

import android.app.ActionBar
import android.os.Bundle
import android.text.Layout
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_register.*
import ro.georgemarinescu.myhealth.models.DataSource
import ro.georgemarinescu.myhealth.models.Profile


/**
 * A simple [Fragment] subclass.
 */
class RegisterFragment : Fragment() {
    var rg: RadioGroup? = null
    var linear: LinearLayout? = null
    var originalMode : Int? = null
    var buttonClick = AlphaAnimation(1F,0.5F)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        originalMode = activity?.window?.attributes?.softInputMode
        activity?.window?.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
    }
    override fun onDestroy() {
        super.onDestroy()
        originalMode?.let { activity?.window?.setSoftInputMode(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        view.btn_sign_up.setOnClickListener {
            btn_sign_up.startAnimation(buttonClick)
            signUpUser()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         linear = view?.findViewById<LinearLayout>(R.id.specialisation_list_layout)
        switch_btn.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                makeVisible()
                populateSpecialisationList()
            }
            else {
                makeInvisible()
                if(rg != null)
                    linear?.removeView(rg)
            }
        }


    }
    private fun makeVisible(){
        doctor_name.visibility = View.VISIBLE
        doctor_surname.visibility = View.VISIBLE
        doctor_phone.visibility = View.VISIBLE
    }

    private fun makeInvisible(){
        doctor_name.visibility = View.INVISIBLE
        doctor_surname.visibility = View.INVISIBLE
        doctor_phone.visibility = View.INVISIBLE

    }
    private fun populateSpecialisationList(){
        rg = RadioGroup(context)
        rg?.orientation = RadioGroup.VERTICAL
        val option = DataSource.createDataSet()
        for(i in option.indices){
            val rb = RadioButton(context)
            rb.text = option[i].title
            rb.id = View.generateViewId()
            rg?.addView(rb)
        }
        linear?.addView(rg)
    }
    private fun signUpUser(){
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
        //TODO: doctors name,surname and password errors and requestFocus
        (activity as MainActivity).auth.createUserWithEmailAndPassword(view?.username?.text.toString(), view?.password?.text.toString())
            .addOnCompleteListener(activity as MainActivity) { task ->
                if (task.isSuccessful) {
                    val user = (activity as MainActivity).auth.currentUser

                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                //salvarea baza de date
                                val ref = FirebaseDatabase.getInstance().getReference("profiles")
                                var specialization = ""
                                rg?.let {
                                    specialization =  it.findViewById<RadioButton>(it.checkedRadioButtonId)?.text.toString()
                                }
                                val profile = Profile(user.uid,doctor_surname.text.toString(),
                                    doctor_name.text.toString(), doctor_phone.text.toString(),
                                    switch_btn.isChecked, specialization)
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

