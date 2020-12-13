package pl.pwr.footballcompetitionmanager.fragments.home

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.adapters.CompetitionAdapter
import pl.pwr.footballcompetitionmanager.adapters.MatchAdapter
import pl.pwr.footballcompetitionmanager.adapters.MatchListener
import pl.pwr.footballcompetitionmanager.adapters.TeamAdapter
import pl.pwr.footballcompetitionmanager.databinding.FragmentHomeBinding
import pl.pwr.footballcompetitionmanager.model.Role
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber

class HomeFragment : Fragment() {
    private lateinit var args: HomeFragmentArgs
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModelFactory: HomeViewModelFactory
    private lateinit var viewModel: HomeViewModel

    private lateinit var matchAdapter: MatchAdapter
    private lateinit var teamAdapter: TeamAdapter
    private lateinit var competitionAdapter: CompetitionAdapter

    private lateinit var onBackPressedCallback: OnBackPressedCallback

    private var selectedTabId: Int = R.id.matches_action
    private var isSnackbarArgsMessageUsed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        onBackPressedCallback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) { showLogoutDialog() }

        args = HomeFragmentArgs.fromBundle(requireArguments())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        viewModelFactory = HomeViewModelFactory(RemoteRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
        binding.viewModel = viewModel

        setupToolbar()
        createAdapters()
        setRecyclerViewAdapter()
        observeViewModel()
        setupBottomNavigation()
        setupRefreshListener()

        Timber.d("onCreateView finished")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
        Timber.d("Selected tab = $selectedTabId")
        binding.bottomNavigation.selectedItemId = selectedTabId
        when (selectedTabId) {
            R.id.teams_action -> {
                binding.extendedFab.text = getString(R.string.fragment_home_fab_add_team_label)
                binding.extendedFab.visibility = View.VISIBLE
            }
            R.id.competitions_action -> {
                binding.extendedFab.text = getString(R.string.fragment_home_fab_add_competition_label)
                binding.extendedFab.visibility = View.VISIBLE
            }
        }
        if (args.snackbarText != "" && !isSnackbarArgsMessageUsed) {
            showSnackbar(args.snackbarText)
            isSnackbarArgsMessageUsed = true
        }
    }

    override fun onPause() {
        super.onPause()
        selectedTabId = binding.bottomNavigation.selectedItemId
        Timber.d("onPause finished, selectedTabPosition = $selectedTabId")
    }

    override fun onDestroyView() {
        onBackPressedCallback.remove()
        super.onDestroyView()
    }

    private fun setupToolbar() {
        if (viewModel.user.value!!.roles.contains(Role.ADMINISTRATOR)) {
            binding.toolbar.menu.findItem(R.id.action_report_list).isVisible = true
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search_action -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment())
                    true
                }
                R.id.action_account_settings -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUserAccountFragment())
                    true
                }
                R.id.action_report_bug -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCreateReportFragment())
                    true
                }
                R.id.action_report_list -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToReportListFragment())
                    true
                }
                R.id.action_logout -> {
                    showLogoutDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (!it) {
                binding.loadingIcon.visibility = View.GONE
                binding.mainConstraintLayout.visibility = View.VISIBLE
            }
        })

        viewModel.matches.observe(viewLifecycleOwner, Observer {
            matchAdapter.matches = it
            if (binding.bottomNavigation.selectedItemId == R.id.matches_action)
                showActualContent(matchAdapter, getString(R.string.fragment_home_no_matches_info))
        })

        viewModel.teams.observe(viewLifecycleOwner, Observer {
            teamAdapter.teams = it
            if (binding.bottomNavigation.selectedItemId == R.id.teams_action)
                showActualContent(teamAdapter, getString(R.string.fragment_home_no_teams_info))
        })

        viewModel.competitions.observe(viewLifecycleOwner, Observer {
            competitionAdapter.competitions = it
            if (binding.bottomNavigation.selectedItemId == R.id.competitions_action)
                showActualContent(competitionAdapter, getString(R.string.fragment_home_no_competitions_info))
        })

        viewModel.navigateToCreate.observe(viewLifecycleOwner, Observer {
            if (it) {
                when (binding.bottomNavigation.selectedItemId) {
                    R.id.teams_action -> findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTeamCreateFragment())
                    R.id.competitions_action -> findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLeagueCreateFragment())
                    else -> Toast.makeText(context, "FAIL", Toast.LENGTH_LONG).show()
                }
                viewModel.navigatedToCreate()
            }
        })

        viewModel.getSnackbarMessage().observe(viewLifecycleOwner, Observer {
            if (it != null)
                showSnackbar(getString(it))
        })
    }

    private fun createAdapters() {
        matchAdapter = MatchAdapter(MatchListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToMatchDetailFragment(it))
        })

        teamAdapter = TeamAdapter(TeamAdapter.TeamListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToTeamDetailFragment(it.teamId!!))
        }, viewModel.user.value!!.userId)

        competitionAdapter = CompetitionAdapter(CompetitionAdapter.CompetitionListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLeagueDetailFragment(it))
        })
    }

    private fun setRecyclerViewAdapter() {
        when (binding.bottomNavigation.selectedItemId) {
            R.id.matches_action -> {
                binding.recyclerView.adapter = matchAdapter
            }
            R.id.teams_action -> {
                binding.recyclerView.adapter = teamAdapter
            }
            R.id.competitions_action -> {
                binding.recyclerView.adapter = competitionAdapter
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item -> bottomNavigationItemSelectedListener(item) }
        binding.bottomNavigation.setOnNavigationItemReselectedListener { item -> bottomNavigationItemReselectedListener(item) }
    }

    private fun bottomNavigationItemSelectedListener(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.matches_action -> {
                Timber.d("Mecze")
                binding.extendedFab.visibility = View.GONE
                showActualContent(matchAdapter, getString(R.string.fragment_home_no_matches_info))
                true
            }
            R.id.teams_action -> {
                Timber.d("Drużyny")
                binding.extendedFab.text = getString(R.string.fragment_home_fab_add_team_label)
                binding.extendedFab.visibility = View.VISIBLE
                showActualContent(teamAdapter, getString(R.string.fragment_home_no_teams_info))
                true
            }
            R.id.competitions_action -> {
                Timber.d("Rozgrywki")
                binding.extendedFab.text = getString(R.string.fragment_home_fab_add_competition_label)
                binding.extendedFab.visibility = View.VISIBLE
                showActualContent(competitionAdapter, getString(R.string.fragment_home_no_competitions_info))
                true
            }
            else -> false
        }
    }

    private fun <VH : RecyclerView.ViewHolder> showActualContent(adapter: RecyclerView.Adapter<VH>, noItemsInfo: String) {
        if (adapter.itemCount == 0) {
            binding.noItemsInfoTextView.text = noItemsInfo
            binding.recyclerView.visibility = View.GONE
            binding.noItemsInfoTextView.visibility = View.VISIBLE
        } else {
            binding.recyclerView.adapter = adapter
            binding.noItemsInfoTextView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun bottomNavigationItemReselectedListener(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.matches_action -> {
                true
            }
            R.id.teams_action -> {
                true
            }
            R.id.competitions_action -> {
                true
            }
//            R.id.notification_action -> {
//                true
//            }
            else -> false
        }
    }

    private fun showLogoutDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Wylogować?")
                .setMessage("Ta akcja wyloguje Cię z aplikacji. Będziesz musiał zalogować się ponownie.")
                .setNegativeButton("Nie") { _, _ -> Unit }
                .setPositiveButton("Tak") { _, _ ->
                    viewModel.logout()
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment(viewModel.user.value!!.username))
                    Snackbar.make(binding.coordinatorLayout, "Wylogowano", Snackbar.LENGTH_SHORT).show()
                }
                .show()
        }
    }

    private fun setupRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun showSnackbar(message: String) = Snackbar.make(binding.swipeRefreshLayout, message, Snackbar.LENGTH_SHORT).show()
}