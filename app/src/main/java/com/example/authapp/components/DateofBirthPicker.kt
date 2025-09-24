package com.example.authapp.components

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import java.util.*

@Composable
fun DatePickerField(
    label: String,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    pastDatesOnly: Boolean = false,   // true = only allow past dates
    futureDatesOnly: Boolean = false  // true = only allow future dates
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
            try {
                val formattedDate = "%02d/%02d/%04d".format(selectedDay, selectedMonth + 1, selectedYear)
                onDateSelected(formattedDate)
            } catch (e: Exception) {
                // Optional: handle error
            }
        },
        year, month, day
    ).apply {
        when {
            pastDatesOnly -> datePicker.maxDate = calendar.timeInMillis  // only past dates
            futureDatesOnly -> datePicker.minDate = calendar.timeInMillis // only future dates
        }
    }

    OutlinedTextField(
        value = selectedDate,
        onValueChange = {},
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        readOnly = true,
        trailingIcon = {
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Pick Date")
            }
        }
    )
}
