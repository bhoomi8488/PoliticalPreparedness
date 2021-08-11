package com.example.android.politicalpreparedness.network.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class VoterInfoResponse(
        val election: Election,
        val state: List<State>? = null
)