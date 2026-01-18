package pl.norwood.ticketer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guests")
data class Guest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val surname: String,
    val photoUrl: String,
    val eventName: String,
    val isCheckedIn: Boolean = false
)