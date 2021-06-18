package com.ramapitecusment.newsapi.common.mvvm

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment<TViewModel : BaseViewModel>(@LayoutRes lId: Int) : Fragment(lId) {

    protected abstract val viewModel: TViewModel


}