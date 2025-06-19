package com.example.recipttracker.ui.receiptslist

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipttracker.domain.state.ReceiptsListState
import com.example.recipttracker.domain.repository.ReceiptRepository
import com.example.recipttracker.domain.util.ReceiptSortOrder
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
        getReceipts(ReceiptSortOrder.DateDesc)
    }

    fun onEvent(event: ReceiptsEvent) { // create an event when user changes Sort Order or Deletes Receipt.
        when(event) {
            is ReceiptsEvent.Order -> {
                // don't do anything if user clicked on the current sort order
                if (state.value.receiptSortOrder == event.receiptSortOrder) {
                    return
                }
                getReceipts(event.receiptSortOrder)
            }
            is ReceiptsEvent.DeleteReceipt -> {
                viewModelScope.launch {
                    repository.deleteReceipt(event.receipt)
                }
            }
        }
    }

    private fun getReceipts(receiptSortOrder: ReceiptSortOrder) {
        getReceiptsCoroutine?.cancel()
        println("In getReceipts")

        getReceiptsCoroutine = when(receiptSortOrder) {
            is ReceiptSortOrder.DateDesc -> repository.getReceiptsByDateDesc()
            is ReceiptSortOrder.DateAsc -> repository.getReceiptsByDateAsc()
            is ReceiptSortOrder.StoreDesc -> repository.getReceiptsByStoreDesc()
            is ReceiptSortOrder.StoreAsc -> repository.getReceiptsByStoreAsc()
            is ReceiptSortOrder.CategoryDesc -> repository.getReceiptsByCategoryDesc()
            is ReceiptSortOrder.CategoryAsc -> repository.getReceiptsByCategoryAsc()
        }
            .onEach { receipts ->
                _state.value = state.value.copy(
                    receipts = receipts,
                    receiptSortOrder = receiptSortOrder
                )
            }
            .launchIn(viewModelScope)
    }
}