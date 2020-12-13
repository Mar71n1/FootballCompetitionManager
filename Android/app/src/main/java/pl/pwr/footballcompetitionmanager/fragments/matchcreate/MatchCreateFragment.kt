package pl.pwr.footballcompetitionmanager.fragments.matchcreate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
import pl.pwr.footballcompetitionmanager.databinding.FragmentMatchCreateBinding
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

class MatchCreateFragment : Fragment() {

    private lateinit var args: MatchCreateFragmentArgs
    private lateinit var binding: FragmentMatchCreateBinding
    private lateinit var viewModelFactory: MatchCreateViewModelFactory
    private lateinit var viewModel: MatchCreateViewModel

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        args = MatchCreateFragmentArgs.fromBundle(requireArguments())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_match_create, container, false)
        viewModelFactory = MatchCreateViewModelFactory(RemoteRepository(), args.homeTeamId, args.awayTeamId, args.competitionId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MatchCreateViewModel::class.java)
        binding.viewModel = viewModel

        setupGoogleMap()
        setupSliderListener()
        observeEditTexts()

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

        binding.planMatchButton.setOnClickListener {
            buttonClicked()
        }

        observeViewModel()

        Timber.d("onCreateView finished")
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.homeTeam.observe(viewLifecycleOwner, Observer {
            if (it != null) binding.homeTeamNameTextView.text = it.name
        })

        viewModel.awayTeam.observe(viewLifecycleOwner, Observer {
            if (it != null) binding.awayTeamNameTextView.text = it.name
        })

        viewModel.competition.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                with (binding) {
                    competitionNameTextView.text = it.name
                    matchLengthTextField.isEnabled = false
                    matchLengthTextField.editText!!.setText(it.matchLength.toString())
                    binding.playersPerTeamSlider.isEnabled = false
                    binding.playersPerTeamSlider.value = it.playersPerTeam.toFloat()
                    playersPerTeamTextView.text = it.playersPerTeam.toString()
                }
            } else {
                binding.competitionNameTextView.text = getString(R.string.friendly_match)
            }
        })

        viewModel.chosenDateTime.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.dateTimeButton.text = it.format(DateTimeFormatter.ofPattern("${getString(R.string.date_format)} ${getString(R.string.time_format)}"))
            }
        })

        viewModel.getSnackbarMessage().observe(viewLifecycleOwner, Observer {
            Snackbar.make(binding.mainLinearLayout, getString(it), Snackbar.LENGTH_SHORT).show()
        })

        viewModel.getNewMatchId().observe(viewLifecycleOwner, Observer {
            findNavController().navigate(MatchCreateFragmentDirections.actionMatchCreateFragmentToMatchDetailFragment(it))
        })
    }

    private fun setupGoogleMap() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it

            val location = LatLng(51.107883, 17.038538)

//            BitmapDescriptorFactory.fromResource(R.drawable.google_maps_marker_small)

//            val widthDp = 41.75f
//            val heightDp = 60f
//
//            val widthPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthDp, resources.displayMetrics).toInt()
//            val heightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightDp, resources.displayMetrics).toInt()

//            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.google_maps_marker)
//            val smallMarker = Bitmap.createScaledBitmap(bitmap, widthPx, heightPx, false)
            val marker = googleMap.addMarker(MarkerOptions().position(location).title("Miejsce rozegrania meczu"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

            googleMap.setOnCameraMoveListener {
                marker.position = googleMap.cameraPosition.target
            }
        }
    }

    private fun setupSliderListener() {
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

    private fun buttonClicked() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Potwierdź planowanie meczu")
            .setNegativeButton("Anuluj") { _, _ -> Unit }
            .setPositiveButton("Ok") { _, _ ->
                if (binding.matchLengthTextField.editText!!.text.isNullOrEmpty()) {
                    binding.matchLengthTextField.error = getString(R.string.fragment_match_create_match_length_empty)
                } else if (binding.matchLengthTextField.editText!!.text.toString().toInt() <= 0) {
                    binding.matchLengthTextField.error = getString(R.string.fragment_match_create_match_length_invalid)
                } else {
                    if (binding.locationEnabledSwitch.isChecked)
                        viewModel.createMatch(googleMap.cameraPosition.target.latitude, googleMap.cameraPosition.target.longitude, binding.matchLengthTextField.editText!!.text.toString().toInt(), binding.playersPerTeamSlider.value.toInt())
                    else
                        viewModel.createMatch(null, null, binding.matchLengthTextField.editText!!.text.toString().toInt(), binding.playersPerTeamSlider.value.toInt())
                }
            }
            .show()
    }

    private fun observeEditTexts() {
        binding.matchLengthTextField.editText?.doAfterTextChanged {
            if (it.toString().isNotEmpty())
                binding.matchLengthTextField.error = null
            else
                binding.matchLengthTextField.error = " "
        }
    }
}