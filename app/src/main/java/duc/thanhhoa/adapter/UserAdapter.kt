package duc.thanhhoa.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import duc.thanhhoa.chatduck.R
import duc.thanhhoa.chatduck.modal.Users

class UserAdapter : RecyclerView.Adapter<UserViewHolder>() {

    private var listOfUsers= listOf<Users>()
    private var listener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.userlistitem,parent,false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listOfUsers.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val users = listOfUsers[position]
        val name = users.username!!.split("\\s".toRegex())[0]

        holder.nameProfile.setText(name)
        if (users.status.equals("Online")){
            holder.statusImageView.setImageResource(R.drawable.onlinestatus)
        }else{
            holder.statusImageView.setImageResource(R.drawable.offlinestatus)
        }

        Glide.with(holder.itemView.context)
            .load(users.imageUrl)
            .into(holder.imageProfile)

        holder.itemView.setOnClickListener {
            listener?.onUserSelected(position,users)
        }
    }

    fun setUserList(list: List<Users>){
        this.listOfUsers = list
        notifyDataSetChanged()
    }
    fun setOnClickListener(listener: OnItemClickListener){
        this.listener = listener
    }
}

class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val nameProfile: TextView = itemView.findViewById(R.id.userName)
    val imageProfile:CircleImageView = itemView.findViewById(R.id.imageViewUser)
    val statusImageView : ImageView = itemView.findViewById(R.id.statusOnline)
}

interface OnItemClickListener{
    fun onUserSelected(position: Int, users: Users)
}