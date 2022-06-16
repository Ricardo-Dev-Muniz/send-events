package br.com.corelib.repository

import br.com.corelib.calls.ResponseAny
import br.com.corelib.data.Event
import br.com.corelib.data.Person

interface MainRepository {
    suspend fun getEvents(): ResponseAny<Array<Event?>?>
    suspend fun getEventsById(id: String): ResponseAny<Event?>
    suspend fun posChecking(person: Person): ResponseAny<Any>
}