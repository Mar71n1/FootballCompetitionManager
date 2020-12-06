package pl.pwr.footballcompetitionmanager.fragments.register

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
import pl.pwr.footballcompetitionmanager.Constants
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.FragmentRegisterBinding
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModelFactory: RegisterViewModelFactory
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_register, container, false)
        viewModelFactory = RegisterViewModelFactory(RemoteRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(RegisterViewModel::class.java)

        binding.viewModel = viewModel

        observeViewModel()
        observeEditTexts()

        Timber.d("onCreateView finished")
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.emailFieldError.observe(viewLifecycleOwner, Observer { if (it != null) binding.emailTextField.error = getString(it) else binding.emailTextField.error = null })
        viewModel.usernameFieldError.observe(viewLifecycleOwner, Observer {
            if (it != null) binding.usernameTextField.error = String.format(getString(it), Constants.MIN_USERNAME_LENGTH, Constants.MAX_USERNAME_LENGTH)
            else binding.usernameTextField.error = null
        })
        viewModel.passwordFieldError.observe(viewLifecycleOwner, Observer { if (it != null) binding.passwordTextField.error = it else binding.passwordTextField.error = null })
        viewModel.confirmPasswordFieldError.observe(viewLifecycleOwner, Observer { if (it != null) binding.confirmPasswordTextField.error = it else binding.confirmPasswordTextField.error = null })

        viewModel.registerInProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.linearLayout.visibility = View.GONE
                binding.registerProgressBar.visibility = View.VISIBLE
            } else {
                binding.registerProgressBar.visibility = View.GONE
                binding.linearLayout.visibility = View.VISIBLE
            }
        })

        viewModel.registerSuccessful.observe(viewLifecycleOwner, Observer {
            if (it) {
                Snackbar.make(binding.linearLayout, R.string.fragment_register_register_success, Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(binding.usernameTextField.editText?.text.toString()))
                viewModel.navigationToLoginScreenFinished()
            }
        })

        viewModel.registerError.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.emailTextField.error = " "
                binding.usernameTextField.error = " "
                Snackbar.make(binding.linearLayout, R.string.fragment_register_register_error_message, Snackbar.LENGTH_LONG).show()
            }
        })
    }

    private fun observeEditTexts() {
        binding.emailTextField.editText?.doAfterTextChanged { viewModel.clearEmailTextFieldError() }
        binding.usernameTextField.editText?.doAfterTextChanged { viewModel.clearUsernameTextFieldError() }
        binding.passwordTextField.editText?.doAfterTextChanged { viewModel.clearPasswordTextFieldError() }
        binding.confirmPasswordTextField.editText?.doAfterTextChanged { viewModel.clearConfirmPasswordTextFieldError() }
    }
}