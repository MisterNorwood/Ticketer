package pl.norwood.ticketer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pl.norwood.ticketer.data.Guest

@Dao
interface GuestDao {
    @Query("SELECT * FROM guests ORDER BY surname ASC")
    fun getAllGuests(): Flow<List<Guest>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertGuest(guest: Guest)

    @Update
    suspend fun updateGuest(guest: Guest)

    @Delete
    suspend fun deleteGuest(guest: Guest)
}