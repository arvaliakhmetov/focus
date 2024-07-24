package repository.models

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import repository.network.defaultJson
import ruavalfocusdb.Steps

@Serializable
data class FocusMessageDto(
    override val message: FocusMessageType,
    override val clientId: String,
    override val chatId: String,
    val messageId: String,
    val name: String = "",
    val category: String = "UNNAMED",
    val estimatedTime: Int = 0,
    val priority: Int = 0,
    val tag: String = "",
    val comments: List<String> = emptyList(),
    val steps: List<Step> = emptyList(),
    val stepsIsLoading: Boolean? = false,
    val received: Int,
    val canChange: Int ,
    val rawText: String,
    val timestamp: Long
): Messaging()

fun FocusMessageDto.toNote(): Note{
    return Note(
        id = messageId,
        timestamp = timestamp,
        text = rawText,
        status = message.name,
        tag = tag,
    )
}

@Serializable
data class NotesForReceive(
    override val message: FocusMessageType,
    override val clientId: String,
    override val chatId: String,
    val notes: List<FocusMessageDto>
): Messaging()

fun NotesForReceive.toJsonString(): String{
    return defaultJson.encodeToString(this)
}

@Serializable
data class Step(
    val id: Long? = null,
    val note_id: String,
    val isChecked: Long,
    val text: String,
    val step_number: Long
)

fun Steps.toStep(): Step{
    return Step(
        id = id,
        note_id = note_id,
        isChecked = isChecked,
        step_number = step_number,
        text = text
    )
}

@Serializable
enum class Status{
    NEW
}