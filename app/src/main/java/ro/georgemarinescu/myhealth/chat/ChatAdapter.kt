package ro.georgemarinescu.myhealth.chat

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.layoutleft_message_item.view.*
import kotlinx.android.synthetic.main.layoutleft_message_item.view.conversation_imageView
import kotlinx.android.synthetic.main.layoutleft_message_item.view.message_text
import kotlinx.android.synthetic.main.layoutright_message_item.view.*
import ro.georgemarinescu.myhealth.R
import ro.georgemarinescu.myhealth.models.Message
import java.io.File

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
                holder.bind(items.get(position))
            }
        }
    }
    class MessageViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){
        private val messageView = itemView.message_text
        fun bind(message: Message){
            messageView.text = message.text
            var currentUserId: String = message.sender
            val currentUserAvatar = File.createTempFile(currentUserId, "png")
            var storageRef: StorageReference = FirebaseStorage.getInstance().getReference("images")
            val currentUserImageRef = storageRef.child("$currentUserId.png")

            currentUserImageRef.getFile(currentUserAvatar).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(currentUserAvatar.absolutePath)
                itemView.conversation_imageView.setImageBitmap(bitmap)
            }
        }
    }

}