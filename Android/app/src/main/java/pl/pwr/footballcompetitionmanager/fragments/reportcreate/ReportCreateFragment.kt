package pl.pwr.footballcompetitionmanager.fragments.reportcreate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.FragmentReportCreateBinding
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber

class ReportCreateFragment : Fragment() {

    private lateinit var binding: FragmentReportCreateBinding
    private lateinit var viewModelFactory: ReportCreateViewModelFactory
    private lateinit var viewModel: ReportCreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_create, container, false)
        viewModelFactory = ReportCreateViewModelFactory(RemoteRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(ReportCreateViewModel::class.java)
        binding.viewModel = viewModel

        observeViewModel()
        observeEditText()

        Timber.d("onCreateView finished")
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.reportCreationSuccessful.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().navigate(ReportCreateFragmentDirections.actionCreateReportFragmentToHomeFragment(getString(R.string.fragment_report_create_successful_message)))
                viewModel.navigatedToHomeScreen()
            }
        })

        viewModel.reportCreationErrorMessage.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.bugDescriptionTextField.error = getString(it)
            } else {
                binding.bugDescriptionTextField.error = null
            }
        })

        viewModel.getSnackbarMessage().observe(viewLifecycleOwner, Observer {
            if (it != null)
                Snackbar.make(binding.linearLayout, getString(it), Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun observeEditText() {
        binding.bugDescriptionTextField.editText?.doAfterTextChanged { viewModel.clearDescriptionTextFieldError() }
    }
}