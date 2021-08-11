package com.example.android.politicalpreparedness.utils

import android.widget.Button
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.network.models.Election
import java.text.SimpleDateFormat
import java.util.*


object BindingAdapters {

    @BindingAdapter("android:liveData")
    @JvmStatic
    fun setRecyclerViewData(recyclerView: RecyclerView, items: LiveData<List<Election>>) {
        items.value?.let { itemList ->
            (recyclerView.adapter as ElectionListAdapter).apply {
                submitList(itemList)
            }
        }
    }

    @BindingAdapter("android:setDateAsText")
    @JvmStatic
    fun setDateAsText(view: TextView, date: Date?) {
        date?.let {
            val format = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            view.text = format.format(it)
        }
    }

    @BindingAdapter("android:voterButtonText")
    @JvmStatic
    fun voterButtonText(button: Button, isSaved: Boolean?) {
        button.text = button.context.getString(if (isSaved == true) R.string.un_follow_election else R.string.follow_election_text)
    }
}