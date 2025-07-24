package com.example.recipttracker.ui.receiptslist

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.state.ReceiptsListState
import com.example.recipttracker.domain.repository.ReceiptRepository
import com.example.recipttracker.domain.util.ReceiptSortOrder
import com.example.recipttracker.domain.util.SortField
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import java.util.UUID

@HiltViewModel
class ReceiptViewModel @Inject constructor(
    private val repository: ReceiptRepository
): ViewModel() {
    private val _state = mutableStateOf(ReceiptsListState())
    val state: State<ReceiptsListState> = _state

    private var getReceiptsCoroutine: Job? = null
    private var userId: UUID? = null
    init {
        getReceipts(_state.value.receiptSortOrder)
    }

    fun setUser(userId: UUID) {
        this.userId = userId
        getReceipts(_state.value.receiptSortOrder)

        // Get cloud database receipts and check if any are not in the local database. Add different receipts to local database
        viewModelScope.launch {
            val cloudReceipts = getCloudReceipts(userId)
            val localReceipts = _state.value.receipts.values.flatten() // Flatten the local database's Map<String, List<Receipt>> to a single list
            val localIds = localReceipts.map { it.id }.toSet()

            val newCloudReceipts = cloudReceipts.filterNot { it.id in localIds }

            if (newCloudReceipts.isNotEmpty()) {
                repository.insertReceiptsFromCloud(newCloudReceipts)
            }
        }
    }

    fun onEvent(event: ReceiptsEvent) { // create an event when user changes Sort Order or Deletes Receipt.
        when(event) {
            is ReceiptsEvent.AddReceipt -> {
                viewModelScope.launch {
                    repository.insertReceipt(event.receipt)
                }
            }
            is ReceiptsEvent.Order -> {
                val allReceipts = _state.value.receipts.values.flatten()
                sortReceipts(allReceipts, event.receiptSortOrder)
            }
            is ReceiptsEvent.DeleteReceipt -> {
                viewModelScope.launch {
                    repository.deleteReceipt(event.receipt)
                }
            }
            is ReceiptsEvent.ModifyReceipt -> {
                viewModelScope.launch {
                    repository.modifyReceipt(
                        event.id,
                        event.store,
                        event.amount,
                        event.date,
                        event.category,
                        event.filePath
                    )
                }
            }
        }
    }

    private fun getReceipts(receiptSortOrder: ReceiptSortOrder) {
        val uid = userId ?: run {
            Log.e("ReceiptViewModel", "getReceipts: userId is null, aborting fetch")
            return
        }

        getReceiptsCoroutine?.cancel()
        println("In getReceipts")

        getReceiptsCoroutine = repository.getReceipts(uid)
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
                SortField.AMOUNT -> if (receiptSortOrder.isAscending) {
                    compareBy { it.amount }
                } else {
                    compareByDescending { it.amount }
                }
            }
        )
        val groupedReceipts = when(receiptSortOrder.field) {
            SortField.DATE -> sortedReceipts.groupBy { it.monthYear }
            SortField.STORE -> mapOf("" to sortedReceipts)
            SortField.CATEGORY -> sortedReceipts.groupBy { it.category }
            SortField.AMOUNT -> mapOf("" to sortedReceipts)
        }
        _state.value = state.value.copy(
            receipts = groupedReceipts,
            receiptSortOrder = receiptSortOrder
        )
    }

    fun searchReceipts(userId: UUID, query: String) {
        getReceiptsCoroutine?.cancel()

        if (query.isBlank()) {
            getReceipts(_state.value.receiptSortOrder)
            return
        }

        getReceiptsCoroutine = repository.searchReceipts(userId, query)
            .onEach { receipts ->
                sortReceipts(receipts, _state.value.receiptSortOrder)
            }
            .launchIn(viewModelScope)
    }

}

private suspend fun getCloudReceipts(userId: UUID) : List<Receipt> {
    val firestore = FirebaseFirestore.getInstance()

    val snapshot = try {
        firestore
            .collection("users")
            .document(userId.toString())
            .collection("receipts")
            .get()
            .await()
    } catch (e: Exception) {
        return emptyList()
    }

    val receipts = mutableListOf<Receipt>()
    for (doc in snapshot.documents) {
        val data = doc.data ?: continue

        val idMap = data["id"] as? Map<*, *> ?: continue
        val idMSB = (idMap["mostSignificantBits"] as? Number)?.toLong() ?: continue
        val idLSB = (idMap["leastSignificantBits"] as? Number)?.toLong() ?: continue
        val id = UUID(idMSB, idLSB)
        receipts.add(
            Receipt(
                id = id,
                userId = userId,
                store = data["store"] as? String ?: "",
                amount = data["amount"] as? String ?: "",
                date = data["date"] as? String ?: "",
                category = data["category"] as? String ?: "",
                filePath = data["filePath"] as? String ?: "",
                syncedWithCloud = true
            )
        )
    }

    return receipts
}
