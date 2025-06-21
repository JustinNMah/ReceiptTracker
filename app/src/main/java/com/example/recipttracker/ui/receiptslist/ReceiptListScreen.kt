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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recipttracker.ui.theme.ReceiptTrackerTheme
import java.text.SimpleDateFormat
import java.util.*

enum class SortOption(val label: String) {
    DATE("Date"),
    ALPHABETICAL("A–Z"),
    CATEGORY("Category")
}

data class SortState(
    val option: SortOption,
    val isAscending: Boolean = true
)

data class Receipt(
    val store: String,
    val date: String,
    val amount: String,
    val category: String
) {
    // get month from dates
    val monthYear: String
        get() {
            return try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val parsedDate = dateFormat.parse(date)
                val calendar = Calendar.getInstance()
                calendar.time = parsedDate!!

                val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
                monthFormat.format(calendar.time)
            } catch (e: Exception) {
                "Unknown"
            }
        }

    val parsedDate: Date?
        get() {
            return try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                dateFormat.parse(date)
            } catch (e: Exception) {
                null
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptListScreen() {
    var sortState by remember { mutableStateOf(SortState(SortOption.DATE)) }
    var showFabMenu by remember { mutableStateOf(false) }

    val receipts = sampleReceipts.sortedWith(
        when (sortState.option) {
            SortOption.DATE -> if (sortState.isAscending) {
                compareBy { it.parsedDate }
            } else {
                compareByDescending { it.parsedDate }
            }
            SortOption.ALPHABETICAL -> if (sortState.isAscending) {
                compareBy { it.store }
            } else {
                compareByDescending { it.store }
            }
            SortOption.CATEGORY -> if (sortState.isAscending) {
                compareBy { it.category }
            } else {
                compareByDescending { it.category }
            }
        }
    )

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
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Upload") },
                        onClick = {
                            showFabMenu = false
                            // handle upload click
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
                SortOption.entries.forEachIndexed { index, option ->
                    SegmentedButton(
                        selected = sortState.option == option,
                        onClick = {
                            sortState = if (sortState.option == option) {
                                sortState.copy(isAscending = !sortState.isAscending)
                            } else {
                                SortState(option, isAscending = true)
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = SortOption.entries.size
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(option.label)
                            if (sortState.option == option) {
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
                when (sortState.option) {
                    SortOption.DATE -> {
                        // date -> group by month
                        val grouped = receipts.groupBy { it.monthYear }
                        grouped.forEach { (month, items) ->
                            item {
                                Column {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = month,
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
                    SortOption.CATEGORY -> {
                        // group by category
                        val grouped = receipts.groupBy { it.category }
                        grouped.forEach { (category, items) ->
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
                    else -> {
                        // a-z sorting
                        items(receipts) { receipt ->
                            ReceiptListItem(receipt)
                        }
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

val sampleReceipts = listOf(
    Receipt("Walmart", "2025-06-19", "$120.00", "Groceries"),
    Receipt("NoFrills", "2025-06-10", "$42.10", "Groceries"),
    Receipt("Bob's", "2025-06-05", "$34.99", "Groceries"),
    Receipt("Apple Store", "2025-05-25", "$999.00", "Electronics")
)

@Preview(showBackground = true)
@Composable
fun ReceiptListScreenPreview() {
    ReceiptTrackerTheme {
        ReceiptListScreen()
    }
}