package ro.georgemarinescu.myhealth

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_main_screen.view.*
import kotlinx.android.synthetic.main.layout_card_list_item.view.*
import ro.georgemarinescu.myhealth.models.CardPost

class RecyclerAdapter(var clickListner: OnCardItemClickListner) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        private var items: List <CardPost> = ArrayList()


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return CardViewHolder(
                        LayoutInflater.from(parent.context).inflate(R.layout.layout_card_list_item,parent,false)
                )
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                when(holder){
                        is CardViewHolder ->{
                                holder.bind(items.get(position),clickListner)
                        }
                }
        }
        override fun getItemCount(): Int {
                return  items.size
        }

        fun submitList(cardlist: List<CardPost>){
                items = cardlist
        }

        class CardViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView){
                val card_image = itemView.card_image
                val card_title = itemView.card_title


                fun bind(cardPost: CardPost, action:OnCardItemClickListner){

                        card_title.setText(cardPost.title)

                        val requestOptions = RequestOptions()
                                .placeholder(R.drawable.ic_launcher_background)
                                .error(R.drawable.ic_launcher_background)

                        Glide.with(itemView.context)
                                .applyDefaultRequestOptions(requestOptions)
                                .load(cardPost.image)
                                .into(card_image)
                        itemView.setOnClickListener{
                                action.onItemClick(cardPost,adapterPosition)
                        }
                }

                }
        }
interface OnCardItemClickListner{
        fun onItemClick(item: CardPost, position: Int)
}