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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.ui.receiptslist.ReceiptViewModel
import com.example.recipttracker.ui.receiptslist.ReceiptsEvent
import com.example.recipttracker.ViewModels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditReceipt(
    onFinish: () -> Unit,
    receiptToEditOrAdd: ReceiptToEditOrAdd,
    receiptViewModel: ReceiptViewModel,
    userViewModel: UserViewModel
) {
    val userState = userViewModel.state.value
    val user = userState.user

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
                        if (isEdit) {
                            receiptViewModel.onEvent(ReceiptsEvent.ModifyReceipt(
                                receiptToEdit!!.id!!,
                                store.value,
                                amount.value,
                                date.value,
                                category.value
                            ))
                            onFinish()
                        }  else {
                            user?.let {
                                val newReceipt = Receipt(
                                    store = store.value,
                                    amount = "$${amount.value}",
                                    date = date.value,
                                    category = category.value,
                                    userId = it.id
                                )
                                receiptViewModel.onEvent(ReceiptsEvent.AddReceipt(newReceipt))
                                onFinish()
                            } ?: run {
                                Log.e("AddEditReceipt", "Cannot add receipt: user is not logged in.")
                            }
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
}
