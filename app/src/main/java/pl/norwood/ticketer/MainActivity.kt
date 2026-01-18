package pl.norwood.ticketer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import pl.norwood.ticketer.data.Guest
import pl.norwood.ticketer.image.saveImageToInternalStorage
import pl.norwood.ticketer.ui.theme.GruvboxBg
import pl.norwood.ticketer.ui.theme.GruvboxColorScheme
import pl.norwood.ticketer.ui.theme.GruvboxDark1
import pl.norwood.ticketer.ui.theme.GruvboxFg
import pl.norwood.ticketer.ui.theme.GruvboxGray
import pl.norwood.ticketer.ui.theme.GruvboxGreen
import pl.norwood.ticketer.ui.theme.GruvboxRed
import pl.norwood.ticketer.ui.theme.GruvboxYellow
import pl.norwood.ticketer.ui.theme.SearchBar
import pl.norwood.ticketer.ui.theme.TextField
import pl.norwood.ticketer.viewmodel.GuestViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = GruvboxColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TicketApp()
                }
            }
        }
    }
}

@Composable
fun TicketApp() {
    val navController = rememberNavController()
    val viewModel: GuestViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = GruvboxDark1) {
                NavigationBarItem(
                    selected = navController.currentDestination?.route == stringResource(R.string.edit),
                    onClick = { navController.navigate(R.string.edit.toString()) },
                    icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_edit)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GruvboxBg,
                        selectedTextColor = GruvboxYellow,
                        indicatorColor = GruvboxYellow,
                        unselectedIconColor = GruvboxGray,
                        unselectedTextColor = GruvboxGray
                    )
                )
                NavigationBarItem(
                    selected = navController.currentDestination?.route == stringResource(R.string.checkin),
                    onClick = { navController.navigate( R.string.checkin.toString())},
                    icon = { Icon(Icons.Default.Check, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_checkin)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = GruvboxBg,
                        selectedTextColor = GruvboxYellow,
                        indicatorColor = GruvboxYellow,
                        unselectedIconColor = GruvboxGray,
                        unselectedTextColor = GruvboxGray
                    )
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "edit",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("edit") { EditScreen(viewModel) }
            composable("checkin") { CheckInScreen(viewModel) }
        }
    }
}

@Composable
fun EditScreen(viewModel: GuestViewModel) {
    val guests by viewModel.filteredGuests.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedGuest by remember { mutableStateOf<Guest?>(null) }

    Scaffold(
        containerColor = GruvboxBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedGuest = null
                    showDialog = true
                },
                containerColor = GruvboxYellow,
                contentColor = GruvboxBg
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_guest))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.onSearchQueryChange(it) }
            )

            if (guests.isEmpty()) {
                EmptyStateView(isSearching = searchQuery.isNotEmpty())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(guests, key = { it.id }) { guest ->
                        GuestEditCard(
                            guest = guest,
                            onClick = {
                                selectedGuest = guest
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            GuestDialog(
                guest = selectedGuest,
                onDismiss = { showDialog = false },
                onDelete = {
                    selectedGuest?.let {
                        viewModel.deleteGuest(it)
                    }
                    showDialog = false
                },
                onSave = { name, surname, localPath, event ->
                    if (selectedGuest == null) {
                        viewModel.addGuest(name, surname, localPath, event)
                    } else {
                        viewModel.updateGuest(
                            selectedGuest!!.copy(
                                name = name,
                                surname = surname,
                                photoUrl = localPath,
                                eventName = event
                            )
                        )
                    }
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun GuestEditCard(guest: Guest, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = GruvboxDark1),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = guest.photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = "${guest.name} ${guest.surname}",
                    color = GruvboxFg,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = guest.eventName,
                    color = GruvboxGray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(isSearching: Boolean) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isSearching) stringResource(R.string.no_results_found) else stringResource(
                    R.string.list_is_empty
                ),
                color = GruvboxGray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


@Composable
fun CheckInScreen(viewModel: GuestViewModel) {
    val guests by viewModel.allGuests.collectAsState()

    if (guests.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = stringResource(R.string.empty_list), color = GruvboxGray)
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(guests) { guest ->
                GuestItemCard(
                    guest = guest,
                    isEditMode = false,
                    onCheckChange = { viewModel.toggleCheckIn(guest) }
                )
            }
        }
    }
}

@Composable
fun GuestItemCard(
    guest: Guest,
    isEditMode: Boolean,
    onItemClick: () -> Unit = {},
    onCheckChange: (Boolean) -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = GruvboxDark1),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEditMode, onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = guest.photoUrl,
                contentDescription = stringResource(R.string.image_desc),
                modifier = Modifier
                    .size(width = 80.dp, height = 80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${guest.name} ${guest.surname}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = GruvboxFg
                )
                Text(
                    text = guest.eventName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = GruvboxGray
                )
            }

            if (!isEditMode) {
                Checkbox(
                    checked = guest.isCheckedIn,
                    onCheckedChange = onCheckChange,
                    colors = CheckboxDefaults.colors(
                        checkedColor = GruvboxGreen,
                        uncheckedColor = GruvboxGray,
                        checkmarkColor = GruvboxBg
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuestDialog(
    guest: Guest?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit,
    onDelete: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var name by remember { mutableStateOf(guest?.name ?: "") }
    var surname by remember { mutableStateOf(guest?.surname ?: "") }
    var event by remember { mutableStateOf(guest?.eventName ?: "") }
    var localPath by remember { mutableStateOf(guest?.photoUrl ?: "") }

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            localPath = saveImageToInternalStorage(context, it)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = GruvboxBg),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (guest == null) stringResource(R.string.dialog_add_guest) else stringResource(
                        R.string.dialog_edit_guest
                    ),
                    style = MaterialTheme.typography.headlineSmall,
                    color = GruvboxFg
                )

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GruvboxDark1)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (localPath.isNotEmpty()) {
                        AsyncImage(
                            model = localPath,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Default.Add, contentDescription = null, tint = GruvboxGray)
                    }
                }

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(R.string.label_name)
                )
                TextField(
                    value = surname,
                    onValueChange = { surname = it },
                    label = stringResource(R.string.label_surname)
                )
                TextField(
                    value = event,
                    onValueChange = { event = it },
                    label = stringResource(R.string.label_event)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    if (guest != null) {
                        Button(
                            onClick = onDelete,
                            colors = ButtonDefaults.buttonColors(containerColor = GruvboxRed)
                        ) {
                            Text(stringResource(R.string.btn_delete))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = GruvboxGray)
                    ) {
                        Text(stringResource(R.string.btn_cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(name, surname, localPath, event) },
                        colors = ButtonDefaults.buttonColors(containerColor = GruvboxGreen),
                        enabled = name.isNotBlank() && localPath.isNotBlank()
                    ) {
                        Text(stringResource(R.string.btn_save))
                    }
                }
            }
        }
    }
}

