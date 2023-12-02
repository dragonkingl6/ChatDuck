package duc.thanhhoa.chatduck.notifications.network

import duc.thanhhoa.chatduck.notifications.Constants.Companion.CONTENT_TYPE
import duc.thanhhoa.chatduck.notifications.Constants.Companion.SERVER_KEY
import duc.thanhhoa.chatduck.notifications.entity.PushNotification
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationApi {

    //3
    @Headers("Authorization:key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}