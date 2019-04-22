package com.chibatching.flowreactiveuisample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chibatching.flowreactiveuisample.databinding.ListItemResultBinding

class RepoListAdapter : RecyclerView.Adapter<RepoListAdapter.ViewHolder>() {

    var repos: List<Repo> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int = repos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.repoName = repos[position].full_name
    }

    class ViewHolder(val binding: ListItemResultBinding) : RecyclerView.ViewHolder(binding.root)
}
