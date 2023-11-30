package duc.thanhhoa.chatduck.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import duc.thanhhoa.chatduck.Utils
import duc.thanhhoa.chatduck.modal.Messages

class MessageRepo {
    private val firestore = FirebaseFirestore.getInstance()

    fun getMessages(friendid: String) : LiveData<List<Messages>>{

        val messages = MutableLiveData<List<Messages>>()

        val uniqueId = listOf(Utils.getUidLoggedIn(), friendid).sorted()
        uniqueId.joinToString(separator = "")

        firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener{ value, error ->
                if(error!=null){
                    return@addSnapshotListener
                }

                val messageList= mutableListOf<Messages>()

                if (!value!!.isEmpty){
                    value.documents.forEach{documentSnapshot ->

                        val messageModal = documentSnapshot.toObject(Messages::class.java)

                        if(messageModal!!.sender.equals(Utils.getUidLoggedIn())&& messageModal.receiver.equals(friendid)
                            || messageModal!!.sender.equals(friendid)&& messageModal.receiver.equals(Utils.getUidLoggedIn())
                            ){
                            messageModal.let {
                                messageList.add(it!!)
                            }
                        }
                        }
                    messages.value= messageList
                    }
                }
        return messages

    }

}