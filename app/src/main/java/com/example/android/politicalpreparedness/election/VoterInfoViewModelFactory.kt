package com.example.android.politicalpreparedness.election

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.election.data.ElectionDataSource

@Suppress("UNCHECKED_CAST")
class VoterInfoViewModelFactory(private val electionDataSource: ElectionDataSource) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VoterInfoViewModel::class.java)) {
            return VoterInfoViewModel(electionDataSource) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
