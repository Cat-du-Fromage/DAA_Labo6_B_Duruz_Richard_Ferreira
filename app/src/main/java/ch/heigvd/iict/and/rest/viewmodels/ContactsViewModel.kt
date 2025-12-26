package ch.heigvd.iict.and.rest.viewmodels

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import ch.heigvd.iict.and.rest.ContactsApplication
import ch.heigvd.iict.and.rest.models.Contact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.core.content.edit
import java.util.UUID

class ContactsViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = (application as ContactsApplication) .repository
    private val sharedPreferences = application.getSharedPreferences("CONTACTS_PREFERENCES", Context.MODE_PRIVATE)
    private var enrollmentToken : String?
        get() = sharedPreferences.getString("ENROLLMENT_TOKEN", null)
        set(value) {
            sharedPreferences.edit { putString("ENROLLMENT_TOKEN", value.toString()) }
        }

    val allContacts : StateFlow<List<Contact>> = repository.allContacts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = emptyList()
    )

    private val _editionMode = MutableStateFlow(false)
    val editionMode: StateFlow<Boolean> = _editionMode.asStateFlow()

    private val _selectedContact = MutableStateFlow<Contact?>(null)
    val selectedContact: StateFlow<Contact?> = _selectedContact.asStateFlow()

    fun openEditor(contact: Contact?) {
        _selectedContact.value = contact
        _editionMode.value = true
    }

    fun closeEditor() {
        _editionMode.value = false
        _selectedContact.value = null
    }

    fun save(contact: Contact) {
        viewModelScope.launch {
            val token = enrollmentToken ?: return@launch

            if (contact.id == null) { // No id -> new contact
                repository.createContact(token, contact)
            } else { // id -> update
                repository.updateContact(token, contact)
            }
            closeEditor()
        }
    }

    fun delete(contact: Contact) {
        viewModelScope.launch {
            val token = enrollmentToken ?: return@launch
            repository.deleteContact(token, contact)
            closeEditor()
        }
    }

    fun enroll() {
        viewModelScope.launch {
            repository.clearAllContacts()

            enrollmentToken = repository.enroll()
            if (enrollmentToken == null) {
                return@launch // TODO : check what @launch is. autocomplete did it
            }

            repository.fetchContacts(enrollmentToken!!)
        }
    }

    fun refresh() {
        viewModelScope.launch {
            //TODO
        }
    }

}
