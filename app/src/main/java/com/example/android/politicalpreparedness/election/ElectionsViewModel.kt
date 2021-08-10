package com.example.android.politicalpreparedness.election

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.launch

class ElectionsViewModel(application: Application) : ViewModel() {

    // Create the database singleton.
    // Define a database variable and assign it to getDatabase(), passing the application.
    private val database = ElectionDatabase.getInstance(application)

    // Create the repository.
    // Instantiate the variable by passing in the singleton AsteroidsDatabase object.
    private val electionRepository = ElectionRepository(database)

    // Create live data val for upcoming elections
    val upcomingElections: LiveData<List<Election>>
        get() = electionRepository.allElections


    val followedElections: LiveData<List<Election>>
        get() = electionRepository.allFollowedElections


    // Refresh the elections using the repository.
    // Create an init block and launch a coroutine to call electionRepository.refreshElections().
    init {
        viewModelScope.launch {
            electionRepository.refreshElections()
        }
    }

    private val _navigateToDetailElection = MutableLiveData<Election>()
    val navigateToDetailElection: LiveData<Election>
        get() = _navigateToDetailElection

    fun onElectionClicked(election: Election) {
        _navigateToDetailElection.value = election
    }

    fun onElectionNavigated() {
        _navigateToDetailElection.value = null
    }

}
