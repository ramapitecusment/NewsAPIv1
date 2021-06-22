package com.ramapitecusment.newsapi.common.mvvm

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseFragment<TViewModel : BaseViewModel>(@LayoutRes lId: Int) : Fragment(lId) {

    protected abstract val viewModel: TViewModel
    private val subscriptions: MutableList<Disposable> = mutableListOf()

    protected fun Disposable.addToSubscription() {
        subscriptions.add(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.forEach {
            it.dispose()
        }
    }
}