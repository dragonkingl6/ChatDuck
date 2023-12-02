package duc.thanhhoa.chatduck.mvvm

import android.util.Log
import android.widget.Toast
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
import duc.thanhhoa.chatduck.notifications.entity.NotificationData
import duc.thanhhoa.chatduck.notifications.entity.PushNotification
import duc.thanhhoa.chatduck.notifications.entity.Token
import duc.thanhhoa.chatduck.notifications.network.Retrofitlnstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class ChatAppViewModel: ViewModel() {
    val name= MutableLiveData<String>()
    val imageUrl= MutableLiveData<String>()
    val message= MutableLiveData<String>()

    val firestore= FirebaseFirestore.getInstance()

    val usersRepo= UsersRepo()
    val messageRepo= MessageRepo()
    var token: String? = null
    val recentChatRepo= ChatListRepo()

    val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    init {
        getCurrentUser()
        getRecentChat()

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
            "time" to Utils.getTime(),
            "date" to Date(),
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

                //notification

                firestore.collection("Tokens").document(receiver).addSnapshotListener { value, error ->


                    if (value != null && value.exists()) {


                        val tokenObject = value.toObject(Token::class.java)


                        token = tokenObject?.token


                        val loggedInUsername =
                            mysharedPrefs.getValue("username")!!.split("\\s".toRegex())[0]



                        if (message.value!!.isNotEmpty() && receiver.isNotEmpty()) {

                            PushNotification(
                                NotificationData(loggedInUsername, message.value!!), token!!
                            ).also {
                                sendNotification(it)
                            }

                        } else {


                            Log.e("ChatAppViewModel", "NO TOKEN, NO NOTIFICATION")
                            Log.e("okeoke", "NO TOKEN, NO NOTIFICATION ${token}")
                        }


                    }

                    Log.e("ViewModel", token.toString())



                    if (taskmessage.isSuccessful){

                        message.value = ""



                    }


                }
            }
    }

    fun sendNotification(notification: PushNotification) = viewModelScope.launch {
        try {
            val response = Retrofitlnstance.api.postNotification(notification)
        } catch (e: Exception) {

            Log.e("ViewModelError", e.toString())
            // showToast(e.message.toString())
        }
    }

    fun getMessages(friendid: String): LiveData<List<Messages>> {
        return messageRepo.getMessages(friendid)
    }

    fun getRecentChat(): LiveData<List<RecentChats>>{
        return recentChatRepo.getAllChatList()
    }

    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {

        val context= MyApplication.instance.applicationContext

        val hashMapUser = hashMapOf<String, Any>("username" to name.value!!, "imageUrl" to imageUrl.value!!)

        firestore.collection("Users").document(Utils.getUidLoggedIn()).update(hashMapUser).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                Toast.makeText(context, "Update", Toast.LENGTH_SHORT).show()

            }

        }
        val mysharedPrefs = SharedPrefs(context)
        val friendid=mysharedPrefs.getValue("friendid")

        val hashMapUpdate= hashMapOf<String, Any>("friendsimage" to imageUrl.value!!, "name" to name.value!!, "person" to name.value!!)


        firestore.collection("Conversation${friendid}").document(Utils.getUidLoggedIn()).update(hashMapUpdate)

        firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(friendid!!).update("person", name.value!!)

    }
}