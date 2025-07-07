package com.example.recipttracker.ui.photo

import com.example.recipttracker.domain.model.Receipt

sealed class PhotoEvent {
    data class InsertReceipt(val receipt: Receipt): PhotoEvent()
}