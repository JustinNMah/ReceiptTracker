package com.example.recipttracker.domain.state

import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.util.ReceiptSortOrder

data class ReceiptsListState (
    val receipts: List<Receipt> = emptyList(),
    val receiptSortOrder: ReceiptSortOrder = ReceiptSortOrder.DateDesc
)