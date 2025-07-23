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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import com.example.recipttracker.R
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.ui.receiptslist.ReceiptViewModel
import com.example.recipttracker.ui.receiptslist.ReceiptsEvent
import com.example.recipttracker.ViewModels.UserViewModel
import java.io.File

@Composable
@OptIn(ExperimentalMaterial3Api::class)
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
    val data: MutableState<Set<String>> = remember { mutableStateOf(modifyReceiptVM.data.value!!) }
    val category: MutableState<String> = remember { mutableStateOf(modifyReceiptVM.category.value!!) }
    val filePath: MutableState<String> = remember { mutableStateOf(modifyReceiptVM.filePath.value!!) }

    val file = File(filePath.value)
    val bitmap = BitmapFactory.decodeFile(file.absolutePath)

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
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(
                value = amount.value,
                onValueChange = { input ->
                    Log.d("ModifyReceiptUI", input)
                    if (input.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
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
                value = date.value,
                onValueChange = { date.value = it },
                label = { Text("Date") },
                modifier = Modifier.padding(8.dp)
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
                        when (modifyReceiptVM.mode.value) {
                            Mode.EDIT -> {
                                user?.let {
                                    receiptViewModel.onEvent(ReceiptsEvent.ModifyReceipt(
                                        modifyReceiptVM.receiptToEdit.value!!.id!!,
                                        store.value,
                                        amount.value,
                                        date.value,
                                        category.value,
                                        filePath.value
                                    ))
                                } ?: run {
                                    Log.e("ModifyReceiptUI", "Cannot edit receipt: user is not logged in.")
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
                                    Log.e("ModifyReceiptUI", "Cannot add receipt: user is not logged in.")
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
}
