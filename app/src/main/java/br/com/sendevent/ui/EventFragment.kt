package br.com.sendevent.ui

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import br.com.corelib.data.Status
import br.com.corelib.share
import br.com.corelib.module.AppModule
import br.com.corelib.module.KoinUtilities
import br.com.corelib.callLoad
import br.com.corelib.launchImage
import br.com.corelib.viewmodel.MainViewModel
import br.com.sendevent.R
import br.com.sendevent.databinding.FragmentEventsBinding
import com.bumptech.glide.Glide
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class EventFragment : Fragment() {

    private val loadModules by lazy { KoinUtilities.loadModules(AppModule.eachModules()) }
    private fun injectModules() = loadModules
    private val mainViewModel: MainViewModel by sharedViewModel()

    private lateinit var binding: FragmentEventsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        injectModules()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEventsBinding.inflate(layoutInflater).apply {
            lifecycleOwner = viewLifecycleOwner
        }

        initV()
        observers()
        return binding.root
    }

    private fun initV() {
        if (mainViewModel.isScreen.value == false) {
            suspend { callLoad(requireContext(), binding.progress, binding.main) }
            mainViewModel.isScreen(true)
        }

        mainViewModel.flowEvent.value.let {
            it?.let { byId -> mainViewModel.getEventsById(byId) }
        }

        binding.exit.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cvShare.setOnClickListener {
            val bitmap = (binding.ivImageEvent.drawable as BitmapDrawable?)?.bitmap

            bitmap?.copy(bitmap.config, true)
                ?.let { uri -> share(requireContext(), binding.tvAboutEvent.text.toString(), uri) }

        }

        binding.cvCheckin.setOnClickListener {
            mainViewModel.event.value?.id?.let { eventId ->
                mainViewModel.flowEvent(eventId = eventId)
            }

            findNavController().navigate(R.id.action_Events_to_checkinFragment)
        }
    }

    private fun observers() {
        mainViewModel.isScreen.observe(viewLifecycleOwner) {
            if (it == true && mainViewModel.isState.value != Status.FAILURE) {
                binding.progress.animate().alpha(1f)
                    .setDuration(400).setStartDelay(100)
                    .withEndAction {
                        binding.progress.visibility = View.GONE
                        binding.main.visibility = View.VISIBLE
                    }
            } else if (it == false && mainViewModel.isState.value != Status.FAILURE) {
                binding.progress.animate().alpha(0f)
                    .setDuration(400).setStartDelay(100)
                    .withEndAction {
                        binding.main.visibility = View.GONE
                        binding.progress.visibility = View.VISIBLE
                    }
            }
        }

        mainViewModel.event.observe(viewLifecycleOwner) { event ->
            Glide.with(requireContext()).load("").error(R.color.white).into(binding.ivImageEvent)
            launchImage(event?.image, 200, requireContext()) {
                binding.ivImageEvent.animate().setStartDelay(0).alpha(1f).setDuration(400)
                    .withEndAction {
                        binding.ivImageEvent.setImageBitmap(it)
                    }
            }

            binding.tvTitleEvent.text = event?.title
            binding.tvAboutEvent.text = event?.description
            binding.tvPeople.text =
                if (event?.people?.toList()?.size == 0)
                    getString(R.string.confirm_peoples_zero) else event?.people.toString()
            binding.tvDate.text = event?.date.toString()

            suspend { callLoad(requireContext(), binding.progress, binding.main) }

        }

        mainViewModel.isState.observe(viewLifecycleOwner) {
            if (it.name == Status.FAILURE.name) {
                binding.progress.visibility = View.GONE
                binding.error.visibility = View.VISIBLE
                binding.main.visibility = View.GONE
            }
        }
    }
}