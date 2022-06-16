package br.com.sendevent.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import br.com.corelib.addChangedListener
import br.com.corelib.clearAll
import br.com.corelib.data.Person
import br.com.corelib.module.AppModule
import br.com.corelib.module.KoinUtilities
import br.com.corelib.viewmodel.MainViewModel
import br.com.sendevent.databinding.FragmentCheckinBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CheckinFragment : Fragment() {

    private val loadModules by lazy { KoinUtilities.loadModules(AppModule.eachModules()) }
    private fun injectModules() = loadModules
    private val mainViewModel: MainViewModel by sharedViewModel()

    private lateinit var binding: FragmentCheckinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        injectModules()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentCheckinBinding.inflate(layoutInflater).apply {
            lifecycleOwner = viewLifecycleOwner
        }

        initV()
        observer()
        return binding.root
    }

    private fun initV() {
        binding.exit.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.edtName.addChangedListener(
            afterTextChanged = { },
            onTextChanged = { s, _, _, _ ->
                if (s.isNotBlank()) {
                    binding.cvSubmit.isClickable = true
                    binding.cvSubmit.isFocusable = true
                } else {
                    binding.cvSubmit.isClickable = false
                    binding.cvSubmit.isFocusable = false
                }
            },
            beforeTextChanged = { _, _, _, _ -> }
        )

        binding.edtEmail.addChangedListener(
            afterTextChanged = { },
            onTextChanged = { s, _, _, _ ->
                if (s.isNotBlank()) {
                    binding.cvSubmit.isClickable = true
                    binding.cvSubmit.isFocusable = true
                } else {
                    binding.cvSubmit.isClickable = false
                    binding.cvSubmit.isFocusable = false
                }
            },
            beforeTextChanged = { _, _, _, _ -> }
        )

        binding.cvSubmit.setOnClickListener {
            if (binding.edtName.text.toString().isNotEmpty()
                && binding.edtEmail.text.toString().isNotEmpty()
            ) {

                binding.edtName.clearAll(false)
                binding.edtEmail.clearAll(false)

                binding.tvBtnCheckin.visibility = View.GONE
                binding.progressCheckin.visibility = View.VISIBLE

                mainViewModel.posChecking(
                    Person(
                        name = binding.edtName.text.toString(),
                        email = binding.edtEmail.text.toString(),
                        eventId = mainViewModel.event.value?.id.toString()
                    )
                )
            }
        }
    }

    private fun observer() {
        mainViewModel.isChecking.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    binding.progressCheckin.visibility = View.GONE
                    binding.tvBtnCheckin.animate().alpha(1f).setDuration(300).withEndAction {
                        binding.tvBtnCheckin.visibility = View.VISIBLE
                    }

                    binding.edtName.clearAll(true)
                    binding.edtEmail.clearAll(true)

                }
                false -> {
                    binding.viewResponse.visibility = View.VISIBLE
                    binding.viewResponse.animate().alpha(1f).setDuration(400).setStartDelay(100)
                        .withEndAction {
                            findNavController().navigateUp()
                            binding.viewResponse.visibility = View.GONE
                            mainViewModel.doneChecking(null)
                        }
                }
                else -> {
                    binding.main.visibility = View.GONE
                    binding.error.visibility = View.VISIBLE
                    binding.error.animate().alpha(1f).setDuration(2000).setStartDelay(100)
                        .withEndAction {
                            findNavController().navigateUp()
                            binding.error.visibility = View.GONE
                            mainViewModel.doneChecking(true)
                        }
                }
            }
        }
    }
}