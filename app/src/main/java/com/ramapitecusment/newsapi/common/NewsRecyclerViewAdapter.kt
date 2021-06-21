package com.ramapitecusment.newsapi.common

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.ramapitecusment.newsapi.R
import com.ramapitecusment.newsapi.databinding.NewsItemBinding
import com.ramapitecusment.newsapi.services.database.Article

class NewsRecyclerViewAdapter(
    private val clickListener: (article: Article) -> Unit,
//    private val binding: NewsItemBinding,
    @LayoutRes private val layoutResId: Int,
    compareItms: (old: Article, new: Article) -> Boolean,
    compareCnts: (old: Article, new: Article) -> Boolean
) : BaseRecyclerView<Article>(
    clickListener,
    layoutResId,
    compareItms,
    compareCnts
) {

    private lateinit var author: TextView
    private lateinit var title: TextView
    private lateinit var description: TextView
    private lateinit var source: TextView
    private lateinit var time: TextView
    private lateinit var newsImage: ImageView
    private lateinit var progressbar: ProgressBar

    override fun ViewHolder.onBind(item: Article) {
        Log.d(LOG, "Recycler View Adapter bind")

        author = itemView.findViewById(R.id.author)
        title = itemView.findViewById(R.id.title)
        description = itemView.findViewById(R.id.description)
        source = itemView.findViewById(R.id.source)
        time = itemView.findViewById(R.id.time)
        newsImage = itemView.findViewById(R.id.news_image)
        progressbar = itemView.findViewById(R.id.progressbar)

        initView(item)
    }

    private fun initView(article: Article) {
        author.text = article.author
        title.text = article.title
        description.text = article.description
        source.text = article.source
        time.text = article.publishedAt

        Glide.bindImage(article.urlToImage, newsImage, progressbar)
        glideImage(article.urlToImage)
    }

    private fun glideImage(urlToImage: String?) {
        urlToImage?.let {
            Glide.with(newsImage.context)
                .load(it)
                .apply(RequestOptions().error(R.drawable.ic_connection_error))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressbar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressbar.visibility = View.GONE
                        return false
                    }
                })
                .into(newsImage)
        }
    }
}

