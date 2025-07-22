package com.example.recipttracker.ui.addEditReceipt
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.ui.receiptslist.ReceiptViewModel
import com.example.recipttracker.ui.receiptslist.ReceiptsEvent
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.DateRange

// Helper function to format date for display
fun formatDateForDisplay(dateString: String): String {
    if (dateString.isEmpty()) return ""

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString // Return original if parsing fails
    }
}

// Helper function to parse date from YYYY-MM-DD format for date picker
fun parseDateForPicker(dateString: String): Long? {
    if (dateString.isEmpty()) return null

    return try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        format.parse(dateString)?.time
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReceipt(
    onFinish: () -> Unit,
    receiptToEditOrAdd: ReceiptToEditOrAdd,
    receiptViewModel: ReceiptViewModel
) {
    val uriPath = receiptToEditOrAdd.uriPath.observeAsState()
    Log.d("TAG", "uriPath change notified in AddEditReceipt.kt: $uriPath")
    var bitmap: Bitmap? = null

    val imgUri = Uri.parse(uriPath.value)
    val inputStream = LocalContext.current.contentResolver.openInputStream(imgUri)
    bitmap = BitmapFactory.decodeStream(inputStream)

    if (bitmap == null) {
        Log.d("TAG", "Unable to convert $uriPath into bitmap in Photo.kt")
        onFinish()
    }

    val receiptToEdit: Receipt? = receiptToEditOrAdd.receiptToEdit.getValue()
    val isEdit: Boolean = receiptToEdit != null

    lateinit var date: MutableState<String>
    lateinit var amount: MutableState<String>
    lateinit var store: MutableState<String>
    lateinit var category: MutableState<String>

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    val initialDateMillis = if (isEdit) parseDateForPicker(receiptToEdit?.date ?: "") else null
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)
    val interactionSource = remember { MutableInteractionSource() }

    if (!isEdit) { // no receipt to edit. add instead
        date = remember { mutableStateOf("") }
        amount = remember { mutableStateOf("") }
        store = remember { mutableStateOf("") }
        category = remember { mutableStateOf("") }
    } else { // edit receipt
        date = remember { mutableStateOf(receiptToEdit!!.date) }
        amount = remember { mutableStateOf(receiptToEdit!!.amount) }
        store = remember { mutableStateOf(receiptToEdit!!.store) }
        category = remember { mutableStateOf(receiptToEdit!!.category) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("ReceiptTracker", textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            bitmap?.let {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Receipt Image",
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .size(200.dp)
                )
            }
            OutlinedTextField(
                value = store.value,
                onValueChange = { store.value = it },
                label = { Text("Store") },
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = amount.value,
                onValueChange = { input ->
                    // Allow digits and decimal point, but only one decimal point
                    if (input.all { it.isDigit() || it == '.' } && input.count { it == '.' } <= 1) {
                        amount.value = input
                    }
                },
                label = { Text("Total") },
                modifier = Modifier.padding(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                trailingIcon = { Text("$") }
            )
            OutlinedTextField(
                value = formatDateForDisplay(date.value),
                onValueChange = { },
                label = { Text("Date") },
                placeholder = { Text("Select a date") },
                modifier = Modifier
                    .padding(8.dp)
                    .clickable { showDatePicker = true },
                readOnly = true,
                trailingIcon = {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Select date",
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                }
            )
            OutlinedTextField(
                value = category.value,
                onValueChange = { category.value = it },
                label = { Text("Category") },
                modifier = Modifier.padding(8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 60.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    onClick = { onFinish() },
                    modifier = Modifier
                        .padding(10.dp)
                        .size(40.dp),
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Cancel")
                }
                FloatingActionButton(
                    onClick = {
                        if (isEdit) {
                            receiptViewModel.onEvent(ReceiptsEvent.ModifyReceipt(
                                receiptToEdit!!.id!!,
                                store.value,
                                amount.value,
                                date.value,
                                category.value
                            ))
                            onFinish()
                        } else {
                            val newReceipt = Receipt(
                                store = store.value,
                                amount = "$${amount.value}",
                                date = date.value,
                                category = category.value
                            )
                            receiptViewModel.onEvent(ReceiptsEvent.AddReceipt(newReceipt))
                            onFinish()
                        }
                    },
                    modifier = Modifier
                        .padding(10.dp)
                        .size(40.dp),
                    containerColor = Color.LightGray
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Cancel")
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        date.value = formatter.format(Date(millis))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}