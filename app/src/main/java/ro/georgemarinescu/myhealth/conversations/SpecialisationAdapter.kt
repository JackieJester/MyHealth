package ro.georgemarinescu.myhealth.conversations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_conversation_list_item.view.*
import kotlinx.android.synthetic.main.layout_doctor_list_item.view.*
import ro.georgemarinescu.myhealth.R
import ro.georgemarinescu.myhealth.models.Conversation
import ro.georgemarinescu.myhealth.models.Profile

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
                holder.bind(items.get(position).name)
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

        fun bind(name: String){
            doctorView.text = name
        }
    }

}
interface OnSpecialisationClickListner{
    fun onClick(conversationId: String,doctorId: String)
}