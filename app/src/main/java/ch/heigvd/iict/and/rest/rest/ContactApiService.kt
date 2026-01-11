package ch.heigvd.iict.and.rest.rest

import ch.heigvd.iict.and.rest.models.ContactDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
ContactApiService.kt
 * Api service to interact with remote
Authors:
 * Duruz Florian
 * Ferreira Silva Sven
 * Richard Aurélien
 */
class ContactApiService {
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        engine {
            connectTimeout = 5_000
            socketTimeout = 5_000
        }
    }

    private val baseUrl = "https://daa.iict.ch"

    suspend fun enroll(): String {
        return client.get("$baseUrl/enroll").body()
    }

    suspend fun getContacts(uuid: String): List<ContactDTO> {
        return client.get("$baseUrl/contacts") {
            header("X-UUID", uuid)
        }.body()
    }

    suspend fun createContact(uuid: String, contact: ContactDTO): ContactDTO {
        return client.post("$baseUrl/contacts") {
            header("X-UUID", uuid)
            contentType(ContentType.Application.Json)
            setBody(contact)
        }.body()
    }

    suspend fun updateContact(uuid: String, contact: ContactDTO): ContactDTO {
        return client.put("$baseUrl/contacts/${contact.id}") {
            header("X-UUID", uuid)
            contentType(ContentType.Application.Json)
            setBody(contact)
        }.body()
    }

    suspend fun deleteContact(uuid: String, remoteId: Long): Boolean {
        val response = client.delete("$baseUrl/contacts/$remoteId") {
            header("X-UUID", uuid)
        }

        return response.status == HttpStatusCode.NoContent
    }
}
