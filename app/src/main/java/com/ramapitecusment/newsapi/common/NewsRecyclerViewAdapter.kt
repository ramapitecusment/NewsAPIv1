package com.ramapitecusment.newsapi.common

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ramapitecusment.newsapi.databinding.NewsItemBinding
import com.ramapitecusment.newsapi.services.database.ArticleEntity

class NewsRecyclerViewAdapter(private val clickListener: (articleEntity: ArticleEntity) -> Unit) :
    ListAdapter<ArticleEntity, NewsRecyclerViewAdapter.ViewHolder>(NewsCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(private val binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(articleEntity: ArticleEntity) {
            Log.d(LOG, "Recycler View Adapter bind: ")
            binding.article = articleEntity
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(NewsItemBinding.inflate(LayoutInflater.from(parent.context)))
            }
        }
    }
}

class NewsCallback : DiffUtil.ItemCallback<ArticleEntity>() {
    override fun areItemsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ArticleEntity, newItem: ArticleEntity): Boolean {
        return oldItem.id == newItem.id
    }
}