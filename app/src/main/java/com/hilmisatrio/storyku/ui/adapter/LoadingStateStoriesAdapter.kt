package com.hilmisatrio.storyku.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hilmisatrio.storyku.databinding.ItemLoadingDataStoryBinding

class LoadingStateStoriesAdapter(private val retryLoadData: () -> Unit) :
    LoadStateAdapter<LoadingStateStoriesAdapter.LoadingStateViewHolder>() {
    class LoadingStateViewHolder(
        val binding: ItemLoadingDataStoryBinding,
        retryLoadData: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.buttonRetry.setOnClickListener { retryLoadData.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMessage.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.buttonRetry.isVisible = loadState is LoadState.Error
            binding.errorMessage.isVisible = loadState is LoadState.Error
        }
    }

    override fun onBindViewHolder(holder: LoadingStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadingStateViewHolder {
        val view =
            ItemLoadingDataStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingStateViewHolder(view, retryLoadData)
    }
}