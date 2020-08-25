package ro.georgemarinescu.myhealth

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import ro.georgemarinescu.myhealth.models.Profile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File


public class AccountFragment : Fragment() {
    lateinit var profile:Profile
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var storageRef: StorageReference
    var imageInputStream: ByteArrayInputStream? = null
    var userId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storageRef = FirebaseStorage.getInstance().getReference("images")
        userId = FirebaseAuth.getInstance().currentUser!!.uid
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        val ref = FirebaseDatabase.getInstance().getReference("profiles").child(userId)
        val localFile = File.createTempFile(userId, "png")
        val userImageRef = storageRef.child("$userId.png")

        userImageRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            photo_circle.setImageBitmap(bitmap)
        }

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
        }
        view.photo.setOnClickListener(){
        dispatchTakePictureIntent()

        }
        return view
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(context!!.packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            photo_circle.setImageBitmap(imageBitmap)
            val bos = ByteArrayOutputStream()
            imageBitmap.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
            val bitmapdata: ByteArray = bos.toByteArray()
            imageInputStream = ByteArrayInputStream(bitmapdata)
            photo.alpha = 0f
        }
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
            Toast.makeText(context, "Save succesfull.", Toast.LENGTH_SHORT).show()
            imageInputStream?.let {
                val userImageRef = storageRef.child("$userId.png")
                userImageRef.putStream(it).addOnSuccessListener {
                    Toast.makeText(context, "Save picture", Toast.LENGTH_SHORT).show()
                    if(profile.doctor)
                        findNavController().navigate(R.id.doctorFragment)
                    else
                    findNavController().navigate(R.id.mainScreenFragment)
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed picture", Toast.LENGTH_SHORT).show()
                    if(profile.doctor)
                        findNavController().navigate(R.id.doctorFragment)
                    else
                    findNavController().navigate(R.id.mainScreenFragment)
                }
                return@addOnCompleteListener
            }
            if(profile.doctor)
                findNavController().navigate(R.id.doctorFragment)
            else
            findNavController().navigate(R.id.mainScreenFragment)

        }
    }

}