package ch.heigvd.iict.and.rest.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.SyncState
import kotlinx.coroutines.flow.Flow

/**
ContactsDao.kt
 * Contact dao, interaction with db
Authors:
 * Duruz Florian
 * Ferreira Silva Sven
 * Richard Aurélien
 */
@Dao
interface ContactsDao {

    @Insert
    fun insert(contact: Contact) : Long

    @Update
    fun update(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    // TODO : Probably irrelevant now because of the DELETED state. might remove
    // Not used anymore
    @Query("SELECT * FROM Contact")
    fun getAllContacts() : Flow<List<Contact>>

    // Only get contacts in specific states (by default synced, created and updated (omitting DELETED))
    @Query("SELECT * FROM Contact WHERE syncState IN (:states)")
    fun getContacts(vararg states: SyncState = arrayOf(SyncState.SYNCED, SyncState.CREATED, SyncState.UPDATED)) : Flow<List<Contact>>

    @Query("SELECT * FROM Contact WHERE syncState IN (:states)")
    fun getContactsAsList(vararg states: SyncState = arrayOf(SyncState.SYNCED, SyncState.CREATED, SyncState.UPDATED)) : List<Contact>

    @Query("SELECT * FROM Contact WHERE id = :id")
    fun getContactById(id : Long) : Contact?

    @Query("SELECT COUNT(*) FROM Contact WHERE syncState IN (:states)")
    fun getCount( vararg states: SyncState = arrayOf(SyncState.SYNCED, SyncState.CREATED, SyncState.UPDATED)) : Int

    @Query("DELETE FROM Contact")
    fun clearAllContacts()

}