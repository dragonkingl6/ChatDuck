package duc.thanhhoa.chatduck

import com.google.firebase.auth.FirebaseAuth

class Utils {
    companion object{
        private val auth = FirebaseAuth.getInstance()


        fun getUidLoggedIn(): String {
            var userid: String = ""
            if (auth.currentUser!=null){
                userid = auth.currentUser!!.uid
            }
            return userid
        }
    }
}