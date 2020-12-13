package pl.pwr.footballcompetitionmanager.fragments.teamcreate

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
import pl.pwr.footballcompetitionmanager.databinding.FragmentTeamCreateBinding
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber

class TeamCreateFragment : Fragment() {

    private lateinit var binding: FragmentTeamCreateBinding
    private lateinit var viewModelFactory: TeamCreateViewModelFactory
    private lateinit var viewModel: TeamCreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_team_create, container, false)
        viewModelFactory = TeamCreateViewModelFactory(RemoteRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(TeamCreateViewModel::class.java)
        binding.viewModel = viewModel

        observeViewModel()
        observeEditTexts()

        Timber.d("onCreateView finished")
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.newTeam.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(TeamCreateFragmentDirections.actionTeamCreateFragmentToTeamDetailFragment(it.teamId!!))
                viewModel.newTeamCreated()
            }
        })

        viewModel.teamNameTextFieldError.observe(viewLifecycleOwner, Observer {
            binding.teamNameTextField.error = String.format(getString(it), Constants.MIN_TEAM_NAME_LENGTH, Constants.MAX_TEAM_NAME_LENGTH)
        })

        viewModel.descriptionTextFieldError.observe(viewLifecycleOwner, Observer {
            binding.descriptionTextField.error = getString(it)
        })

        viewModel.getSnackbarMessage().observe(viewLifecycleOwner, Observer {
            if (it != null)
                Snackbar.make(binding.mainLinearLayout, getString(it), Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun observeEditTexts() {
        binding.teamNameTextField.editText?.doAfterTextChanged { binding.teamNameTextField.error = null }
        binding.descriptionTextField.editText?.doAfterTextChanged {
            if (it.toString().length <= Constants.MAX_TEAM_DESCRIPTION_LENGTH)
                binding.descriptionTextField.error = null
        }
    }
}