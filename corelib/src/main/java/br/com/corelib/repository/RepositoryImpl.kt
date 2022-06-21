package br.com.corelib.repository

import br.com.corelib.calls.Api
import br.com.corelib.calls.ResponseAny
import br.com.corelib.calls.safeApiCall
import br.com.corelib.data.Event
import br.com.corelib.data.Person

class RepositoryImpl(private val api: Api): MainRepository {
    override suspend fun getEvents(): ResponseAny<Array<Event?>?> = safeApiCall { api.getEvents() }
    override suspend fun getEventsById(id: String): ResponseAny<Event?> = safeApiCall { api.getEventById(id = id) }
    override suspend fun posChecking(person: Person): ResponseAny<Any> = safeApiCall { api.posChecking(person =  person) }
}