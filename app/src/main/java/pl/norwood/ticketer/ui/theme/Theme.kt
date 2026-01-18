package pl.norwood.ticketer.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.norwood.ticketer.R

val GruvboxColorScheme: ColorScheme
    @Composable
    get() = lightColorScheme(
        primary = GruvboxYellow,
        onPrimary = GruvboxBg,
        secondary = GruvboxGreen,
        onSecondary = GruvboxBg,
        tertiary = GruvboxBlue,
        background = GruvboxBg,
        onBackground = GruvboxFg,
        surface = GruvboxDark1,
        onSurface = GruvboxFg,
        error = GruvboxRed,
        onError = GruvboxFg
    )


@Composable
fun TextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GruvboxYellow,
            unfocusedBorderColor = GruvboxGray,
            focusedLabelColor = GruvboxYellow,
            unfocusedLabelColor = GruvboxGray,
            cursorColor = GruvboxYellow,
            focusedTextColor = GruvboxFg,
            unfocusedTextColor = GruvboxFg
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = {
            Text(
                text = stringResource(R.string.search_guests),
                color = GruvboxGray,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = stringResource(R.string.search_icon),
                tint = GruvboxYellow
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.clear_search),
                    tint = GruvboxGray,
                    modifier = Modifier.clickable { onQueryChange("") }
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = GruvboxFg,
            unfocusedTextColor = GruvboxFg,
            focusedContainerColor = GruvboxDark1,
            unfocusedContainerColor = GruvboxDark1,
            focusedBorderColor = GruvboxYellow,
            unfocusedBorderColor = GruvboxDark1,
            cursorColor = GruvboxYellow,
            focusedPlaceholderColor = GruvboxGray,
            unfocusedPlaceholderColor = GruvboxGray,
            focusedLeadingIconColor = GruvboxYellow,
            unfocusedLeadingIconColor = GruvboxGray
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

