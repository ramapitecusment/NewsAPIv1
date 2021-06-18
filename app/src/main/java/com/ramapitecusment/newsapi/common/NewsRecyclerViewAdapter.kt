package com.ramapitecusment.newsapi.common

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.databinding.NewsItemBinding
import com.ramapitecusment.newsapi.services.database.Article
import com.ramapitecusment.newsapi.services.database.ArticleEntity

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
            Log.d(LOG, "Recycler View Adapter bind")
            glideImage(article.urlToImage)
            initView(article)
        }

        private fun initView(article: Article) {
            with(binding) {
                author.text = article.author
                title.text = article.title
                description.text = article.description
                source.text = article.source
                time.text = article.publishedAt
            }
        }

        private fun glideImage(urlToImage: String?) {
            urlToImage?.let {
                Glide.with(binding.newsImage.context)
                    .load(it)
                    .apply(RequestOptions().error(R.drawable.ic_connection_error))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressbar.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressbar.visibility = View.GONE
                            return false
                        }
                    })
                    .into(binding.newsImage)
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