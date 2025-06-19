package com.example.recipttracker.ui.receiptslist

import com.example.recipttracker.domain.model.Receipt
import com.example.recipttracker.domain.util.ReceiptSortOrder

sealed class ReceiptsEvent {
    data class Order(val receiptSortOrder: ReceiptSortOrder): ReceiptsEvent()
    data class DeleteReceipt(val receipt: Receipt): ReceiptsEvent()
}