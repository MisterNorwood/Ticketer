package pl.norwood.ticketer.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.norwood.ticketer.data.Guest


@Composable
fun GuestListLayout(
    guests: List<Guest>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    emptyStateMessage: String = "No guests found",
    itemContent: @Composable (Guest) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {

        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange
        )

        if (guests.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = emptyStateMessage, color = GruvboxGray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(guests, key = { it.id }) { guest ->
                    itemContent(guest)
                }
            }
        }
    }
}