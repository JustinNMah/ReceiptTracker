package com.example.recipttracker.domain.util

sealed class ReceiptSortOrder {
    data object DateDesc: ReceiptSortOrder()
    data object DateAsc: ReceiptSortOrder()
    data object StoreDesc: ReceiptSortOrder()
    data object StoreAsc: ReceiptSortOrder()
    data object CategoryDesc: ReceiptSortOrder()
    data object CategoryAsc: ReceiptSortOrder()
}