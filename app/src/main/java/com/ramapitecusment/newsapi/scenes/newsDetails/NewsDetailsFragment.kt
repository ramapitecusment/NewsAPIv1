package com.ramapitecusment.newsapi.scenes.newsDetails

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.ramapitecusment.newsapi.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.ramapitecusment.newsapi.common.baseSetup
import com.ramapitecusment.newsapi.common.glideImage
import com.ramapitecusment.newsapi.common.mvvm.BaseFragment
import com.ramapitecusment.newsapi.databinding.FragmentNewsDetailsBinding
import com.ramapitecusment.newsapi.services.database.Article

class NewsDetailsFragment : BaseFragment<NewsDetailsViewModel>(R.layout.fragment_news_details) {
    override val viewModel: NewsDetailsViewModel by viewModel()
    private val binding: FragmentNewsDetailsBinding by viewBinding()
    private val args: NewsDetailsFragmentArgs by navArgs()
    private lateinit var article: Article

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        article = args.article
        initViews()
        bindViewModel()
    }

    private fun initViews() {
        binding.readLaterImageButton.setOnClickListener {
            if (article.isReadLater == 1) {
                article.isReadLater = 0
                binding.readLaterImageButton.setImageResource(R.drawable.ic_bookmark_white)
            } else if (article.isReadLater == 0) {
                article.isReadLater = 1
                binding.readLaterImageButton.setImageResource(R.drawable.ic_bookmark_red)
            }
        }
    }

    override fun bindViewModel() {
        super.bindViewModel()
        viewModel.articles.observe(viewLifecycleOwner) {
            with(binding) {
                titleTextView.text = article.title
                descriptionTextView.text = article.description
                newsImageView.glideImage(article.urlToImage, binding.imageProgressBar)

                if (article.isReadLater == 1)
                    readLaterImageButton.setImageResource(R.drawable.ic_bookmark_red)
                else
                    readLaterImageButton.setImageResource(R.drawable.ic_bookmark_white)

                sourceTextView.text = article.source
                timeTextView.text = article.publishedAt

                detailsWebView.baseSetup()
                article.url?.let { detailsWebView.loadUrl(it) }
                if (detailsWebView.isShown) {
                    detailsWebViewProgressBar.visibility = View.INVISIBLE
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.stop(article)
    }

}