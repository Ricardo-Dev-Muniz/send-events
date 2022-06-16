package br.com.sendevent.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.corelib.data.Event
import br.com.corelib.data.Status
import br.com.corelib.module.AppModule
import br.com.corelib.module.KoinUtilities
import br.com.corelib.viewmodel.MainViewModel
import br.com.sendevent.R
import br.com.sendevent.adapter.EventsAdapter
import br.com.sendevent.databinding.FragmentHomeBinding
import br.com.sendevent.listener.OnclickListener
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : Fragment() {

    private val loadModules by lazy { KoinUtilities.loadModules(AppModule.eachModules()) }
    private fun injectModules() = loadModules
    internal val mainViewModel: MainViewModel by sharedViewModel()

    private lateinit var binding: FragmentHomeBinding
    private var adapter: EventsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        injectModules()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater).apply {
            lifecycleOwner = viewLifecycleOwner
        }

        initV()
        observers()
        return binding.root
    }

    private fun initV() {
        mainViewModel.getEvents()
        binding.progress.visibility = View.VISIBLE
    }

    private fun observers() {
        mainViewModel.events.observe(viewLifecycleOwner) {
            setAdapter(it)
        }

        mainViewModel.isState.observe(viewLifecycleOwner) {
            if (it.name == Status.FAILURE.name) {
                binding.progress.visibility = View.GONE
                binding.error.visibility = View.VISIBLE
            }
        }
    }

    private fun setAdapter(body: Array<Event?>?) {
        adapter = EventsAdapter(
            requireContext(),
            body?.toMutableList()
        )

        val manager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)

        binding.rvEvents.layoutManager = manager
        binding.rvEvents.adapter = adapter

        adapter?.onClickItem(object : OnclickListener {
            override fun flowId(id: String) {
                if (id.isNotBlank()) {
                    mainViewModel.flowEvent(eventId = id)
                    findNavController().navigate(R.id.action_Home_to_Events)
                }
            }
        })

        binding.progress.visibility = View.GONE
        binding.main.visibility = View.VISIBLE
    }
}