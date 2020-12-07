package pl.pwr.footballcompetitionmanager.fragments.leaguecreate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.FragmentLeagueCreateBinding
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber

class LeagueCreateFragment : Fragment() {

    private lateinit var binding: FragmentLeagueCreateBinding
    private lateinit var viewModelFactory: LeagueCreateViewModelFactory
    private lateinit var viewModel: LeagueCreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_league_create, container, false)
        viewModelFactory = LeagueCreateViewModelFactory(RemoteRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(LeagueCreateViewModel::class.java)

        binding.viewModel = viewModel

        observeViewModel()
        observeEditTexts()
        setupSlidersListeners()
        setButtonListener()

        Timber.d("onCreateView finished")
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.newCompetitionId.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                findNavController().navigate(LeagueCreateFragmentDirections.actionLeagueCreateFragmentToLeagueDetailFragment(it))
                viewModel.navigatedToCompetitionDetailView()
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Snackbar.make(binding.mainLinearLayout, getString(it), Snackbar.LENGTH_SHORT).show()
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

    private fun setButtonListener() {
        binding.leagueCreateButton.setOnClickListener {
            if (binding.matchLengthTextField.editText!!.text.toString() == "") {
                binding.matchLengthTextField.error = getString(R.string.fragment_league_create_empty_match_length)
            } else if (binding.matchLengthTextField.editText!!.text.toString().toInt() <= 0) {
                binding.matchLengthTextField.error = getString(R.string.fragment_league_create_match_length_not_greater_than_zero)
            } else {
                viewModel.createNewLeague(binding.leagueNameTextField.editText!!.text.toString(), binding.numberOfTeamsSlider.value.toInt(), binding.doubleMatchEnabledSwitch.isChecked, Integer.parseInt(binding.matchLengthTextField.editText!!.text.toString()), binding.playersPerTeamSlider.value.toInt(), binding.descriptionTextField.editText!!.text.toString())
            }
        }
    }

    private fun observeEditTexts() {
//        binding.leagueNameTextField.editText?.doAfterTextChanged {
//            Toast.makeText(context, binding.leagueNameTextField.counterMaxLength.toString(), Toast.LENGTH_SHORT).show()
////            if (it.toString().length > 20)
////                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
//            //viewModel.clearTextFieldErrors()
//        }
//        binding.descriptionTextField.editText?.doAfterTextChanged {
//            Toast.makeText(context, binding.descriptionTextField.counterMaxLength.toString(), Toast.LENGTH_SHORT).show()
//        }
    }
}