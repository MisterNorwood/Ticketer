package pl.norwood.ticketer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import pl.norwood.ticketer.ui.theme.GuestListLayout
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


//Screen handles
const val EDIT = "edit"
const val CHECKIN = "checkin"

@Composable
fun TicketApp() {
    val navController = rememberNavController()
    val viewModel: GuestViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = GruvboxDark1) {
                NavigationBarItem(
                    selected = navController.currentDestination?.route == stringResource(R.string.edit),
                    onClick = { navController.navigate(EDIT) },
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
                    onClick = { navController.navigate(CHECKIN) },
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
            startDestination = EDIT,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(EDIT) { EditScreen(viewModel) }
            composable(CHECKIN) { CheckInScreen(viewModel) }
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
                },
                containerColor = GruvboxYellow,
                contentColor = GruvboxBg
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_guest))
            }
        }
    ) { padding ->


        GuestListLayout(
            guests = guests,
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.padding(padding),
            emptyStateMessage = stringResource(R.string.no_guests_tap)
        ) { guest ->

            SwipeToDismissBoxWrapper(
                onDelete = { viewModel.deleteGuest(guest) }
            ) {
                GuestItemCard(
                    guest = guest,
                    onEditClick = {
                        selectedGuest = guest
                        showDialog = true
                    },
                    onCheckChange = null
                )
            }
        }

        if (showDialog) {
            GuestDialog(
                guest = selectedGuest,
                onDismiss = { },
                onDelete = {
                    selectedGuest?.let { viewModel.deleteGuest(it) }
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
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissBoxWrapper(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = { DismissBackground(dismissState) },
        enableDismissFromEndToStart = true,
        enableDismissFromStartToEnd = false,
        content = { content() }
    )
}

@Composable
fun DismissBackground(dismissState: SwipeToDismissBoxState) {
    val color = when (dismissState.dismissDirection) {
        SwipeToDismissBoxValue.EndToStart -> GruvboxRed
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color, shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CheckInScreen(viewModel: GuestViewModel) {
    val guests by viewModel.filteredGuests.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(containerColor = GruvboxBg) { padding ->
        GuestListLayout(
            guests = guests,
            searchQuery = searchQuery,
            onSearchQueryChange = { viewModel.onSearchQueryChange(it) },
            modifier = Modifier.padding(padding)
        ) { guest ->
            GuestItemCard(
                guest = guest,
                onCheckChange = { viewModel.toggleCheckIn(guest) }
            )
        }
    }
}

@Composable
fun GuestItemCard(
    guest: Guest,
    onEditClick: (() -> Unit)? = null,
    onCheckChange: ((Boolean) -> Unit)? = null
) {
    val isCheckInMode = onCheckChange != null
    val isChecked = guest.isCheckedIn

    val backgroundColor =
        if (isCheckInMode && isChecked) GruvboxGreen.copy(alpha = 0.2f) else GruvboxDark1

    val borderStroke = if (isCheckInMode && isChecked) BorderStroke(1.dp, GruvboxGreen) else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onEditClick != null) Modifier.clickable { onEditClick() } else Modifier),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = borderStroke,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Guest Photo
            AsyncImage(
                model = guest.photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            //Guest Info
            Column(modifier = Modifier.weight(1f)) {
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

            if (onCheckChange != null) {
                Checkbox(
                    checked = isChecked,
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

