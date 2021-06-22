package com.ramapitecusment.newsapi.common.mvvm

import android.app.Application
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import com.ramapitecusment.newsapi.MainApplication
import com.ramapitecusment.newsapi.common.LOG
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.*
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

typealias Text = BaseViewModel.TextProperty
typealias Checkable = BaseViewModel.CheckableProperty
typealias Data<T> = BaseViewModel.DataProperty<T>
typealias DataList<T> = BaseViewModel.DataListProperty<T>
typealias Enabled = BaseViewModel.EnabledProperty
typealias Progress = BaseViewModel.ProgressProperty
typealias Visible = BaseViewModel.VisibleProperty

abstract class BaseViewModel() : AndroidViewModel(MainApplication.instance) {
    private val subscriptions: MutableList<Disposable> = mutableListOf()
    private val subscriptionsWhileVisible: MutableList<Disposable> = mutableListOf()

    open fun stop() {
        subscriptionsWhileVisible.forEach { it.dispose() }
        subscriptionsWhileVisible.clear()
    }

    open fun destroy() {
        subscriptions.forEach { it.dispose() }
        subscriptions.clear()
    }

    protected fun Completable.subscribeOnIoObserveMain() =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    protected fun <T> Single<T>.subscribeOnIoObserveMain() =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    protected fun <T> Maybe<T>.subscribeOnIoObserveMain() =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    protected fun <T> Maybe<T>.subscribeOnSingleObserveMain() =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    protected fun <T> Flowable<T>.subscribeOnIoObserveMain() =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    protected fun <T> Observable<T>.subscribeOnIoObserveMain() =
        this.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())

    protected fun <T> Observable<T>.subscribeOnSingleObserveMain() =
        this.subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread())

    protected fun getString(@StringRes resId: Int, vararg formatArgs: Any): String =
        this.getApplication<Application>().getString(resId, *formatArgs)

    protected fun showLog(log: String) = Log.d(LOG, log)

    protected fun showErrorLog(log: String) = Log.e(LOG, log)

    protected fun Disposable.addToSubscription() {
        subscriptions.add(this)
    }

    protected fun Disposable.addToSubscriptionsWhileVisible() {
        subscriptionsWhileVisible.add(this)
    }

    class CheckableProperty(value: Boolean = false) : MutableBindingProperty<Boolean>(value)
    class DataListProperty<T>(value: List<T> = emptyList()) : MutableBindingProperty<List<T>>(value)
    class DataProperty<T>(value: T) : MutableBindingProperty<T>(value)
    class EnabledProperty(value: Boolean = false) : MutableBindingProperty<Boolean>(value)
    class ProgressProperty(value: Float = 0f) : MutableBindingProperty<Float>(value)
    class TextProperty(value: String = "") : MutableBindingProperty<String>(value)
    class VisibleProperty(value: Boolean = false) : MutableBindingProperty<Boolean>(value)

    abstract class MutableBindingProperty<T>(defaultValue: T) {
        private val mutableLiveData = MutableLiveData<T>()

        val liveData: LiveData<T> = mutableLiveData
        val value: T
            get() = liveData.value!!

        init {
            mutableLiveData.value = defaultValue!!
        }

        fun observe(owner: LifecycleOwner, observer: Observer<T>) =
            liveData.observe(owner, observer)

        fun observeForever(observer: (value: T) -> Unit) = liveData.observeForever { observer(it) }
    }

    protected var <T> MutableBindingProperty<T>.mutableValue: T
        get() = value
        set(value) {
            (liveData as MutableLiveData<T>).postValue(value)
        }
}