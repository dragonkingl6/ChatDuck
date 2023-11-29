package duc.thanhhoa.chatduck.mvvm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ChatAppViewModel: ViewModel() {
    val name= MutableLiveData<String>()
    val imageUrl= MutableLiveData<String>()
    val message= MutableLiveData<String>()
}