package com.example.recipttracker.ui.addEditReceipt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipttracker.domain.model.Receipt

class ReceiptToEditOrAdd: ViewModel() {
    private var _uriPath: MutableLiveData<String> = MutableLiveData<String>()
    val uriPath: LiveData<String> = _uriPath

    private var _receiptToEdit: MutableLiveData<Receipt?> = MutableLiveData<Receipt?>(null)
    val receiptToEdit: LiveData<Receipt?> = _receiptToEdit

    fun changeUriPath(newUriPath: String) {
        this._uriPath.value = newUriPath
    }

    fun changeReceiptToEdit(receiptToEdit: Receipt) {
        this._receiptToEdit.value = receiptToEdit
    }
}
