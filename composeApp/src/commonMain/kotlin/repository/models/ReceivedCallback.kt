package repository.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ReceivedCallback(
    override val message: FocusMessageType,
    override val clientId: String,
    override val chatId: String,
    val messageId: String
): Messaging()

fun ReceivedCallback.toJsonString(): String{
    println(Json.encodeToString(this))
    return Json.encodeToString(this)
}
