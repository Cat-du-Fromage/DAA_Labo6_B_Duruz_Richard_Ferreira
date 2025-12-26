package ch.heigvd.iict.and.rest.ui.screens
import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.heigvd.iict.and.rest.R
import ch.heigvd.iict.and.rest.models.Contact
import ch.heigvd.iict.and.rest.models.PhoneType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.*
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ScreenContactEditor(contact : Contact?, onCancel : () -> Unit, onSave : (Contact) -> Unit , onDelete : () -> Unit) {

    val context = LocalContext.current

    var name by remember { mutableStateOf(contact?.name ?: "") }
    var firstname by remember { mutableStateOf(contact?.firstname ?: "") }
    var email by remember { mutableStateOf(contact?.email ?: "") }
    var birthday by remember { mutableStateOf<Calendar?>(contact?.birthday) }
    var address by remember { mutableStateOf(contact?.address ?: "") }
    var zip by remember { mutableStateOf(contact?.zip ?: "") }
    var city by remember { mutableStateOf(contact?.city ?: "") }
    var type by remember { mutableStateOf(contact?.type ?: PhoneType.HOME) }
    var phoneNumber by remember { mutableStateOf(contact?.phoneNumber ?: "") }


    fun createContact() : Contact? {
        if (name.isBlank()) return null

        return Contact(
            id = contact?.id,
            remoteId = contact?.remoteId,
            name = name,
            firstname = firstname,
            email = email,
            birthday = birthday,
            address = address,
            zip = zip,
            city = city,
            type = type,
            phoneNumber = phoneNumber
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Makes the form scrollable
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = if (contact == null) stringResource(R.string.screen_detail_title_new) else stringResource(R.string.screen_detail_title_edit), fontSize = 24.sp)

        TextField(value = name,
            onValueChange = { name = it },
            label = { Text(text = stringResource(R.string.screen_detail_name_subtitle)) },
            modifier = Modifier.fillMaxWidth())
        TextField(value = firstname,
            onValueChange = { firstname = it },
            label = { Text(text = stringResource(R.string.screen_detail_firstname_subtitle)) },
            modifier = Modifier.fillMaxWidth())
        TextField(value = email,
            onValueChange = { email = it },
            label = { Text(text = stringResource(R.string.screen_detail_email_subtitle)) },
            modifier = Modifier.fillMaxWidth())

        // Use the new helper here
        DateField(label = stringResource(R.string.screen_detail_birthday_subtitle),
            value = birthday,
            onValueChange = { birthday = it })

        TextField(value = address,
            onValueChange = { address = it },
            label = { Text(text = stringResource(R.string.screen_detail_address_subtitle)) },
            modifier = Modifier.fillMaxWidth())
        TextField(value = zip,
            onValueChange = { zip = it },
            label = { Text(text = stringResource(R.string.screen_detail_zip_subtitle)) },
            modifier = Modifier.fillMaxWidth())
        TextField(value = city,
            onValueChange = { city = it },
            label = { Text(text = stringResource(R.string.screen_detail_city_subtitle)) },
            modifier = Modifier.fillMaxWidth())

        // Phone Type Radio Group
        Text(text = stringResource(R.string.screen_detail_phonetype_subtitle),
            modifier = Modifier.padding(top = 8.dp),
            style = MaterialTheme.typography.bodySmall)
        Row(verticalAlignment = Alignment.CenterVertically) {
            PhoneType.entries.forEach { phoneType ->
                RadioButton(selected = (type == phoneType),
                    onClick = { type = phoneType })
                Text(text = phoneType.name.lowercase().replaceFirstChar { it.uppercase() })
            }
        }

        TextField(value = phoneNumber,
            onValueChange = { phoneNumber = it },
            label = { Text(text = stringResource(R.string.screen_detail_phonenumber_subtitle)) },
            modifier = Modifier.fillMaxWidth())

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onCancel, modifier = Modifier.weight(1f)) { Text(text = stringResource(R.string.screen_detail_btn_cancel)) }

            if (contact != null) {
                // Delete button only shown in Modification mode
                Button(onClick = { onDelete() }, modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.screen_detail_btn_delete))
                    // TODO : delete Icon
                    Icon(painterResource(R.drawable.add), contentDescription = null, modifier = Modifier.padding(start = 4.dp).size(18.dp))
                }
            }

            // Create/Save button
            Button(
                onClick = {
                    val contact = createContact()
                    if (contact != null) onSave(contact) },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (contact == null) stringResource(R.string.screen_detail_btn_create) else stringResource(R.string.screen_detail_btn_save))
                // TODO : save Icon
                Icon(painterResource(R.drawable.add), contentDescription = null, modifier = Modifier.padding(start = 4.dp).size(18.dp))
            }
        }
    }
}

@Composable
fun DateField(
    label: String,
    value: Calendar?,
    onValueChange: (Calendar) -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()) }

    // Format the date for display
    val dateText = value?.let { dateFormatter.format(it.time) } ?: ""

    // Prepare the Dialog
    val datePickerDialog = remember {
        val now = value ?: Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val selected = Calendar.getInstance().apply { set(year, month, day) }
                onValueChange(selected)
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        )
    }

    // The Field
    TextField(
        value = dateText,
        onValueChange = {},
        label = { Text(label) },
        readOnly = true,
        enabled = true, // Keep enabled so it stays clickable
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { if(it.isFocused) datePickerDialog.show() },
        colors = TextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = Color.Transparent,
            disabledIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Preview(showBackground = true, name = "Creation Mode")
@Composable
fun PreviewContactCreation() {
    MaterialTheme {
        ScreenContactEditor(
            contact = null, // No contact = New Contact mode
            onCancel = {},
            onSave = {},
            onDelete = {}
        )
    }
}


@Preview(showBackground = true, name = "Edit Mode")
@Composable
fun PreviewContactEdit() {
    MaterialTheme {
        // Dummy contact to see the "Edit contact" screen with data
        val dummyContact = Contact(
            id = 1L,
            name = "Pelletier",
            firstname = "Bernard",
            email = "b.pelletier@gmel.com",
            birthday = Calendar.getInstance().apply { set(2003, 11, 26) }, // 26.12.2003
            address = "Avenue des Sports 20",
            zip = "1400",
            city = "Yverdon-les-Bains",
            type = PhoneType.FAX,
            phoneNumber = "+41 24 123 10 01"
        )

        ScreenContactEditor(
            contact = dummyContact,
            onCancel = {},
            onSave = {},
            onDelete = {}
        )
    }
}