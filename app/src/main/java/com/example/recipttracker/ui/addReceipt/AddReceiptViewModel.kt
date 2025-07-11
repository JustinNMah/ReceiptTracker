package com.example.recipttracker.ui.addReceipt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipttracker.addReceipt.AddReceiptEvent
import com.example.recipttracker.domain.repository.ReceiptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AddReceiptViewModel @Inject constructor(
    private val repository: ReceiptRepository
): ViewModel() {
    fun onEvent(event: AddReceiptEvent) {
        when(event) {
            is AddReceiptEvent.InsertReceipt -> {
                viewModelScope.launch {
                    repository.insertReceipt(event.receipt)
                }
            }
        }
    }
}
