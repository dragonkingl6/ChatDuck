package duc.thanhhoa.chatduck.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import duc.thanhhoa.chatduck.MyApplication
import duc.thanhhoa.chatduck.SharedPrefs
import duc.thanhhoa.chatduck.Utils
import duc.thanhhoa.chatduck.modal.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatAppViewModel: ViewModel() {
    val name= MutableLiveData<String>()
    val imageUrl= MutableLiveData<String>()
    val message= MutableLiveData<String>()

    val firestore= FirebaseFirestore.getInstance()

    val usersRepo= UsersRepo()

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
}