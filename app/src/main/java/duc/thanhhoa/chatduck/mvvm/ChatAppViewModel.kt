package duc.thanhhoa.chatduck.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import duc.thanhhoa.chatduck.MyApplication
import duc.thanhhoa.chatduck.SharedPrefs
import duc.thanhhoa.chatduck.Utils
import duc.thanhhoa.chatduck.modal.Messages
import duc.thanhhoa.chatduck.modal.RecentChats
import duc.thanhhoa.chatduck.modal.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel: ViewModel() {
    val name= MutableLiveData<String>()
    val imageUrl= MutableLiveData<String>()
    val message= MutableLiveData<String>()

    val firestore= FirebaseFirestore.getInstance()

    val usersRepo= UsersRepo()
    val messageRepo= MessageRepo()
    val recentChatRepo= ChatListRepo()

    init {
        getCurrentUser()
    }
    fun getUsers(): LiveData<List<Users>>{
        return usersRepo.getUsers()
    }

    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext


        firestore.collection("Users").document(Utils.getUidLoggedIn())
            .addSnapshotListener { value, error ->


                if (value!!.exists() && value != null) {

                    val users = value.toObject(Users::class.java)
                    name.value = users?.username!!
                    imageUrl.value = users.imageUrl!!


                    val mysharedPrefs = SharedPrefs(context)
                    mysharedPrefs.setValue("username", users.username!!)


                }


            }


    }

    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) = viewModelScope.launch(Dispatchers.IO) {

        val context= MyApplication.instance.applicationContext

        val hashMap = hashMapOf<String, Any>(
            "sender" to sender,
            "receiver" to receiver,
            "message" to message.value!!,
            "time" to Utils.getTime()
        )

        val uniqueId = listOf(sender, receiver).sorted()
        uniqueId.joinToString(separator = "")

        val friendnamesplit = friendname.split("\\s".toRegex())[0]
        val mysharedPrefs = SharedPrefs(context)
        mysharedPrefs.setValue("friendid", receiver)
        mysharedPrefs.setValue("chatroomid", uniqueId.toString())
        mysharedPrefs.setValue("friendname", friendnamesplit)
        mysharedPrefs.setValue("friendimage", friendimage)


        firestore.collection("Messages").document(uniqueId.toString())
            .collection("chats").document(Utils.getTime()).set(hashMap).addOnCompleteListener { taskmessage ->

                val hashMapForRecent= hashMapOf<String, Any>(
                    "friendid" to receiver,
                    "time" to Utils.getTime(),
                    "sender" to Utils.getUidLoggedIn(),
                    "message" to message.value!!,
                    "friendsimage" to friendimage,
                    "name" to friendname,
                    "person" to name.value!!
                )

                firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(receiver).set(hashMapForRecent)

                firestore.collection("Conversation${receiver}")
                    .document(Utils.getUidLoggedIn())
                    .update("message", message.value!!, "time", Utils.getTime(), "person", name.value!!)

                if (taskmessage.isSuccessful) {

                    message.value = ""

                }
            }
    }

    fun getMessages(friendid: String): LiveData<List<Messages>> {
        return messageRepo.getMessages(friendid)
    }

    fun getRecentChat(): LiveData<List<RecentChats>>{
        return recentChatRepo.getAllChatList()
    }
}