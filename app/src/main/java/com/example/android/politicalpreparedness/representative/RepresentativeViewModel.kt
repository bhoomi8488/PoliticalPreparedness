package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel : ViewModel() {

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
        get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address


    fun getRepresentatives(address: String) {
        viewModelScope.launch {
            val (offices, officials) = CivicsApi.retrofitService.getRepresentatives(address)
            _representatives.value = offices.flatMap { office ->
                office.getRepresentatives(officials)
            }
        }
    }

    fun getAddressFromLocation(address: Address) {
        _address.value = address
    }

    init {
        // Avoid a crash in case which the user clicks on find my representatives without adding an ddress.
        _address.value = Address("", "", "", "New York", "")
    }

}
