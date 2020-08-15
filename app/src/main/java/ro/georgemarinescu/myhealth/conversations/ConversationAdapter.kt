package ro.georgemarinescu.myhealth.conversations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_conversation_list_item.view.*
import kotlinx.android.synthetic.main.layoutleft_message_item.view.*
import ro.georgemarinescu.myhealth.OnCardItemClickListner
import ro.georgemarinescu.myhealth.R
import ro.georgemarinescu.myhealth.chat.ChatAdapter
import ro.georgemarinescu.myhealth.models.CardPost
import ro.georgemarinescu.myhealth.models.Conversation
import ro.georgemarinescu.myhealth.models.Message

class ConversationAdapter(var clickListner: OnConversationClickListner): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var items: List <Conversation> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ConversationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_conversation_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ConversationViewHolder->{
                holder.bind(items.get(position).pacient!!.name)
                holder.itemView.setOnClickListener {
                    clickListner.onClick(items.get(position).id)
                }
            }
        }
    }

    class ConversationViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){
        private val conversationView = itemView.conversation_name

        fun bind(name: String){
            conversationView.text = name
        }
    }

}
interface OnConversationClickListner{
    fun onClick(item: String)
}