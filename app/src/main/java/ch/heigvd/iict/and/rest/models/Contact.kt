package ch.heigvd.iict.and.rest.models

import android.icu.text.SimpleDateFormat
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.*

/**
Contact.kt
 * Contact entity & DTO
Authors:
 * Duruz Florian
 * Ferreira Silva Sven
 * Richard Aurélien
 */

enum class SyncState{
    SYNCED,
    CREATED,
    UPDATED,
    DELETED
}

@Entity
data class Contact(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var name: String,
    var firstname: String?,
    var birthday : Calendar?,
    var email: String?,
    var address: String?,
    var zip: String?,
    var city: String?,
    var type: PhoneType?,
    var phoneNumber: String?,
    var remoteId: Long? = null,
    var syncState: SyncState = SyncState.CREATED
)

// Class for data coming and sent to the remote
@Serializable
data class ContactDTO(
    val id: Long? = null, // remoteId
    val name: String,
    val firstname: String?,
    val birthday: String?,
    val email: String?,
    val address: String?,
    val zip: String?,
    val city: String?,
    val type: PhoneType?,
    val phoneNumber: String?
)

private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US)

// Entity to DTO
fun Contact.toDTO() = ContactDTO(
    id = remoteId,
    name = name,
    firstname = firstname,
    birthday = birthday?.let { isoFormat.format(it.time) },
    email = email,
    address = address,
    zip = zip,
    city = city,
    type = type,
    phoneNumber = phoneNumber
)

// DTO to Entity
fun ContactDTO.toEntity() = Contact(
    remoteId = id, // store the remoteId
    name = name,
    firstname = firstname,
    birthday = birthday?.let { str ->
        Calendar.getInstance().apply { time = isoFormat.parse(str) ?: Date() }
    },
    email = email,
    address = address,
    zip = zip,
    city = city,
    type = type,
    phoneNumber = phoneNumber,
    syncState = SyncState.SYNCED // Data from remote is always synced
)