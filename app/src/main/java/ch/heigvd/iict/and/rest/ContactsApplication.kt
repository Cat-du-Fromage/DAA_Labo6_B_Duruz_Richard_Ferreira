package ch.heigvd.iict.and.rest

import android.app.Application
import ch.heigvd.iict.and.rest.database.ContactsDatabase
import ch.heigvd.iict.and.rest.rest.ContactApiService


class ContactsApplication : Application() {

    private val database by lazy { ContactsDatabase.getDatabase(this) }
    private val contactApiService by lazy { ContactApiService() }

    val repository by lazy { ContactsRepository(database.contactsDao(), contactApiService) }
}