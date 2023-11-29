package duc.thanhhoa.chatduck.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import duc.thanhhoa.Utils
import duc.thanhhoa.chatduck.modal.Users

class UsersRepo {
    private var firestore= FirebaseFirestore.getInstance()
    fun getUsers() :LiveData<List<Users>>{
        val users= MutableLiveData<List<Users>>()

            firestore.collection("Users").addSnapshotListener{ snapshot, exception ->
                if (exception!=null){
                    return@addSnapshotListener
                }
                val usersList= mutableListOf<Users>()
                snapshot?.documents?.forEach {
                    val user= it.toObject(Users::class.java)
                    if (user!!.userid != Utils.getUidLoggedIn()){
                        user.let {
                            usersList.add(it)
                        }
                    }
                }
                users.value= usersList

            }
        return users
    }
}