package com.ithe.ss.imv

import android.annotation.SuppressLint
import android.content.Context


class ContextProvider {

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null

        fun init(context: Context) {
            this.context = context
        }

        fun get(): Context {
            return context!!
        }
    }
}