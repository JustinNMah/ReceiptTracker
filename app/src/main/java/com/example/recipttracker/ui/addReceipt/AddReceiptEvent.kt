package com.example.recipttracker.addReceipt

import com.example.recipttracker.domain.model.Receipt

sealed class AddReceiptEvent {
    data class InsertReceipt(val receipt: Receipt): AddReceiptEvent()
}
