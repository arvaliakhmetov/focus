package repository.models

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Message (
    override val message: FocusMessageType,
    override val clientId: String,
    override val chatId: String
):Messaging()

@Serializable
abstract class Messaging {
    abstract val message: FocusMessageType
    abstract val clientId: String
    abstract val chatId: String
}

fun Message.toJsonString(): String{
    println(Json.encodeToString(this))
    return Json.encodeToString(this)
}


@Serializable
enum class FocusMessageType{
    AUTH,UPDATES,SYNC,TIMER,MESSAGE,DELETE
}