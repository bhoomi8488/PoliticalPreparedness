package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ItemElectionBinding
import com.example.android.politicalpreparedness.network.models.Division
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener) :
        ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val election = getItem(position)
        holder.binding.election = election
        holder.binding.executePendingBindings()
        holder.binding.root.setOnClickListener {
            clickListener.onElectionClick(election.id, election.division)
        }
    }
}

class ElectionViewHolder(val binding: ItemElectionBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): ElectionViewHolder {
            return ElectionViewHolder(ItemElectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false))
        }
    }
}

class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem == newItem
    }
}

interface ElectionListener {
    fun onElectionClick(id: Int, division: Division)
}