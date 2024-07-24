package repository.network

import repository.database.MainDatabaseRepository

interface WsRepository {
    val wsClient: WsClient
    val db: MainDatabaseRepository

    fun listenSocket()

    fun sendMessage()
}