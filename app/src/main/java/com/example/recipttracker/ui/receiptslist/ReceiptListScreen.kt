package com.example.recipttracker.ui.receiptslist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.util.ReceiptSortOrder
import com.example.recipttracker.domain.util.SortField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptListScreen(
    onCapture: () -> Unit,
    onUpload: () -> Unit,
    viewModel: ReceiptViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val sortState = state.receiptSortOrder
    val receipts = state.receipts
    var showFabMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "ReceiptTracker",
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* handle menu click */ }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* handle search click */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = { showFabMenu = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add receipt")
                }

                DropdownMenu(
                    expanded = showFabMenu,
                    onDismissRequest = { showFabMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Capture") },
                        onClick = {
                            showFabMenu = false
                            // handle capture click
                            onCapture()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Upload") },
                        onClick = {
                            showFabMenu = false
                            // handle upload click
                            onUpload()
                        }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                SortField.entries.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = sortState.field == option,
                        onClick = {
                            println("clicked on $option")
                            val newSortState: ReceiptSortOrder = if (sortState.field == option) {
                                sortState.copy(isAscending = !sortState.isAscending)
                            } else {
                                if (option == SortField.DATE) {
                                    ReceiptSortOrder(option, isAscending = false)
                                } else {
                                    ReceiptSortOrder(option, isAscending = true)
                                }
                            }
                            println("newSortState $newSortState")
                            val event = ReceiptsEvent.Order(newSortState)
                            viewModel.onEvent(event)
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = SortField.entries.size
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(option.toString())
                            if (sortState.field == option) {
                                Icon(
                                    imageVector = if (sortState.isAscending) {
                                        Icons.Default.KeyboardArrowUp
                                    } else {
                                        Icons.Default.KeyboardArrowDown
                                    },
                                    contentDescription = if (sortState.isAscending) "Ascending" else "Descending",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                receipts.forEach { (category, items) ->
                    item {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = category,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    items(items) { receipt ->
                        ReceiptListItem(receipt)
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptListItem(receipt: Receipt) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        ListItem(
            headlineContent = { Text(receipt.store) },
            supportingContent = {
                Column {
                    Text(receipt.date)
                }
            },
            trailingContent = {
                Text(
                    text = receipt.amount,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        )
    }
}
