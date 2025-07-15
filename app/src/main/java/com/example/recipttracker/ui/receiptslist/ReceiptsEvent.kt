package com.example.recipttracker.ui.receiptslist

import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.util.ReceiptSortOrder

sealed class ReceiptsEvent {
    data class AddReceipt(val receipt: Receipt): ReceiptsEvent()
    data class Order(val receiptSortOrder: ReceiptSortOrder): ReceiptsEvent()
    data class DeleteReceipt(val receipt: Receipt): ReceiptsEvent()
    data class ModifyReceipt(
        val id: Int,
        val store: String,
        val amount: String,
        val date: String,
        val category: String,
        val uriPath: String
    ): ReceiptsEvent()
}