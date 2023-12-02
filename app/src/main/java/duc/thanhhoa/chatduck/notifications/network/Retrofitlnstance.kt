package duc.thanhhoa.chatduck.notifications.network

import duc.thanhhoa.chatduck.notifications.Constants.Companion.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Retrofitlnstance {

    //2
    companion object{
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy{
            retrofit.create(NotificationApi::class.java)
        }
    }
}