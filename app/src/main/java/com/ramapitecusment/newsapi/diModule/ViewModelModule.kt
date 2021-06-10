package com.ramapitecusment.newsapi.diModule

import com.ramapitecusment.newsapi.scenes.everything.EverythingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        EverythingViewModel(get())
    }

}