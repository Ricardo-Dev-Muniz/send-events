package br.com.corelib.module

import android.app.Application
import org.koin.core.context.loadKoinModules

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        KoinUtilities.loadKoin(applicationContext)
        loadKoinModules(AppModule.eachModules())
    }
}