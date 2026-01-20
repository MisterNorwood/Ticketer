package pl.norwood.ticketer.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.norwood.ticketer.data.Guest
import pl.norwood.ticketer.data.TicketDatabase

class GuestViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = TicketDatabase.getDatabase(application).guestDao()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val allGuests: StateFlow<List<Guest>> = dao.getAllGuests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredGuests: StateFlow<List<Guest>> = _searchQuery
        .combine(dao.getAllGuests()) { query, guests ->
            if (query.isBlank()) {
                guests
            } else {
                guests.filter {
                    it.name.contains(query, ignoreCase = true) ||
                            it.surname.contains(query, ignoreCase = true) ||
                            it.eventName.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }


    fun addGuest(name: String, surname: String, photoUrl: String, eventName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertGuest(
                Guest(
                    name = name,
                    surname = surname,
                    photoUrl = photoUrl,
                    eventName = eventName
                )
            )
        }
    }

    fun updateGuest(updatedGuest: Guest) {
        viewModelScope.launch(Dispatchers.IO) {
            val existingGuest = dao.getGuestById(updatedGuest.id)

            val oldPath = existingGuest?.photoUrl
            val newPath = updatedGuest.photoUrl

            if (oldPath != null && oldPath != newPath) {
                val file = java.io.File(oldPath)
                if (file.exists()) {
                    file.delete()
                    Log.d("FileCleanup", "Deleted old image: $oldPath")
                }
            }

            dao.updateGuest(updatedGuest)
        }
    }

    fun deleteGuest(guest: Guest) {
        viewModelScope.launch(Dispatchers.IO) {
            val photoPath = guest.photoUrl
            if (photoPath.isNotEmpty()) {
                val file = java.io.File(photoPath)
                if (file.exists()) {
                    val deleted = file.delete()
                    if (deleted) {
                        Log.d("Ticketer", "File deleted successfully: $photoPath")
                    }
                }
            }
            dao.deleteGuest(guest)
        }
    }

    fun toggleCheckIn(guest: Guest) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateGuest(guest.copy(isCheckedIn = !guest.isCheckedIn))
        }
    }
}