package com.example.recipttracker.domain.state

import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.util.ReceiptSortOrder
import com.example.recipttracker.domain.util.SortField

data class ReceiptsListState (
    val receipts: Map<String, List<Receipt>> = emptyMap(),
    val receiptSortOrder: ReceiptSortOrder = ReceiptSortOrder(field = SortField.DATE, isAscending = false)
)