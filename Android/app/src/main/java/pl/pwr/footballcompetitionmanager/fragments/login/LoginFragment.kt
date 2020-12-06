package pl.pwr.footballcompetitionmanager.fragments.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.FragmentLoginBinding
import pl.pwr.footballcompetitionmanager.fragments.home.HomeFragmentDirections
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber
import kotlin.system.exitProcess

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModelFactory: LoginViewModelFactory
    private lateinit var viewModel: LoginViewModel

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            //showExitDialog()
            requireActivity().moveTaskToBack(true)
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModelFactory = LoginViewModelFactory(RemoteRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

        binding.viewModel = viewModel
        val newUsername = LoginFragmentArgs.fromBundle(requireArguments()).username
        binding.usernameTextField.editText?.setText(newUsername)

        observeViewModel()
        observeEditTexts()

        Timber.d("onCreateView finished")
        return binding.root
    }

    override fun onDestroyView() {
        onBackPressedCallback.remove()
        super.onDestroyView()
    }

    private fun observeViewModel() {
        viewModel.usernameFieldError.observe(viewLifecycleOwner, Observer { if (it != null) binding.usernameTextField.error = getString(it) else binding.usernameTextField.error = null })
        viewModel.passwordFieldError.observe(viewLifecycleOwner, Observer { if (it != null) binding.passwordTextField.error = it else binding.passwordTextField.error = null })

        viewModel.loginInProgress.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.linearLayout.visibility = View.GONE
                binding.loginProgressBar.visibility = View.VISIBLE
            } else {
                binding.loginProgressBar.visibility = View.GONE
                binding.linearLayout.visibility = View.VISIBLE
            }
        })

        viewModel.loginSuccessful.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHomeFragment())
                viewModel.loginFinished()
            }
        })

        viewModel.loginError.observe(viewLifecycleOwner, Observer {
            binding.usernameTextField.error = " "
            binding.passwordTextField.error = " "
            Snackbar.make(binding.linearLayout, getString(it), Snackbar.LENGTH_LONG).show()
//            if (it) {
//                binding.usernameTextField.error = " "
//                binding.passwordTextField.error = " "
//                Snackbar.make(binding.linearLayout, R.string.fragment_login_login_error_message, Snackbar.LENGTH_LONG).show()
//            } else {
//                binding.usernameTextField.error = null
//                binding.passwordTextField.error = null
//            }
        })

        viewModel.navigateToRegister.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
                viewModel.navigationToRegisterScreenFinished()
            }
        })
    }

    private fun observeEditTexts() {
        binding.usernameTextField.editText?.doAfterTextChanged { viewModel.clearTextFieldErrors() }
        binding.passwordTextField.editText?.doAfterTextChanged { viewModel.clearTextFieldErrors() }
    }

    private fun showExitDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Wyjść z aplikacji?")
                .setMessage("Ta akcja wyłączy aplikację.")
                .setNegativeButton("Nie") { _, _ -> Unit }
                .setPositiveButton("Tak") { _, _ -> exitProcess(0) }
                .show()
        }
    }
}