package com.example.android.politicalpreparedness.election

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.election.data.ElectionRepository
import com.example.android.politicalpreparedness.network.CivicsApi


class VoterInfoFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProvider(this,
                VoterInfoViewModelFactory(ElectionRepository(CivicsApi.retrofitService,
                        ElectionDatabase.getInstance(requireContext()).electionDao))).get(VoterInfoViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.voterInfoViewModel = viewModel
        arguments?.let {
            val args = VoterInfoFragmentArgs.fromBundle(it)
            println("args===" + args.argElectionId)
            println("Division.state===" + args.argDivision.state)

            if (args.argElectionId == 0 || args.argDivision.country.isEmpty()) {
                openErrorDialog()
                return@let
            }

            viewModel.passArguments(args.argElectionId, args.argDivision)
            viewModel.openLinkAction.observe(viewLifecycleOwner, Observer { url ->
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(url)
                })
            })
        }

        return binding.root
    }

    private fun openErrorDialog() {
        AlertDialog.Builder(context)
                .setTitle(R.string.error_title)
                .setCancelable(false)
                .setMessage(R.string.error_message)
                .setPositiveButton(R.string.alert_btn) { dialog, _ ->
                    dialog.dismiss()
                    this.findNavController().popBackStack()
                }.show()
    }
}