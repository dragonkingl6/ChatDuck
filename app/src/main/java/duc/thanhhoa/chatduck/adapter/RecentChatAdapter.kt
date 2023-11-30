package duc.thanhhoa.chatduck.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import duc.thanhhoa.chatduck.R
import duc.thanhhoa.chatduck.modal.RecentChats

class RecentChatAdapter: RecyclerView.Adapter<RecentChatHolder>() {
    private var listOfChat= listOf<RecentChats>()
    private var listener: OnRecentChatListener? = null
    private var recentModal = RecentChats()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentChatHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recentchatlist, parent, false)

        return RecentChatHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfChat.size
    }

    override fun onBindViewHolder(holder: RecentChatHolder, position: Int) {


        val recentChatList= listOfChat[position]

        recentModal= recentChatList

        holder.userName.setText(recentChatList.name)

        val themeMessage= recentChatList.message!!.split(" ").take(2).joinToString(" ")
        val makeLastMessage= "${recentChatList.person}: ${themeMessage} "

        holder.lastMessage.setText(makeLastMessage)

        Glide.with(holder.itemView.context).load(recentChatList.friendsimage).into(holder.imageView)

        holder.timeView.setText(recentChatList.time!!.substring(0,5))


        holder.itemView.setOnClickListener {
            listener?.getRecentChat(position, recentChatList )
        }
    }

    fun setOnRecentChatListener(listener: OnRecentChatListener){
        this.listener = listener
    }

    fun setOnRecentChatList(list: List<RecentChats>){
        this.listOfChat= list
    }
}

class RecentChatHolder(itemView: View): ViewHolder(itemView){

    val imageView: CircleImageView = itemView.findViewById(R.id.recentChatImageView)
    val userName: TextView = itemView.findViewById(R.id.recentChatTextName)
    val lastMessage: TextView = itemView.findViewById(R.id.recentChatTextLastMessage)
    val timeView: TextView = itemView.findViewById(R.id.recentChatTextTime)

}
interface OnRecentChatListener{
    fun getRecentChat(position: Int, recentChats: RecentChats)
}