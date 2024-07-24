package repository.database

import PlatformDispatcher
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.db.SqlDriver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import repository.network.defaultJson
import repository.models.Note
import repository.models.toNote
import ru.aval.focus.db.FocusDatabase
import ruavalfocusdb.Notes
import utils.DateTime

class MainDatabaseRepository(
    sqlDriver: SqlDriver
) {
    val db = FocusDatabase(sqlDriver)
    val query = db.focusDatabaseQueries

    fun getTimer() : Flow<Long?> {
        return query.getTimer()
            .asFlow()
            .mapToOneOrNull(PlatformDispatcher.io)
    }

    suspend fun setTime(time: Long){
        withContext(PlatformDispatcher.io){
            val _time = query.getTimer().executeAsOneOrNull()
            if( _time == null){
                query.setTimer(0,time-100L)
            }else{
                query.updateTimer(_time - 100L)
            }

        }

    }
    suspend fun addNote(note: Note){
        withContext(PlatformDispatcher.io){
            query.addNote(
                id = note.id?:"jhsdkfjhkjh",
                timestamp = DateTime.getCurrentTimeInMilliSeconds(),
                text = note.text,
                tag = note.tag,
                status = note.status,
                category = note.category,
                estimatedTime = note.estimated_time.toLong(),
                title = note.name,
                priority = note.priority.toLong()
            )
        }
    }

    fun getAllNotes(): Flow<List<Notes>>{
        return query.getAllNotes()
            .asFlow()
            .mapToList(PlatformDispatcher.io)
    }

    fun getUpdates():Flow<List<Notes>>{
        return query.getUpdates()
            .asFlow()
            .mapToList(PlatformDispatcher.io)
    }

    fun getNote(id: String): Flow<Note>{
        return query.getNote(id)
            .asFlow()
            .map {
                it.executeAsOne().toNote()
            }

    }

    suspend fun deleteNote(id: String){
        query.deleteNote(id)
    }




}
//https://api.notion.com/v1/databases/9153919b-d179-44b2-8d36-b7c99c3381c2/query