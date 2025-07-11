package com.example.recipttracker.ui.addReceipt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DisplayImageViewModel: ViewModel() {
    private var _uriPath: MutableLiveData<String> = MutableLiveData<String>()
    val uriPath: LiveData<String> = _uriPath

    fun changeUriPath(newUriPath: String) {
        this._uriPath.value = newUriPath
    }
}
