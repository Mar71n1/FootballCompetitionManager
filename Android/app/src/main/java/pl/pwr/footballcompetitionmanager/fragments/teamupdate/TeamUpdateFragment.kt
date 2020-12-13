package pl.pwr.footballcompetitionmanager.fragments.teamupdate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import pl.pwr.footballcompetitionmanager.Constants
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.FragmentTeamUpdateBinding
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber

class TeamUpdateFragment : Fragment() {

    private lateinit var binding: FragmentTeamUpdateBinding
    private lateinit var viewModelFactory: TeamUpdateViewModelFactory
    private lateinit var viewModel: TeamUpdateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTeamUpdateBinding.inflate(inflater)
        viewModelFactory = TeamUpdateViewModelFactory(RemoteRepository(), TeamUpdateFragmentArgs.fromBundle(requireArguments()).teamId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TeamUpdateViewModel::class.java)
        binding.viewModel = viewModel

        observeViewModel()
        observeEditTexts()

        Timber.d("onCreateView finished")
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.team.observe(viewLifecycleOwner, Observer {
            binding.team = it
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

        viewModel.updated.observe(viewLifecycleOwner, Observer {
            if (it)
                findNavController().navigate(TeamUpdateFragmentDirections.actionTeamUpdateFragmentToTeamDetailFragment(viewModel.team.value!!.teamId!!, getString(R.string.fragment_team_detail_team_updated_snackbar_message)))
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