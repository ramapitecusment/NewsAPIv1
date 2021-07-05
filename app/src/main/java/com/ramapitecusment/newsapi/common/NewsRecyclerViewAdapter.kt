package com.ramapitecusment.newsapi.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.databinding.NewsItemBinding
import com.ramapitecusment.newsapi.services.database.Article


class NewsRecyclerViewAdapter(
    private val articleClickListener: (article: Article) -> Unit,
    private val readLaterClickListener: (article: Article) -> Unit,
) : ListAdapter<Article, NewsRecyclerViewAdapter.ViewHolder>(NewsCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), articleClickListener, readLaterClickListener)
    }

    class ViewHolder(private val binding: NewsItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            article: Article,
            articleClickListener: (article: Article) -> Unit,
            readLaterClickListener: (article: Article) -> Unit
        ) {
            binding.authorTextView.text = article.author
            binding.titleTextView.text = article.title
            binding.descriptionTextView.text = article.description
            binding.sourceTextView.text = article.source
            binding.timeTextView.text = article.publishedAt
            binding.newsImageView.glideImage(article.urlToImage, binding.imageProgressBar)

            if (article.isReadLater == 1)
                binding.readLaterImageButton.setImageResource(R.drawable.ic_bookmark_red)
            else
                binding.readLaterImageButton.setImageResource(R.drawable.ic_bookmark_white)

            binding.cardView.setOnClickListener {
                articleClickListener(article)
            }
            binding.readLaterImageButton.setOnClickListener {
                readLaterClickListener(article)
            }
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
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}

