package com.ramapitecusment.newsapi.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ramapitecusment.newsapi.databinding.NewsItemBinding
import com.ramapitecusment.newsapi.model.database.Article
import com.ramapitecusment.newsapi.util.LOG

class NewsRecyclerViewAdapter(private val clickListener: (article: Article) -> Unit) :
    ListAdapter<Article, NewsRecyclerViewAdapter.ViewHolder>(NewsCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class ViewHolder(private val binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Article) {
            Log.d(LOG, "Recycler View Adapter bind: ")
            binding.article = article
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(NewsItemBinding.inflate(LayoutInflater.from(parent.context)))
            }
        }
    }
}

class NewsCallback : DiffUtil.ItemCallback<Article>() {
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.id == newItem.id
    }
}