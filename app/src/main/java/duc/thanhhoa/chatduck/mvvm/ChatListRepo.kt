package duc.thanhhoa.chatduck.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import duc.thanhhoa.chatduck.Utils
import duc.thanhhoa.chatduck.modal.RecentChats

class ChatListRepo {

    private val firestore = FirebaseFirestore.getInstance()

    fun getAllChatList(): LiveData<List<RecentChats>>{

        val mainChatList= MutableLiveData<List<RecentChats>>()

        firestore.collection("Conversation${Utils.getUidLoggedIn()}")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if(error!=null){
                    return@addSnapshotListener
                }

                val chatList= mutableListOf<RecentChats>()
                value?.forEach { documentSnapshot ->
                    val chat = documentSnapshot.toObject(RecentChats::class.java)

                    if (chat.sender.equals(Utils.getUidLoggedIn())){

                        chat.let {
                            chatList.add(it)
                        }
                    }

                }
                mainChatList.value= chatList
            }
        return mainChatList
    }
}