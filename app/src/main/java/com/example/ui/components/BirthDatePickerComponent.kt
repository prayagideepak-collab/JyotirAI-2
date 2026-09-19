package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthDatePickerComponent(
    dateValue: String,
    onDateChanged: (String) -> Unit,
    label: String = "जन्म तिथि (DD/MM/YYYY)"
) {
    OutlinedTextField(
        value = dateValue,
        onValueChange = { newVal ->
            // Format enforcement or direct entry
            onDateChanged(newVal)
        },
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = {
                // Quick toggle or picker trigger action
            }) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date")
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}
