package pl.pwr.footballcompetitionmanager.fragments.changedata

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
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.FragmentChangeDataBinding
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber

class ChangeDataFragment : Fragment() {

    private lateinit var binding: FragmentChangeDataBinding
    private lateinit var viewModelFactory: ChangeDataViewModelFactory
    private lateinit var viewModel: ChangeDataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_data, container, false)
        viewModelFactory = ChangeDataViewModelFactory(RemoteRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChangeDataViewModel::class.java)
        binding.viewModel = viewModel

        binding.emailTextField.editText!!.setText(viewModel.getCurrentUserEmail())
        observeViewModel()
        observeEditTexts()

        Timber.d("onCreateView finished")
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.emailFieldError.observe(viewLifecycleOwner, Observer {
            if (it != null)
                binding.emailTextField.error = getString(it)
        })

        viewModel.passwordFieldError.observe(viewLifecycleOwner, Observer {
            if (it != null)
                binding.newPasswordTextField.error = it
        })

        viewModel.confirmPasswordFieldError.observe(viewLifecycleOwner, Observer {
            if (it != null)
                binding.newPasswordRepeatTextField.error = it
        })

        viewModel.currentPasswordFieldError.observe(viewLifecycleOwner, Observer {
            if (it != null)
                binding.currentPasswordTextField.error = it
        })

        viewModel.getDataChangeSuccessful().observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().navigate(ChangeDataFragmentDirections.actionChangeDataFragmentToUserAccountFragment(getString(R.string.fragment_user_account_data_changed_message)))
            }
        })
    }

    private fun observeEditTexts() {
        binding.emailTextField.editText?.doAfterTextChanged { binding.emailTextField.error = null }
        binding.newPasswordTextField.editText?.doAfterTextChanged { binding.newPasswordTextField.error = null }
        binding.newPasswordRepeatTextField.editText?.doAfterTextChanged { binding.newPasswordRepeatTextField.error = null }
        binding.currentPasswordTextField.editText?.doAfterTextChanged { binding.currentPasswordTextField.error = null }
    }
}