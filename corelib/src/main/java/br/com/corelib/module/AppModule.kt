package br.com.corelib.module

import br.com.corelib.calls.Api
import br.com.corelib.network.ServiceProvider
import br.com.corelib.repository.MainRepository
import br.com.corelib.repository.RepositoryImpl
import br.com.corelib.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModule {

    fun eachModules() = arrayListOf(
        module,
        repository,
        service
    )

    private val module = module {
        viewModel { MainViewModel(androidApplication(), repository = get()) }
    }

    private val repository = module {
        single<MainRepository>{ RepositoryImpl(get()) }
    }


    private val service = module {
        single {
            ServiceProvider(
                url = "https://5f5a8f24d44d640016169133.mockapi.io/api/", headers = listOf(
                    Pair("Content-Type", "application/json"),
                    Pair("Accept", "application/json"),
                    Pair("Connection", "close"),
                    Pair("x-platform", "Mobile.Android")
                )
            ).generate() as Api
        }
    }

}