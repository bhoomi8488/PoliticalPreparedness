package com.example.android.politicalpreparedness.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveElection(election: Election)

    @Query("SELECT * FROM election_table")
    suspend fun getElections(): List<Election>

    @Query("SELECT * FROM election_table where id =:electionId")
    suspend fun getElectionsById(electionId: Int): List<Election>

    @Query("DELETE FROM election_table WHERE id = :electionId")
    suspend fun deleteElectionById(electionId: Int)

    @Query("DELETE FROM election_table")
    suspend fun deleteAllReminders()
}