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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.util.ReceiptSortOrder
import com.example.recipttracker.domain.util.SortField
import com.example.recipttracker.ui.addEditReceipt.ModifyReceiptVM
import com.example.recipttracker.ViewModels.UserViewModel
import kotlinx.coroutines.launch
import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Color
import com.example.recipttracker.domain.event.UserEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.runtime.collectAsState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptListScreen(
    onCapture: () -> Unit,
    onUpload: () -> Unit,
    onLogout: () -> Unit,
    onView: () -> Unit,
    onSettings: () -> Unit,
    receiptViewModel: ReceiptViewModel,
    userViewModel: UserViewModel,
    modifyReceiptVM: ModifyReceiptVM
) {
    LaunchedEffect(Unit) {
        snapshotFlow { userViewModel.state.value.user }
            .collectLatest { user ->
                if (user != null) {
                    receiptViewModel.setUser(user.id)
                }
            }
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var searchQuery by remember { mutableStateOf("") }
    val userId = userViewModel.state.value.user?.id
    val categoryCount by receiptViewModel.categoryCount
    val mostVisitedStore by receiptViewModel.mostVisitedStore



    LaunchedEffect(userId) {
        snapshotFlow { searchQuery }
            .debounce(300)
            .distinctUntilChanged()
            .collectLatest { query ->
                userId?.let {
                    receiptViewModel.searchReceipts(it, query)
                }
            }
    }

    val scope = rememberCoroutineScope()

    val state = receiptViewModel.state.value
    val sortState = state.receiptSortOrder
    val receiptCount by receiptViewModel.receiptCount
    val receipts = state.receipts

    var showFabMenu by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Menu",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )

                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {
                        scope.launch {
                            onSettings()
                            drawerState.close()
                        }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        scope.launch { userViewModel.onEvent(UserEvent.Logout); onLogout(); drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("ReceiptTracker", textAlign = TextAlign.Center)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        var isSearching by remember { mutableStateOf(false) }

                        if (isSearching) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    singleLine = true,
                                    placeholder = { Text("Search...") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp),
                                    textStyle = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurface)
                                )
                                IconButton(
                                    onClick = {
                                        searchQuery = ""
                                        isSearching = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear Search"
                                    )
                                }
                            }
                        } else {
                            IconButton(onClick = { isSearching = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                Box {
                    FloatingActionButton(onClick = { showFabMenu = true }) {
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
                                onCapture()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Upload") },
                            onClick = {
                                showFabMenu = false
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
                                val newSortState: ReceiptSortOrder = if (sortState.field == option) {
                                    sortState.copy(isAscending = !sortState.isAscending)
                                } else {
                                    if (option == SortField.DATE) {
                                        ReceiptSortOrder(option, isAscending = false)
                                    } else {
                                        ReceiptSortOrder(option, isAscending = true)
                                    }
                                }
                                receiptViewModel.onEvent(ReceiptsEvent.Order(newSortState))
                            },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = SortField.entries.size
                            ),
                            icon = { }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    option.toString(),
                                    fontSize = 12.sp
                                )
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
                            ReceiptListItem(onView, receipt, modifyReceiptVM) // might have to change later. this drilling is probably bad design
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptListItem(
    onView: () -> Unit,
    receipt: Receipt,
    modifyReceiptVM: ModifyReceiptVM
) {
    var showModifyMenu by remember { mutableStateOf(false) }
    val cardShape = RoundedCornerShape(12.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .border(1.dp, Color.Gray, cardShape)
            .clickable {
                Log.d("ReceiptListScreen", "Receipt ${receipt.id} pressed")
                modifyReceiptVM.setReceiptToEdit(receipt)
                onView()
            }
        ,
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = cardShape,
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
                    text = "$${receipt.amount}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        )
    }
}