package duc.thanhhoa.chatduck.modal

import java.util.Date

data class Messages(
    val sender: String? = "",
    val receiver: String? = "",
    val message: String? = "",
    val time: String? = "",
    var date: Date? = null,
) {

    val id: String get() = "$sender-$receiver-$message-$time"
}