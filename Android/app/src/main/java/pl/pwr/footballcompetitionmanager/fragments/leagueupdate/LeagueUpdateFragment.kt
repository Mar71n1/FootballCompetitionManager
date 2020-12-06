package pl.pwr.footballcompetitionmanager.fragments.leagueupdate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.FragmentLeagueUpdateBinding
import pl.pwr.footballcompetitionmanager.fragments.teamupdate.TeamUpdateFragmentDirections
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber

class LeagueUpdateFragment : Fragment() {

    private lateinit var args: LeagueUpdateFragmentArgs
    private lateinit var binding: FragmentLeagueUpdateBinding
    private lateinit var viewModelFactory: LeagueUpdateViewModelFactory
    private lateinit var viewModel: LeagueUpdateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        args = LeagueUpdateFragmentArgs.fromBundle(requireArguments())
        binding = FragmentLeagueUpdateBinding.inflate(inflater)
        viewModelFactory = LeagueUpdateViewModelFactory(RemoteRepository(), args.leagueSeasonId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LeagueUpdateViewModel::class.java)
        binding.viewModel = viewModel

        setupSlidersListeners()
        observeViewModel()

        Timber.d ("onCreateView finished")
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.mainLinearLayout.visibility = View.INVISIBLE
                binding.loadingIcon.visibility = View.VISIBLE
            } else {
                binding.loadingIcon.visibility = View.GONE
                binding.mainLinearLayout.visibility = View.VISIBLE
            }
        })

        viewModel.leagueSeason.observe(viewLifecycleOwner, Observer {
            binding.leagueSeason = it
            binding.numberOfTeamsSlider.value = it.competition.numberOfTeams.toFloat()
            binding.playersPerTeamSlider.value = it.competition.playersPerTeam.toFloat()
        })

        viewModel.getSnackbarMessage().observe(viewLifecycleOwner, Observer {
            Snackbar.make(binding.mainLinearLayout, getString(it), Snackbar.LENGTH_SHORT).show()
        })

        viewModel.updated.observe(viewLifecycleOwner, Observer {
            if (it)
                findNavController().navigate(LeagueUpdateFragmentDirections.actionLeagueUpdateFragmentToLeagueDetailFragment(viewModel.leagueSeason.value!!.competition.competitionId!!, getString(R.string.fragment_league_detail_league_updated_snackbar_message)))
        })
    }

    private fun setupSlidersListeners() {
        binding.numberOfTeamsSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                binding.numberOfTeamsLabel.setTextColor(ContextCompat.getColor(context!!, R.color.primaryColor))
                binding.numberOfTeamsTextView.setTextColor(ContextCompat.getColor(context!!, R.color.primaryColor))
            }

            override fun onStopTrackingTouch(slider: Slider) {
                binding.numberOfTeamsLabel.setTextColor(ContextCompat.getColor(context!!, android.R.color.tab_indicator_text))
                binding.numberOfTeamsTextView.setTextColor(ContextCompat.getColor(context!!, android.R.color.tab_indicator_text))
            }
        })

        binding.numberOfTeamsSlider.addOnChangeListener { _, value, _ ->
            binding.numberOfTeamsTextView.text = value.toInt().toString()
        }

        binding.playersPerTeamSlider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                binding.playersPerTeamLabel.setTextColor(ContextCompat.getColor(context!!, R.color.primaryColor))
                binding.playersPerTeamTextView.setTextColor(ContextCompat.getColor(context!!, R.color.primaryColor))
            }

            override fun onStopTrackingTouch(slider: Slider) {
                binding.playersPerTeamLabel.setTextColor(ContextCompat.getColor(context!!, android.R.color.tab_indicator_text))
                binding.playersPerTeamTextView.setTextColor(ContextCompat.getColor(context!!, android.R.color.tab_indicator_text))
            }
        })

        binding.playersPerTeamSlider.addOnChangeListener { _, value, _ ->
            binding.playersPerTeamTextView.text = value.toInt().toString()
        }
    }
}