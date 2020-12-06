package pl.pwr.footballcompetitionmanager.fragments.useraccount

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.FragmentUserAccountBinding
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber

class UserAccountFragment : Fragment() {

    private lateinit var binding: FragmentUserAccountBinding
    private lateinit var viewModelFactory: UserAccountViewModelFactory
    private lateinit var viewModel: UserAccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_account, container, false)
        viewModelFactory = UserAccountViewModelFactory(RemoteRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(UserAccountViewModel::class.java)
        binding.viewModel = viewModel

        binding.changeDataButton.setOnClickListener {
            findNavController().navigate(UserAccountFragmentDirections.actionUserAccountFragmentToChangeDataFragment())
        }

        Timber.d("onCreateView finished")
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val args = UserAccountFragmentArgs.fromBundle(requireArguments())
        if (args.snackbarText != "")
            Snackbar.make(binding.mainLinearLayout, args.snackbarText, Snackbar.LENGTH_SHORT).show()
    }
}