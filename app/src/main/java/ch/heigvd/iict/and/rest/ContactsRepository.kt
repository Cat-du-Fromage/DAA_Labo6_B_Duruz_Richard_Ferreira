package ch.heigvd.iict.and.rest

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import ch.heigvd.iict.and.rest.database.ContactsDao
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.SyncState
import ch.heigvd.iict.and.rest.models.toDTO
import ch.heigvd.iict.and.rest.models.toEntity
import ch.heigvd.iict.and.rest.rest.ContactApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ContactsRepository(private val contactsDao: ContactsDao,
                         private val contactService: ContactApiService,
                         private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    // allContacts should not take all all (don't take DELETED contacts) so we use getContacts instead
    //val allContacts = contactsDao.getAllContacts()
    val allContacts = contactsDao.getContacts()

    suspend fun createContact(token: String, contact: Contact) = withContext(dispatcher){
        contact.syncState = SyncState.CREATED
        contact.remoteId = null

        val localId = contactsDao.insert(contact)
        contact.id = localId

        try {
            val contactDTO = contactService.createContact(token,contact.toDTO())

            // success
            contact.remoteId = contactDTO.id
            contact.syncState = SyncState.SYNCED

            contactsDao.update(contact)
        } catch (e: Exception) {
            // failed to create contact - will be synced at a later time
            e.printStackTrace()
        }
    }

    suspend fun updateContact(token: String, contact: Contact) = withContext(dispatcher){
        contact.syncState = SyncState.UPDATED
        contactsDao.update(contact)

        try {
            // update on remote
            val contactDTO = contactService.updateContact(token,contact.toDTO())
            contact.remoteId = contactDTO.id
            contact.syncState = SyncState.SYNCED
            contactsDao.update(contact)
        } catch (e: Exception) {
            // failed to update contact
            e.printStackTrace()
        }
    }

    suspend fun deleteContact(token: String, contact: Contact) = withContext(dispatcher){
        contact.syncState = SyncState.DELETED
        contactsDao.update(contact)

        try {
            val deleted = contactService.deleteContact(token,contact.remoteId!!)
            if (deleted){
                contactsDao.delete(contact)
            }
        } catch (e: Exception) {
            // failed to delete contact
            e.printStackTrace()
        }
    }

    suspend fun synchronize(token: String) = withContext(dispatcher) {
        contactsDao.getContactsAsList(
            SyncState.CREATED,
            SyncState.UPDATED,
            SyncState.DELETED
        ).forEach { contact ->
            try {
                when (contact.syncState) {
                    SyncState.CREATED -> {
                        val dto = contact.toDTO()
                        val createdDto = contactService.createContact(token, dto)
                        contact.remoteId = createdDto.id
                        contact.syncState = SyncState.SYNCED
                        contactsDao.update(contact)
                    }
                    SyncState.UPDATED -> {
                        contact.remoteId?.let {
                            val contactDTO = contactService.updateContact(token,contact.toDTO())
                            contact.remoteId = contactDTO.id
                            contact.syncState = SyncState.SYNCED
                            contactsDao.update(contact)
                        }
                    }
                    SyncState.DELETED -> {
                        contact.remoteId?.let { rid ->
                            val success = contactService.deleteContact(token, rid)
                            if (success) {
                                contactsDao.delete(contact)
                            }
                        } ?: contactsDao.delete(contact) // if no remoteId, delete locally
                    }
                    else -> {} // SYNCED goes here, nothing to sync
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun clearAllContacts() = withContext(dispatcher){
        contactsDao.clearAllContacts()
    }

    suspend fun enroll() : String? = withContext(dispatcher){
        try {
            contactService.enroll()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun fetchContacts(uuid : String) = withContext(dispatcher){
        try {
            val contacts = contactService.getContacts(uuid)
            // Contacts from remote are DTOS, we need to convert them to entities
            for (contactDto in contacts) {
                contactsDao.insert(contactDto.toEntity())
            }
        } catch (e: Exception) {
            // failed to get contacts
            e.printStackTrace()
        }
    }

}