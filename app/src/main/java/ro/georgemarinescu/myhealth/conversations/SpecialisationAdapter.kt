package ro.georgemarinescu.myhealth.conversations

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.layout_conversation_list_item.view.*
import kotlinx.android.synthetic.main.layout_doctor_list_item.view.*
import ro.georgemarinescu.myhealth.R
import ro.georgemarinescu.myhealth.models.Conversation
import ro.georgemarinescu.myhealth.models.Profile
import java.io.File

class SpecialisationAdapter(var clickListner: OnSpecialisationClickListner, val currentUserId: String): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List <Profile> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SpecialisationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_doctor_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is SpecialisationViewHolder ->{
                holder.bind(items.get(position))
                holder.itemView.setOnClickListener {
                    val doctorId = items.get(position).id
                    val conversationId = (doctorId + currentUserId).hashCode()
                    clickListner.onClick(conversationId.toString(),doctorId)
                }
            }
        }
    }
    class SpecialisationViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){
        private val doctorView = itemView.doctor_name

        fun bind(profile: Profile){
            doctorView.text = profile.name

            var currentUserId: String = profile.id
            val currentUserAvatar = File.createTempFile(currentUserId, "png")
            var storageRef: StorageReference = FirebaseStorage.getInstance().getReference("images")
            val currentUserImageRef = storageRef.child("$currentUserId.png")

            currentUserImageRef.getFile(currentUserAvatar).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(currentUserAvatar.absolutePath)
                itemView.doctor_list_imageView.setImageBitmap(bitmap)
            }
        }
    }

}
interface OnSpecialisationClickListner{
    fun onClick(conversationId: String,doctorId: String)
}