package com.example.android.politicalpreparedness.election

import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.election.data.ElectionDataSource
import com.example.android.politicalpreparedness.network.Result
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.launch

class VoterInfoViewModel(private val electionDataSource: ElectionDataSource) : ViewModel() {

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    private val voterInfo: LiveData<VoterInfoResponse>
        get() = _voterInfo

    val toolbarTitle = Transformations.map(voterInfo) {
        it.election.name
    }

    val voterDate = Transformations.map(voterInfo) {
        it.election.electionDay
    }

    val correspondence = Transformations.map(voterInfo) {
        it.state?.firstOrNull()?.let { state ->
            state.electionAdministrationBody.correspondenceAddress?.line1
        }
    }

    val stateAddress = Transformations.map(voterInfo) {
        it.state?.firstOrNull()?.electionAdministrationBody?.correspondenceAddress?.let { address ->
            "${address.city}, ${address.state} ${address.zip}}"
        }
    }

    val stateHeader = Transformations.map(voterInfo) {
        it.state?.firstOrNull()?.name
    }

    val locationLink = Transformations.map(voterInfo) {
        it?.state?.firstOrNull()?.electionAdministrationBody?.votingLocationFinderUrl
    }

    val ballotInfoLink = Transformations.map(voterInfo) {
        it?.state?.firstOrNull()?.electionAdministrationBody?.ballotInfoUrl
    }

    private val _openLinkAction = MutableLiveData<String>()
    val openLinkAction: LiveData<String>
        get() = _openLinkAction

    private val _hasAlreadySavedElection = MutableLiveData<Boolean>()
    val hasAlreadySavedElection: LiveData<Boolean>
        get() = _hasAlreadySavedElection

    private val _showProgressBar = MutableLiveData<Boolean>()
    val showProgressBar: LiveData<Boolean>
        get() = _showProgressBar


    fun passArguments(argElectionId: Int, argDivision: Division) {
        checkElectionSaved(argElectionId)

        viewModelScope.launch {
            _showProgressBar.value = true

            var state = argDivision.state
            if (state.isEmpty()) {
                state = "ca";
            }

            val result = electionDataSource.getVoterInfo(mapOf(

                    "electionId" to argElectionId,
                    "address" to "country:${argDivision.country}/state: $state}"

            ))


            /*var address = "country:${argDivision.country}"
            if (!argDivision.state.isBlank() && !argDivision.state.isEmpty()) {
                address += "/state:${argDivision.state}"
            } else {
                address += "/state:ca"
            }
            println("address=="+address)
            _voterInfo.value = CivicsApi.retrofitService.getVoterInfo1(
                    address, argElectionId)

            _showProgressBar.value = false*/
            when (result) {
                is Result.Success<*> -> {
                    _showProgressBar.value = false
                    result.data?.let {
                        _voterInfo.value = it as VoterInfoResponse
                    }
                }
                is Result.Error -> {
                    _showProgressBar.value = false
                    Log.e("VoterInfoViewModel", result.message)
                }
            }
        }
    }

    private fun checkElectionSaved(argElectionId: Int) {
        viewModelScope.launch {
            when (val result = electionDataSource.getSavedElection()) {
                is Result.Success<*> -> {
                    result.data?.let { list ->
                        _hasAlreadySavedElection.value = (list as? List<Election>)?.any { it.id == argElectionId }
                    } ?: run {
                        _hasAlreadySavedElection.value = false
                    }
                }
                is Result.Error -> {
                    _hasAlreadySavedElection.value = false
                }
            }
        }
    }

    fun openLink(url: String?) {
        url?.let {
            _openLinkAction.value = url
        }
    }

    fun onSaveElection() {
        viewModelScope.launch {
            voterInfo.value?.election?.let {
                if (_hasAlreadySavedElection.value == true) {
                    electionDataSource.deleteElection(it.id)
                    _hasAlreadySavedElection.value = false
                } else {
                    electionDataSource.saveElection(it)
                    _hasAlreadySavedElection.value = true
                }
            }
        }
    }
}