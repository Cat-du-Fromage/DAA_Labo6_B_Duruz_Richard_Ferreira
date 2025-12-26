package ch.heigvd.iict.and.rest

import ch.heigvd.iict.and.rest.database.ContactsDao
import ch.heigvd.iict.and.rest.models.Contact
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

    fun create(contact: Contact) {
        contactsDao.insert(contact)
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
            null
        }
    }

}