package duc.thanhhoa.chatduck.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import duc.thanhhoa.chatduck.modal.Users

class ChatAppViewModel: ViewModel() {
    val name= MutableLiveData<String>()
    val imageUrl= MutableLiveData<String>()
    val message= MutableLiveData<String>()

    val usersRepo= UsersRepo()

    fun getUsers(): LiveData<List<Users>>{
        return usersRepo.getUsers()
    }
}