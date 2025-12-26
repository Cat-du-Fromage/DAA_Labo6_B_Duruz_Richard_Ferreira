package ch.heigvd.iict.and.rest.viewmodels

import android.app.Application
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

class ContactsViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = (application as ContactsApplication) .repository

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

    fun create(contact: Contact) {
        viewModelScope.launch {
            //TODO
        }
    }

    fun update(contact: Contact){

    }

    fun enroll() {
        viewModelScope.launch {
            //TODO
        }
    }

    fun refresh() {
        viewModelScope.launch {
            //TODO
        }
    }

}
