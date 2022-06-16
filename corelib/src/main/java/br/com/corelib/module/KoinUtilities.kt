package br.com.corelib.module

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.KoinContextHandler
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.module.Module

object KoinUtilities {

    fun loadModules(modules: List<Module>) {
        loadKoinModules(modules)
    }

    fun loadKoin(context: Context) {
        KoinContextHandler.getOrNull() ?: run {
            startKoin {
                androidContext(context)
            }
        }
    }
}