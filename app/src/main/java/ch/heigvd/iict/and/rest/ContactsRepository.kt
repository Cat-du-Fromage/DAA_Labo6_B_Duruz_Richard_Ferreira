package ch.heigvd.iict.and.rest

import ch.heigvd.iict.and.rest.database.ContactsDao
import ch.heigvd.iict.and.rest.models.SyncState

class ContactsRepository(private val contactsDao: ContactsDao) {

    // allContacts should not take all all (don't take DELETED contacts) so we use getContacts instead
    //val allContacts = contactsDao.getAllContacts()
    val allContacts = contactsDao.getContacts()


}