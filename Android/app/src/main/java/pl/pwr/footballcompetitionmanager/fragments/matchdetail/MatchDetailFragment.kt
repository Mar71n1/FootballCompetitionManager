package pl.pwr.footballcompetitionmanager.fragments.matchdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.FragmentMatchDetailBinding
import pl.pwr.footballcompetitionmanager.model.MatchStatus
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber
import org.threeten.bp.LocalDateTime


class MatchDetailFragment : Fragment() {

    private lateinit var binding: FragmentMatchDetailBinding
    private lateinit var viewModelFactory: MatchDetailViewModelFactory
    private lateinit var viewModel: MatchDetailViewModel

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val args = MatchDetailFragmentArgs.fromBundle(requireArguments())

        binding = FragmentMatchDetailBinding.inflate(inflater, container, false)
        viewModelFactory = MatchDetailViewModelFactory(RemoteRepository(), args.matchId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MatchDetailViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        observeViewModel()
        setInputScoreLayoutButtonsListeners()

        Timber.d("onCreateView finished")
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (!it) {
                setupSettingsMenu()

                val matchInfo = viewModel.getMatchInfo()
                if (matchInfo != null) binding.matchInfoTextView.text = matchInfo
                else binding.infoLinearLayout.visibility = View.GONE

                val match = viewModel.match.value!!

                if (LocalDateTime.now().isAfter(match.time.plusMinutes(match.length.toLong())) && viewModel.canCurrentUserSetScore()) {
                    binding.buttonsLayout.visibility = View.VISIBLE

                    if (match.competitionId != null && match.matchStatus == MatchStatus.PLANNED) {
                        // Mecz w rozgrywkach
                        setupCompetitionOwnerInputScoreButton()
                    } else if (match.competitionId == null && match.matchStatus == MatchStatus.PLANNED) {
                        // Mecz towarzyski bez propozycji
                        setupNoProposalFriendlyMatchScoreButtons()
                    } else if (match.competitionId == null && (match.matchStatus == MatchStatus.SCORE_PROPOSED_HOME_TEAM_OWNER || match.matchStatus == MatchStatus.SCORE_PROPOSED_AWAY_TEAM_OWNER)) {
                        // Mecz towarzyski pierwsza propozycja
                        setupFirstProposalFriendlyMatchScoreButtons()
                    } else if (match.competitionId == null && (match.matchStatus == MatchStatus.SCORE_PROPOSED_2ND_TIME_HOME_TEAM_OWNER || match.matchStatus == MatchStatus.SCORE_PROPOSED_2ND_TIME_AWAY_TEAM_OWNER)) {
                        // Mecz towarzyski druga propozycja
                        setupSecondProposalFriendlyMatchScoreButtons()
                    }
                } else if ((match.matchStatus == MatchStatus.PROPOSED_HOME_TEAM_OWNER && viewModel.isCurrentUserAwayTeamOwner())
                    || (match.matchStatus == MatchStatus.PROPOSED_AWAY_TEAM_OWNER && viewModel.isCurrentUserHomeTeamOwner())) {
                    binding.buttonsLayout.visibility = View.VISIBLE
                    setupAcceptRejectMatchProposal()
                } else {
                    binding.buttonsLayout.visibility = View.GONE
                }

                binding.loadingIcon.visibility = View.GONE
                binding.mainLinearLayout.visibility = View.VISIBLE
            } else {
                binding.mainLinearLayout.visibility = View.GONE
                binding.loadingIcon.visibility = View.VISIBLE
            }
        })

        viewModel.match.observe(viewLifecycleOwner, Observer {
            with (it) {
                if (competitionName == null) {
                    binding.competitionNameButton.text = getString(R.string.friendly_match)
                    binding.competitionNameButton.isClickable = false
                } else binding.competitionNameButton.text = it.competitionName

                if (homeTeamGoals != null && awayTeamGoals != null) binding.scoreTextView.text = String.format(getString(R.string.score_format), homeTeamGoals, awayTeamGoals)
                else binding.scoreTextView.text = getString(R.string.versus)

                if (latitude != null && longitude != null) {
                    Timber.d("Ustawianie mapy")
                    binding.matchLocationMapLabel.visibility = View.VISIBLE
                    binding.googleMapFrame.visibility = View.VISIBLE
                    setupGoogleMap(latitude!!, longitude!!)
                }
            }
        })

        viewModel.getSnackbarMessage().observe(viewLifecycleOwner, Observer { showSnackbar(getString(it)) })

        viewModel.getMatchDeleted().observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().navigate(MatchDetailFragmentDirections.actionMatchDetailFragmentToHomeFragment(getString(R.string.fragment_home_match_deleted_snackbar_message)))
            }
        })
    }

    private fun setupGoogleMap(latitude: Double, longitude: Double) {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            val location = LatLng(latitude, longitude)
            googleMap.addMarker(MarkerOptions().position(location).title("Miejsce rozegrania meczu"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        }
    }

    private fun setInputScoreLayoutButtonsListeners() {
        binding.cancelButton.setOnClickListener { binding.inputScoreLayout.visibility = View.GONE }
        binding.confirmButton.setOnClickListener {
            binding.inputScoreLayout.visibility = View.GONE
            viewModel.setMatchScore(binding.homeTeamGoalsTextField.editText!!.text.toString().toInt(), binding.awayTeamGoalsTextField.editText!!.text.toString().toInt())
        }
    }

    private fun setupCompetitionOwnerInputScoreButton() {
        binding.buttonOne.text = getString(R.string.fragment_match_detail_set_score_button_text)
        binding.buttonOne.setOnClickListener { binding.inputScoreLayout.visibility = View.VISIBLE }
        binding.buttonTwo.visibility = View.GONE
    }

    private fun setupNoProposalFriendlyMatchScoreButtons() {
        binding.buttonOne.text = getString(R.string.fragment_match_detail_set_score_button_text)
        binding.buttonOne.setOnClickListener { binding.inputScoreLayout.visibility = View.VISIBLE }
        binding.buttonTwo.visibility = View.GONE
    }

    private fun setupFirstProposalFriendlyMatchScoreButtons() {
        binding.buttonOne.text = "Odrzuć"
        binding.buttonOne.setOnClickListener { binding.inputScoreLayout.visibility = View.VISIBLE }

        binding.buttonTwo.text = "Akceptuj"
        binding.buttonTwo.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Czy na pewno chcesz zaakceptować wynik tego meczu?")
                .setNegativeButton("Nie") { _, _ -> Unit }
                .setPositiveButton("Tak") { _, _ -> viewModel.acceptMatchScore() }
                .show()
        }
        binding.buttonTwo.visibility = View.VISIBLE
    }

    private fun setupSecondProposalFriendlyMatchScoreButtons() {
        binding.buttonOne.text = "Odrzuć"
        binding.buttonOne.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Czy na pewno chcesz odrzucić wynik meczu? Zostanie on ustawiony jako nieokreślony.")
                .setNegativeButton("Nie") { _, _ -> Unit }
                .setPositiveButton("Tak") { _, _ ->
                    Timber.d("Odrzucanie")
                    viewModel.rejectMatchScore() }
                .show()
        }

        binding.buttonTwo.text = "Akceptuj"
        binding.buttonTwo.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Czy na pewno chcesz zaakceptować wynik tego meczu?")
                .setNegativeButton("Nie") { _, _ -> Unit }
                .setPositiveButton("Tak") { _, _ ->
                    Timber.d("Akceptowanie")
                    viewModel.acceptMatchScore() }
                .show()
        }
        binding.buttonTwo.visibility = View.VISIBLE
    }

    private fun setupAcceptRejectMatchProposal() {
        binding.buttonOne.text = "Odrzuć"
        binding.buttonOne.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Czy na pewno chcesz odrzucić propozycję meczu?")
                .setNegativeButton("Nie") { _, _ -> Unit }
                .setPositiveButton("Tak") { _, _ -> viewModel.rejectMatchProposal() }
                .show()
        }

        binding.buttonTwo.text = "Akceptuj"
        binding.buttonTwo.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("Czy na pewno chcesz zaakceptować propozycję meczu?")
                .setNegativeButton("Nie") { _, _ -> Unit }
                .setPositiveButton("Tak") { _, _ -> viewModel.acceptMatchProposal() }
                .show()
        }
        binding.buttonTwo.visibility = View.VISIBLE
    }

    private fun setupSettingsMenu() {
        val match = viewModel.match.value!!

        if (viewModel.isCurrentUserAdmin()) { /*|| (viewModel.isCurrentUserCompetitionOwner() && match.matchStatus == MatchStatus.PLANNED && LocalDateTime.now().isAfter(match.time))*/
            binding.settingsButton.setOnClickListener {
                val popupMenu = PopupMenu(requireContext(), it)
                popupMenu.menuInflater.inflate(R.menu.match_menu, popupMenu.menu)
                popupMenu.show()

                if (LocalDateTime.now().isAfter(match.time.plusMinutes(match.length.toLong())))
                    popupMenu.menu.findItem(R.id.action_set_match_score).isVisible = true

                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_set_match_score -> {
                            binding.inputScoreLayout.visibility = View.VISIBLE
                            true
                        }
                        R.id.action_change_data -> {
                            findNavController().navigate(MatchDetailFragmentDirections.actionMatchDetailFragmentToMatchUpdateFragment(viewModel.match.value!!.matchId!!))
                            true
                        }
                        R.id.action_delete_match -> {
                            MaterialAlertDialogBuilder(requireContext())
                                .setMessage("Czy na pewno chcesz usunąć ten mecz?")
                                .setNegativeButton("Nie") { _, _ -> Unit }
                                .setPositiveButton("Tak") { _, _ -> viewModel.deleteMatch() }
                                .show()
                            true
                        }
                        else -> {
                            showSnackbar("Error")
                            false
                        }
                    }
                }
            }

            binding.settingsButton.visibility = View.VISIBLE
        }
    }

    private fun showSnackbar(message: String) = Snackbar.make(binding.mainLinearLayout, message, Snackbar.LENGTH_SHORT).show()
}