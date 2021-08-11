package com.example.android.politicalpreparedness.election.data

import com.example.android.politicalpreparedness.network.Result
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

interface ElectionDataSource {
    suspend fun getElectionFromApi(): Result<ElectionResponse>
    suspend fun getSavedElection(): Result<List<Election>>
    suspend fun getVoterInfo(map: Map<String, Any>): Result<VoterInfoResponse>
    suspend fun saveElection(election: Election)
    suspend fun deleteElection(electionId: Int)
}