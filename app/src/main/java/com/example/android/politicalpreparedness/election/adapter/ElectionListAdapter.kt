package com.example.android.politicalpreparedness.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.databinding.ItemElectionBinding
import com.example.android.politicalpreparedness.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener) :
        ListAdapter<Election, ElectionViewHolder>(ElectionDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ElectionViewHolder {
        return from(parent)
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        val election = getItem(position)

        //Set up onClickListener()
        holder.itemView.setOnClickListener {
            clickListener.onClick(election)
        }
        holder.bind(election)
    }

    companion object {
        fun from(parent: ViewGroup): ElectionViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemElectionBinding.inflate(layoutInflater, parent, false)
            return ElectionViewHolder(binding)
        }
    }
}


//Create ElectionViewHolder inner class, and implement the bind() method
// that includes a binding to Election.
class ElectionViewHolder(var binding: ItemElectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
    fun bind(election: Election) {
        binding.election = election

        // Call binding.executePendingBindings(), which causes the update to execute immediately.
        binding.executePendingBindings()
    }
}

//Create the ElectionDiffCallback companion object and override its two required areItemsTheSame() methods.
class ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
    override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
        return oldItem.id == newItem.id
    }
}

class ElectionListener(val clickListener: (election: Election) -> Unit) {
    fun onClick(election: Election) = clickListener(election)
}