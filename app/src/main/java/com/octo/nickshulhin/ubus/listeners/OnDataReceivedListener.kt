package com.octo.nickshulhin.ubus.listeners

/**
 * Created by nickshulhin on 24/3/18.
 */
interface OnDataReceivedListener<T> {
    fun onDataReceived(data: T)
}