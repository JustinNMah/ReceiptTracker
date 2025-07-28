package com.example.recipttracker.ui.receiptslist

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.state.ReceiptsListState
import com.example.recipttracker.domain.repository.ReceiptRepository
import com.example.recipttracker.domain.repository.UserRepository
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
    private val repository: ReceiptRepository,
    private val userRepository: UserRepository
): ViewModel() {
    private val _state = mutableStateOf(ReceiptsListState())
    val state: State<ReceiptsListState> = _state

    private val _receiptCount = mutableStateOf(0)
    val receiptCount: State<Int> = _receiptCount

    private val _monthlyTotal = mutableStateOf(0f)
    val monthlyTotal: State<Float> = _monthlyTotal

    private val _categoryCount = mutableStateOf(0)
    val categoryCount: State<Int> = _categoryCount

    private val _mostVisitedStore = mutableStateOf<Pair<String, Int>?>(null)
    val mostVisitedStore: State<Pair<String, Int>?> = _mostVisitedStore

    private val _enabledSortFields = mutableStateOf(SortField.entries.toSet())
    val enabledSortFields: State<Set<SortField>> = _enabledSortFields

    private var getReceiptsCoroutine: Job? = null
    private var userId: UUID? = null

    init {
        getReceipts(_state.value.receiptSortOrder)
    }

    fun setUser(userId: UUID) {
        this.userId = userId
        getReceipts(_state.value.receiptSortOrder)
        updateReceiptCount(userId)
        updateMonthlyTotal(userId)

        // Load settings after userId is set
        loadSettings(userId)

        viewModelScope.launch {
            val cloudReceipts = getCloudReceipts(userId)
            val localReceipts = _state.value.receipts.values.flatten()
            val localIds = localReceipts.map { it.id }.toSet()

            val newCloudReceipts = cloudReceipts.filterNot { it.id in localIds }

            if (newCloudReceipts.isNotEmpty()) {
                repository.insertReceiptsFromCloud(newCloudReceipts)
            }
        }
    }

    private fun updateReceiptCount(userId: UUID) {
        viewModelScope.launch {
            val count = repository.getReceiptCount(userId)
            _receiptCount.value = count
        }
    }

    fun onEvent(event: ReceiptsEvent) {
        when(event) {
            is ReceiptsEvent.AddReceipt -> {
                viewModelScope.launch {
                    repository.insertReceipt(event.receipt)
                    updateReceiptCount(event.receipt.userId)
                }
            }
            is ReceiptsEvent.Order -> {
                val allReceipts = _state.value.receipts.values.flatten()
                sortReceipts(allReceipts, event.receiptSortOrder)
            }
            is ReceiptsEvent.DeleteReceipt -> {
                viewModelScope.launch {
                    repository.deleteReceipt(event.receipt)
                    updateReceiptCount(event.receipt.userId)
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
                    userId?.let { updateReceiptCount(it) }
                }
            }
        }
    }

    fun toggleSortField(sortField: SortField) {
        val currentUserId = userId ?: run {
            Log.e("ReceiptViewModel", "toggleSortField: userId is null")
            return
        }

        viewModelScope.launch {
            val currentFields = _enabledSortFields.value.toMutableSet()

            // Ensure at least one field remains enabled
            if (currentFields.size > 1 || !currentFields.contains(sortField)) {
                if (currentFields.contains(sortField)) {
                    currentFields.remove(sortField)
                } else {
                    currentFields.add(sortField)
                }

                _enabledSortFields.value = currentFields

                // Save to database
                try {
                    userRepository.updateEnabledSortFields(currentUserId, currentFields)
                    Log.d("ReceiptViewModel", "Settings saved: $currentFields")
                } catch (e: Exception) {
                    Log.e("ReceiptViewModel", "Error saving settings", e)
                    // Revert the UI change on error
                    if (currentFields.contains(sortField)) {
                        currentFields.remove(sortField)
                    } else {
                        currentFields.add(sortField)
                    }
                    _enabledSortFields.value = currentFields
                }

                // Update current sort if needed
                val currentSortField = _state.value.receiptSortOrder.field
                if (!currentFields.contains(currentSortField)) {
                    val newSortField = currentFields.first()
                    val newSortOrder = ReceiptSortOrder(
                        field = newSortField,
                        isAscending = if (newSortField == SortField.DATE) false else true
                    )
                    onEvent(ReceiptsEvent.Order(newSortOrder))
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
        getReceiptsCoroutine = repository.getReceipts(uid)
            .onEach { receipts ->
                sortReceipts(receipts, receiptSortOrder)
                _categoryCount.value = receipts.map { it.category }.distinct().count()

                val mostVisitedEntry = receipts
                    .groupingBy { it.store }
                    .eachCount()
                    .maxByOrNull { it.value }

                _mostVisitedStore.value = mostVisitedEntry?.let { Pair(it.key, it.value) }
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
                    compareBy { it.store.lowercase() }
                } else {
                    compareByDescending { it.store.lowercase() }
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
        val groupedReceipts = when (receiptSortOrder.field) {
            SortField.DATE -> sortedReceipts.groupBy { it.monthYear }
            SortField.STORE -> sortedReceipts.groupBy {
                it.store.trim().takeIf { name -> name.isNotEmpty() }?.first()?.uppercaseChar().toString()
            }
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
                _categoryCount.value = receipts.map { it.category }.distinct().count()
                val mostVisitedEntry = receipts
                    .groupingBy { it.store }
                    .eachCount()
                    .maxByOrNull { it.value }

                _mostVisitedStore.value = mostVisitedEntry?.let { Pair(it.key, it.value) }
            }
            .launchIn(viewModelScope)
    }

    fun updateMonthlyTotal(userId: UUID) {
        viewModelScope.launch {
            val total = repository.getMonthlyTotal(userId)
            _monthlyTotal.value = total
        }
    }

    private fun loadSettings(userId: UUID) {
        viewModelScope.launch {
            try {
                Log.d("ReceiptViewModel", "Loading settings for user: $userId")
                val sortFields = userRepository.getEnabledSortFields(userId)
                _enabledSortFields.value = sortFields
                Log.d("ReceiptViewModel", "Loaded sort fields: $sortFields")
            } catch (e: Exception) {
                Log.e("ReceiptViewModel", "Error loading settings", e)
                _enabledSortFields.value = SortField.entries.toSet()
            }
        }
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