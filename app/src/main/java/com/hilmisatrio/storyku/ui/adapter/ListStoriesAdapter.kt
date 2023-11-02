package com.hilmisatrio.storyku.ui.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hilmisatrio.storyku.R
import com.hilmisatrio.storyku.data.room.Story
import com.hilmisatrio.storyku.databinding.ItemStoryBinding
import com.hilmisatrio.storyku.ui.detail.DetailFragment
import com.hilmisatrio.storyku.utils.creationDate

class ListStoriesAdapter :
    PagingDataAdapter<Story, ListStoriesAdapter.MyViewHolder>(DIFF_CALLBACK) {

    class MyViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val dataStory = getItem(position)

        if (dataStory != null) {
            with(holder.binding) {
                imageContent.loadImageUser(dataStory.photoUrl)
                tvName.text = dataStory.name
                tvContent.text = dataStory.description
                tvCreateDate.text = dataStory.createdAt.creationDate()
            }

            holder.itemView.setOnClickListener {
                val idBundle = Bundle().apply {
                    putString(DetailFragment.ID_STORY, dataStory.id)
                }
                it.findNavController()
                    .navigate(R.id.action_homeFragment_to_detailFragment, idBundle)
            }
        }

    }

    private fun ImageView.loadImageUser(urlImage: String?) {
        Glide.with(this.context).load(urlImage).into(this)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}