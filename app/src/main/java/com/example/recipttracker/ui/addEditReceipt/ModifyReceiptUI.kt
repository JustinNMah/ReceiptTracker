package com.example.recipttracker.ui.addEditReceipt
import android.content.Intent
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.ui.receiptslist.ReceiptViewModel
import com.example.recipttracker.ui.receiptslist.ReceiptsEvent
import com.example.recipttracker.ViewModels.UserViewModel

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
    val category: MutableState<String> = remember { mutableStateOf(modifyReceiptVM.category.value!!) }
    val uriPath: MutableState<String> = remember { mutableStateOf(modifyReceiptVM.uriPath.value!!) }

    val imgUri: Uri = Uri.parse(uriPath.value)
    val inputStream = LocalContext.current.contentResolver.openInputStream(imgUri)
    val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

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
                    Log.d("ModifyReceiptUI", input)
                    if (input.all { it.isDigit() }) {
                        amount.value = input
                    }
                },
                label = { Text("Total") },
                modifier = Modifier.padding(8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                                        uriPath.value
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
                                        category = category.value,
                                        uriPath = uriPath.value
                                    )
                                    receiptViewModel.onEvent(ReceiptsEvent.AddReceipt(newReceipt))
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
