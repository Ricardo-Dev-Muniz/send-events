package br.com.corelib.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import br.com.corelib.calls.read
import br.com.corelib.data.Event
import br.com.corelib.data.Person
import br.com.corelib.data.Status
import br.com.corelib.network.ServiceInterceptor
import br.com.corelib.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(
    application: Application,
    private val repository: MainRepository,
) : AndroidViewModel(application),
    ServiceInterceptor.OnServiceResponseListener {

    private val _events: MutableLiveData<Array<Event?>?> = MutableLiveData()
    val events: LiveData<Array<Event?>?> = _events

    private val _event: MutableLiveData<Event?> = MutableLiveData()
    val event: LiveData<Event?> = _event

    private val _flowEvent: MutableLiveData<String> = MutableLiveData()
    val flowEvent: LiveData<String> = _flowEvent

    private val _isChecking: MutableLiveData<Boolean?> = MutableLiveData()
    val isChecking: LiveData<Boolean?> = _isChecking

    private val _isScreen: MutableLiveData<Boolean?> = MutableLiveData(false)
    val isScreen: LiveData<Boolean?> = _isScreen

    private val _code: MutableLiveData<Int?> = MutableLiveData()
    val code: LiveData<Int?> = _code

    private val _isState: MutableLiveData<Status> = MutableLiveData()
    val isState: LiveData<Status> = _isState

    fun getEvents() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.getEvents()
        }.read({
            _events.value = it
            _isState.value = Status.SUCCESS
        }, {
            _isState.value = Status.FAILURE
            Log.e("error_events", "error events load: ${it.message}")
        })
    }

    fun getEventsById(id: String) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.getEventsById(id)
        }.read({
            _event.value = it
            _isState.value = Status.SUCCESS
        }, {
            _isState.value = Status.FAILURE
            Log.e("error_events", "error event details: ${it.message}")
        })
    }

    fun posChecking(person: Person) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            repository.posChecking(person)
        }.read({
            _isChecking.value = true
            if (it.toString().isNotBlank() && it != "")
                _isChecking.value = false
        }, {
            _isChecking.value = null
            Log.e("error_events", "error post checking: ${it.message}")
        })
    }

    fun flowEvent(eventId: String) {
        _flowEvent.value = eventId
    }

    fun doneChecking(isDone: Boolean?) {
        _isChecking.value = isDone
    }

    fun isScreen(isOn: Boolean?) {
        _isScreen.value = isOn
    }

    override fun onReceiveResponseCode(code: Int) {}
}



