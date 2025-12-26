package ch.heigvd.iict.and.rest.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.SyncState
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {

    @Insert
    fun insert(contact: Contact) : Long

    @Update
    fun update(contact: Contact)

    @Delete
    fun delete(contact: Contact)

    // TODO : Probably irrelevant now because of the DELETED state. might remove
    @Query("SELECT * FROM Contact")
    fun getAllContacts() : Flow<List<Contact>>

    // Only get contacts in specific states (by default synced, created and updated (omitting DELETED))
    // TODO : if we go with this then we should update the getCount() too
    @Query("SELECT * FROM Contact WHERE syncState IN (:states)")
    fun getContacts(vararg states: SyncState = arrayOf(SyncState.SYNCED, SyncState.CREATED, SyncState.UPDATED)) : Flow<List<Contact>>

    @Query("SELECT * FROM Contact WHERE id = :id")
    fun getContactById(id : Long) : Contact?

    @Query("SELECT COUNT(*) FROM Contact WHERE syncState IN (:states)")
    abstract fun countByStatus(states: List<SyncState>): Int

    fun getCount(
        states: List<SyncState> = listOf(SyncState.SYNCED, SyncState.CREATED, SyncState.UPDATED)
    ) : Int {
        return countByStatus(states)
    }

    // Could also be done like this ? tested and it works
    // We will need to decide
    // TODO : decide. This form has been used for getContacts
    /*
    @Query("SELECT COUNT(*) FROM Contact WHERE syncState IN (:states)")
    fun getCount( vararg states: SyncState = arrayOf(SyncState.SYNCED, SyncState.CREATED, SyncState.UPDATED)) : Int
    */

    @Query("DELETE FROM Contact")
    fun clearAllContacts()

}