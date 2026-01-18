package pl.norwood.ticketer.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import pl.norwood.ticketer.data.Guest

@Database(entities = [Guest::class], version = 1, exportSchema = false)
abstract class TicketDatabase : RoomDatabase() {
    abstract fun guestDao(): GuestDao

    companion object {
        @Volatile
        private var INSTANCE: TicketDatabase? = null

        fun getDatabase(context: Context): TicketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TicketDatabase::class.java,
                    "ticket_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}