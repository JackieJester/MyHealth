package ro.georgemarinescu.myhealth.conversations

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.layout_conversation_list_item.view.*
import kotlinx.android.synthetic.main.layout_doctor_list_item.view.*
import ro.georgemarinescu.myhealth.R
import ro.georgemarinescu.myhealth.models.Conversation
import ro.georgemarinescu.myhealth.models.Profile
import java.io.File
import java.util.*

class SpecialisationAdapter(var clickListner: OnSpecialisationClickListner, val currentUserId: String):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    var items = emptyList<Profile>()
    var filteredItems = emptyList<Profile>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SpecialisationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_doctor_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is SpecialisationViewHolder ->{
                holder.bind(filteredItems.get(position))
                holder.itemView.setOnClickListener {
                    val doctorId = filteredItems.get(position).id
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

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                filteredItems = if(constraint.isNullOrEmpty()) {
                    items
                } else {
                    val filteredList = mutableListOf<Profile>()
                    items.forEach {element->
                        when {
                            element.name.toLowerCase(Locale.getDefault())
                                .contains(constraint.toString()
                                    .toLowerCase(Locale.getDefault())) -> filteredList.add(element)
                        }
                    }

                    filteredList.toList()
                }

                val filterResults = FilterResults()
                filterResults.values = filteredItems
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results!!.values as List<Profile>
                notifyDataSetChanged()
            }

        }
    }

}
interface OnSpecialisationClickListner{
    fun onClick(conversationId: String,doctorId: String)
}