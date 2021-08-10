package com.example.android.politicalpreparedness.election

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ElectionRepository(private val database: ElectionDatabase) {

    // The list of elections that can be shown on the screen.
    val allElections: LiveData<List<Election>> = database.electionDao.getSavedElections()

    // The list of followed elections.
    val allFollowedElections: LiveData<List<Election>> = database.electionDao.getFollowedElections()

    //Voter Info
    val voterInfo = MutableLiveData<VoterInfoResponse>()


    /*The refreshElections() function refresh the offline cache.
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the elections for use, observe [elections]
     */
    suspend fun refreshElections() {
        withContext(Dispatchers.IO) {
            try {
                // Get String Json response via Retrofit
                val electionsResponse = CivicsApi.retrofitService.getElections()
                val result = electionsResponse.elections

                // Push the results to the database
                database.electionDao.insertAllElections(*result.toTypedArray())

                Log.d(TAG, result.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}