package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.election.data.ElectionRepository
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Division

class ElectionsFragment : Fragment(), ElectionListener {

    private val viewModel by lazy {
        ViewModelProvider(this,
                ElectionsViewModelFactory(ElectionRepository(CivicsApi.retrofitService,
                        ElectionDatabase.getInstance(requireContext()).electionDao)
                )).get(ElectionsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.electionViewModel = viewModel

        binding.upcomingElectionsRecyclerView.adapter = ElectionListAdapter(this)
        binding.savedElectionsRecyclerView.adapter = ElectionListAdapter(this)
        return binding.root
    }

    override fun onElectionClick(id: Int, division: Division) {

        println("id====")
        println("division====$division")

        this@ElectionsFragment.findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(id, division))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFollowedElections()
    }
}