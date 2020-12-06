package pl.pwr.footballcompetitionmanager.fragments.matchupdate

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.FragmentMatchUpdateBinding
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class MatchUpdateFragment : Fragment() {

    private lateinit var args: MatchUpdateFragmentArgs
    private lateinit var binding: FragmentMatchUpdateBinding
    private lateinit var viewModelFactory: MatchUpdateViewModelFactory
    private lateinit var viewModel: MatchUpdateViewModel

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        args = MatchUpdateFragmentArgs.fromBundle(requireArguments())
        binding = FragmentMatchUpdateBinding.inflate(inflater)
        viewModelFactory = MatchUpdateViewModelFactory(RemoteRepository(), args.matchId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MatchUpdateViewModel::class.java)

        observeViewModel()
        setListeners()

        Timber.d("onCreateViewFinished")
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.mainLinearLayout.visibility = View.GONE
                binding.loadingIcon.visibility = View.VISIBLE
            } else {
                binding.loadingIcon.visibility = View.GONE
                binding.mainLinearLayout.visibility = View.VISIBLE
            }
        })

        viewModel.match.observe(viewLifecycleOwner, Observer {
            binding.match = it

            if (it.competitionId == null)
                binding.competitionNameTextView.text = getString(R.string.friendly_match)
            else {
                binding.competitionNameTextView.text = it.competitionName
                binding.matchLengthTextField.isEnabled = false
                binding.playersPerTeamSlider.isEnabled = false
            }

            setupGoogleMap(it.latitude, it.longitude)
        })

        viewModel.chosenDateTime.observe(viewLifecycleOwner, Observer {
            if (it != null) {
//                binding.dateTimeButton.text = it.format(DateTimeFormatter.ofPattern(getString(R.string.date_format))) + " " + it.format(DateTimeFormatter.ofPattern(getString(R.string.time_format)))
                binding.dateTimeButton.text = it.format(DateTimeFormatter.ofPattern(getString(R.string.date_time_format)))
            }
        })

        viewModel.getSnackbarMessage().observe(viewLifecycleOwner, Observer {
            Snackbar.make(binding.mainLinearLayout, getString(it), Snackbar.LENGTH_SHORT).show()
        })

        viewModel.getUpdated().observe(viewLifecycleOwner, Observer {
            findNavController().navigate(MatchUpdateFragmentDirections.actionMatchUpdateFragmentToMatchDetailFragment(it))
        })
    }

    private fun setupGoogleMap(latitude: Double?, longitude: Double?) {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it

            val location: LatLng = if (latitude == null && longitude == null)
                LatLng(51.107883, 17.038538)
            else
                LatLng(latitude!!, longitude!!)

            val marker = googleMap.addMarker(MarkerOptions().position(location).title("Miejsce rozegrania meczu"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

            googleMap.setOnCameraMoveListener {
                marker.position = googleMap.cameraPosition.target
            }
        }
    }

    private fun pickDateTime() {
        val datePicker = buildDatePicker()
        datePicker.addOnPositiveButtonClickListener {
            val dateInMillis = it
            val timePicker = buildTimePicker()
            timePicker.addOnPositiveButtonClickListener {
                viewModel.setDateTime(dateInMillis, timePicker.hour, timePicker.minute)
            }
            timePicker.show(activity?.supportFragmentManager!!, timePicker.toString())
        }
        datePicker.show(activity?.supportFragmentManager!!, datePicker.toString())
    }

    private fun buildDatePicker(): MaterialDatePicker<Long> {
        val polishLocale = Locale("pl-PL")
        Locale.setDefault(polishLocale)

        return MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.fragment_match_create_date_picker_title)
            .build()
    }

    private fun buildTimePicker(): MaterialTimePicker {
        return  MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setTitleText(R.string.fragment_match_create_time_picker_title)
            .build()
    }

    private fun setListeners() {
        binding.dateTimeButton.setOnClickListener { pickDateTime() }

        binding.locationEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.matchLocationMapLabel.visibility = View.VISIBLE
                binding.googleMapFrame.visibility = View.VISIBLE
            } else {
                binding.matchLocationMapLabel.visibility = View.GONE
                binding.googleMapFrame.visibility = View.GONE
            }
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

        binding.updateMatchButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Na pewno zaproponować zmiany?")
                .setNegativeButton("Anuluj") { _, _ -> Unit }
                .setPositiveButton("Ok") { _, _ ->
                    if (binding.locationEnabledSwitch.isChecked)
                        viewModel.updateMatch(googleMap.cameraPosition.target.latitude, googleMap.cameraPosition.target.longitude, binding.matchLengthTextField.editText!!.text.toString().toInt(), binding.playersPerTeamSlider.value.toInt())
                    else
                        viewModel.updateMatch(null, null, binding.matchLengthTextField.editText!!.text.toString().toInt(), binding.playersPerTeamSlider.value.toInt())
                }
                .show()
        }
    }
}