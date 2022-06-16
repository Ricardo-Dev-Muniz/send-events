package br.com.corelib.calls

import br.com.corelib.data.Event
import br.com.corelib.data.Person
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface Api {
    @GET("events")
    suspend fun getEvents(): Response<Array<Event?>?>

    @GET(value = "/events/{id}")
    suspend fun getEventById( @Path("id") id: String): Response<Event?>

    @POST("checkin")
    suspend fun posChecking(@Body person: Person): Response<Any>
}