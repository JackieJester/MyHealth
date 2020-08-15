package ro.georgemarinescu.myhealth.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layoutleft_message_item.view.*
import ro.georgemarinescu.myhealth.R
import ro.georgemarinescu.myhealth.models.Message
private val LEFT  = 1
private val RIGHT = 2

class ChatAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
     var items: List <Message> = ArrayList()
     var currentUserID:String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == LEFT)
            MessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layoutleft_message_item, parent, false)
            )
        else
            MessageViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layoutright_message_item, parent, false)
            )
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position].sender == currentUserID )
            RIGHT
        else
            LEFT

    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MessageViewHolder ->{
                holder.bind(items.get(position).text)
            }
        }
    }
    class MessageViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){
        private val messageView = itemView.message_text

        fun bind(message: String){
            messageView.text = message
        }
    }


}