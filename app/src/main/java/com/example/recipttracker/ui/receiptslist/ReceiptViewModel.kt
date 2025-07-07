package com.example.recipttracker.ui.receiptslist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.state.ReceiptsListState
import com.example.recipttracker.domain.repository.ReceiptRepository
import com.example.recipttracker.domain.util.ReceiptSortOrder
import com.example.recipttracker.domain.util.SortField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    private val repository: ReceiptRepository
): ViewModel() {

    private val _state = mutableStateOf (ReceiptsListState())
    val state: State<ReceiptsListState> = _state

    private var getReceiptsCoroutine: Job? = null

    init {
        getReceipts(_state.value.receiptSortOrder)
//        val receipt1 = Receipt(store="Walmart", date="2025-11-19", amount="$120.00", category="Groceries")
//        val receipt2 = Receipt(store="NoFrills", date="2025-11-10", amount="$42.10", category="Groceries")
//        val receipt3 = Receipt(store="Bob's", date="2025-11-17", amount="$39.99", category="Groceries")
//        val receipt4 = Receipt(store="Apple Store", date="2025-02-25", amount="$999.00", category="Electronics")
//        val receipt5 = Receipt(store="Another Store", date="2025-12-25", amount="$999.00", category="Electronics")
//        viewModelScope.launch {
//            repository.insertReceipt(receipt1)
//            repository.insertReceipt(receipt2)
//            repository.insertReceipt(receipt3)
//            repository.insertReceipt(receipt4)
//            repository.insertReceipt(receipt5)
//        }
    }

    fun onEvent(event: ReceiptsEvent) { // create an event when user changes Sort Order or Deletes Receipt.
        when(event) {
            is ReceiptsEvent.Order -> {
                val allReceipts = _state.value.receipts.values.flatten()
                sortReceipts(allReceipts, event.receiptSortOrder)
            }
            is ReceiptsEvent.DeleteReceipt -> {
                viewModelScope.launch {
                    repository.deleteReceipt(event.receipt)
                }
                // TODO: add refreshing view
            }
        }
    }

    private fun getReceipts(receiptSortOrder: ReceiptSortOrder) {
        getReceiptsCoroutine?.cancel()
        println("In getReceipts")

        getReceiptsCoroutine = repository.getReceipts()
            .onEach { receipts ->
                sortReceipts(receipts, receiptSortOrder)
            }
            .launchIn(viewModelScope)
    }

    private fun sortReceipts(allReceipts: List<Receipt>, receiptSortOrder: ReceiptSortOrder) {
        val sortedReceipts = allReceipts.sortedWith(
            when (receiptSortOrder.field) {
                SortField.DATE -> if (receiptSortOrder.isAscending) {
                    compareBy { it.parsedDate }
                } else {
                    compareByDescending { it.parsedDate }
                }
                SortField.STORE -> if (receiptSortOrder.isAscending) {
                    compareBy { it.store }
                } else {
                    compareByDescending { it.store }
                }
                SortField.CATEGORY -> if (receiptSortOrder.isAscending) {
                    compareBy { it.category }
                } else {
                    compareByDescending { it.category }
                }
            }
        )
        val groupedReceipts = when(receiptSortOrder.field) {
            SortField.DATE -> sortedReceipts.groupBy { it.monthYear }
            SortField.STORE -> mapOf("" to sortedReceipts)
            SortField.CATEGORY -> sortedReceipts.groupBy { it.category }
        }
        _state.value = state.value.copy(
            receipts = groupedReceipts,
            receiptSortOrder = receiptSortOrder
        )
    }
}