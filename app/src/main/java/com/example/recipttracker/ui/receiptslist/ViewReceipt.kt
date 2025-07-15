package com.example.recipttracker.ui.receiptslist

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.example.recipttracker.R
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.ui.addEditReceipt.ModifyReceiptVM

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ViewReceipt(
    onBack: () -> Unit,
    onEdit: () -> Unit,
    receiptViewModel: ReceiptViewModel,
    modifyReceiptVM: ModifyReceiptVM
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(onClick = {
                            onBack()
                        }) {
                            Icon(
                                painterResource(R.drawable.outline_keyboard_backspace_24),
                                contentDescription = "Back"
                            )
                        }
                        Text("Back to Receipts")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        receiptViewModel.onEvent(ReceiptsEvent.DeleteReceipt(modifyReceiptVM.receiptToEdit.value!!))
                        onBack()
                    }) {
                        Icon(painterResource(R.drawable.outline_delete_24), contentDescription = "Delete")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val imgUri: Uri = Uri.parse(modifyReceiptVM.uriPath.value)
            val inputStream = LocalContext.current.contentResolver.openInputStream(imgUri)
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)

            bitmap?.let {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Receipt Image",
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 16.dp)
                        .size(200.dp)
                )
            }

            Card(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(0.8f),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val fields: MutableMap<String, String> = mutableMapOf<String, String>()
//                    fields["Uri Path:"] = modifyReceiptVM.uriPath.value!!
                    fields["Store:"] = modifyReceiptVM.store.value!!
                    fields["Amount:"] = "$${modifyReceiptVM.amount.value!!}"
                    fields["Date:"] = modifyReceiptVM.date.value!!
                    fields["Category:"] = modifyReceiptVM.category.value!!

                    for (fieldKey in fields.keys) {
                        val topPadding = if (fieldKey == "Store:") 40 else 5
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(25.dp, topPadding.dp, 25.dp, 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(fieldKey)
                            Text(fields[fieldKey]!!)
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                onEdit()
                            },
                            modifier = Modifier.fillMaxWidth().padding(50.dp, 25.dp, 50.dp, 25.dp),
                            colors = ButtonColors(Color.Gray, Color.White, Color.Gray, Color.Gray)
                        ) {
                            Text("Edit Receipt")
                        }
                    }
                }
            }
        }
    }
}
