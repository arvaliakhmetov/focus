package repository.models

import kotlinx.serialization.Serializable
import repository.network.defaultJson
import ruavalfocusdb.Notes

@Serializable
data class Note(
    val id: String? = null,
    val timestamp: Long = 0,
    val text: String = "",
    val status: String = "",
    val tag: String = "",
    val name: String = "",
    val category: String = "UNNAMED",
    val steps: List<Step> = emptyList(),
    val estimated_time: Int = 0,
    val priority: Int = 0,
    val comments: List<String> = emptyList()
)

fun Notes.toNote(): Note {
    return Note(
        id = id,
        timestamp = timestamp,
        text = text,
        status = status,
        tag = tag,
        name = title,
        category = "UNNAMED",
        steps = emptyList(),
        estimated_time = 0,
        priority  = priority.toInt(),
        comments = emptyList()
    )
}
