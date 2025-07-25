package com.example.recipttracker.ui.addEditReceipt
import android.graphics.BitmapFactory
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
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.example.recipttracker.R
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.ui.receiptslist.ReceiptViewModel
import com.example.recipttracker.ui.receiptslist.ReceiptsEvent
import com.example.recipttracker.ViewModels.UserViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.icons.filled.DateRange
import generateBitmap

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
fun ModifyReceiptUI(
    onFinish: () -> Unit,
    modifyReceiptVM: ModifyReceiptVM,
    receiptViewModel: ReceiptViewModel,
    userViewModel: UserViewModel
) {
    Log.d("ModifyReceiptUI", "Mode: ${modifyReceiptVM.mode}")
    val userState = userViewModel.state.value
    val user = userState.user

    // update states when modifying text field (set initial values to be those in VM)
    val store: MutableState<String> = remember { mutableStateOf(modifyReceiptVM.store.value!!) }
    val amount: MutableState<String> = remember { mutableStateOf(modifyReceiptVM.amount.value!!) }
    val date: MutableState<String> = remember { mutableStateOf(modifyReceiptVM.date.value!!) }
    val data: MutableState<List<String>> = remember { mutableStateOf(modifyReceiptVM.data.value!!) }
    val category: MutableState<String> = remember { mutableStateOf(modifyReceiptVM.category.value!!) }
    val filePath: MutableState<String> = remember { mutableStateOf(modifyReceiptVM.filePath.value!!) }

    val file = File(filePath.value)
    val bitmap = generateBitmap(file.absolutePath)

    val validAmounts = Regex("^\\d*(\\.\\d{0,2})?$")
    val allCategories = receiptViewModel.state.value.receipts
        .values.flatten()
        .map { it.category }
        .distinct()
        .filter { it.isNotBlank() }

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    val initialDateMillis = if (modifyReceiptVM.mode.value == Mode.EDIT) parseDateForPicker(modifyReceiptVM.date.value ?: "") else null
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = {
                            onFinish()
                        }) {
                            Icon(
                                painterResource(R.drawable.outline_keyboard_backspace_24),
                                contentDescription = "Back"
                            )
                        }
                        Text("Back to Receipts")
                    }
                },
                actions = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            bitmap?.let {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Receipt Image",
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 16.dp)
                        .size(200.dp)
                )
            }
            OutlinedTextField(
                value = store.value,
                onValueChange = { store.value = it },
                label = { Text("Store") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = amount.value,
                onValueChange = { input ->
                    if (validAmounts.matches(input)) {
                        amount.value = input
                    }
                },
                label = { Text("Total") },
                modifier = Modifier.padding(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
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
            AutoCompleteCategory(
                categoryState = category,
                suggestions = allCategories,
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
                        when (modifyReceiptVM.mode.value) {
                            Mode.EDIT -> {
                                user?.let {
                                    receiptViewModel.onEvent(
                                        ReceiptsEvent.ModifyReceipt(
                                            modifyReceiptVM.receiptToEdit.value!!.id!!,
                                            store.value,
                                            amount.value,
                                            date.value,
                                            category.value,
                                            filePath.value
                                        )
                                    )
                                } ?: run {
                                    Log.e(
                                        "ModifyReceiptUI",
                                        "Cannot edit receipt: user is not logged in."
                                    )
                                }
                            }

                            Mode.ADD -> {
                                user?.let {
                                    val newReceipt = Receipt(
                                        userId = it.id,
                                        store = store.value,
                                        amount = amount.value,
                                        date = date.value,
                                        data = data.value,
                                        category = category.value,
                                        filePath = filePath.value
                                    )
                                    receiptViewModel.onEvent(ReceiptsEvent.AddReceipt(newReceipt))
                                    Log.e("ModifyReceiptUI", "Receipt Added. Data: ${data.value}")
                                } ?: run {
                                    Log.e(
                                        "ModifyReceiptUI",
                                        "Cannot add receipt: user is not logged in."
                                    )
                                }
                            }
                        }
                        onFinish()
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