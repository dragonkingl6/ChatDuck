package duc.thanhhoa.chatduck

import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date

class Utils {
    companion object{
        private val auth = FirebaseAuth.getInstance()

        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2


        fun getUidLoggedIn(): String {
            var userid: String = ""
            if (auth.currentUser!=null){
                userid = auth.currentUser!!.uid
            }
            return userid
        }

        fun getTime() : String{
            val formatter = SimpleDateFormat("HH:mm:ss")
            val date : Date = Date(System.currentTimeMillis())
            val stringDate= formatter.format(date)


            return stringDate
        }
    }
}