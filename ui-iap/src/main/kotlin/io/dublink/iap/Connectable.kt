package io.dublink.iap

import io.reactivex.Flowable

interface Connectable<T> {

    fun connect(): Flowable<T>
}
