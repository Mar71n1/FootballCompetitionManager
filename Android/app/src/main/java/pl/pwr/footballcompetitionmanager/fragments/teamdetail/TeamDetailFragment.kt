package pl.pwr.footballcompetitionmanager.fragments.teamdetail

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.adapters.*
import pl.pwr.footballcompetitionmanager.databinding.FragmentTeamDetailBinding
import pl.pwr.footballcompetitionmanager.model.RequestStatus
import pl.pwr.footballcompetitionmanager.model.User
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber
import org.threeten.bp.format.DateTimeFormatter

class TeamDetailFragment : Fragment() {

    private lateinit var args: TeamDetailFragmentArgs
    private lateinit var binding: FragmentTeamDetailBinding
    private lateinit var viewModelFactory: TeamDetailViewModelFactory
    private lateinit var viewModel: TeamDetailViewModel

    private lateinit var matchAdapter: MatchAdapter
    private lateinit var resultAdapter: ResultAdapter
    private lateinit var competitionAdapter: CompetitionAdapter
    private lateinit var playerAdapter: PlayerAdapter
    private lateinit var requestUserAdapter: RequestUserAdapter

    private var isSnackbarArgsMessageUsed = false
    private var selectedTabPosition: Int = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.d("onAttach finished")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        args = TeamDetailFragmentArgs.fromBundle(requireArguments())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_team_detail, container, false)
        viewModelFactory = TeamDetailViewModelFactory(RemoteRepository(), args.teamId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TeamDetailViewModel::class.java)
        binding.viewModel = viewModel

        observeViewModel()
        setupRefreshListener()
        setPlanFriendlyListener()

        Timber.d("onCreateView finished")
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.d("onActivityCreated finished")
    }

    override fun onStart() {
        super.onStart()
        Timber.d("onStart finished")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("Selected tab = $selectedTabPosition")
        binding.tabs.getTabAt(selectedTabPosition)!!.select()
        if (args.snackbarMessage != "" && !isSnackbarArgsMessageUsed) {
            showSnackbar(args.snackbarMessage)
            isSnackbarArgsMessageUsed = true
        }
        Timber.d("onResume finished")
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause finished")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop finished")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("onDestroyView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy finished")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.d("onDetach finished")
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (!it) {
                binding.loadingIcon.visibility = View.GONE
                binding.swipeRefreshLayout.visibility = View.VISIBLE

                if (!viewModel.isCurrentUserOwner() && viewModel.currentUserOwnedTeams.value!!.isNotEmpty())
                    binding.planFriendlyMatchButton.visibility = View.VISIBLE
            }
        })

        viewModel.teamReady.observe(viewLifecycleOwner, Observer {
            if (it) {
                setupTabLayout()
                createAdapters()
            }
        })

        viewModel.team.observe(viewLifecycleOwner, Observer {
            if (it == null) findNavController().navigate(TeamDetailFragmentDirections.actionTeamDetailFragmentToHomeFragment(getString(R.string.fragment_home_team_deleted_snackbar_message)))
            else {
                binding.toolbar.title = it.name
                binding.toolbar.subtitle = String.format(getString(R.string.fragment_team_detail_creation_date_label), it.creationDate.format(DateTimeFormatter.ofPattern(getString(R.string.date_format))))
            }
        })

        viewModel.currentUserStatus.observe(viewLifecycleOwner, Observer {
            setupToolbar()
        })

        viewModel.matches.observe(viewLifecycleOwner, Observer {
            matchAdapter.matches = it
            if (binding.tabs.selectedTabPosition == 0)
                tabDataRefreshed()
        })

        viewModel.results.observe(viewLifecycleOwner, Observer {
            resultAdapter.matches = it
            if (binding.tabs.selectedTabPosition == 1)
                tabDataRefreshed()
        })

        viewModel.competitions.observe(viewLifecycleOwner, Observer {
            competitionAdapter.competitions = it
            if (binding.tabs.selectedTabPosition == 2)
                tabDataRefreshed()
        })

        viewModel.players.observe(viewLifecycleOwner, Observer {
            playerAdapter.submitList(usersToTeamPlayers(it))
            if (binding.tabs.selectedTabPosition == 3)
                tabDataRefreshed()
        })

        viewModel.requestsUsers.observe(viewLifecycleOwner, Observer {
            requestUserAdapter.requestsUsers = it

            if (binding.tabs.selectedTabPosition == 4)
                tabDataRefreshed()

            if (it.isNotEmpty())
                binding.tabs.getTabAt(4)!!.orCreateBadge.number = requestUserAdapter.itemCount
            else
                binding.tabs.getTabAt(4)?.removeBadge()
        })

        viewModel.getSnackbarMessage().observe(viewLifecycleOwner, Observer { showSnackbar(getString(it)) })
    }

    private fun setupToolbar() {
//        if (viewModel.user.value!!.roles.contains(Role.ADMINISTRATOR)) {
//            binding.toolbar.menu.findItem(R.id.action_report_list).isVisible = true
//        }

        binding.toolbar.title = viewModel.team.value!!.name
        binding.toolbar.subtitle = String.format(getString(R.string.fragment_team_detail_creation_date_label), viewModel.team.value!!.creationDate.format(DateTimeFormatter.ofPattern("d.MM.y")))

        if (viewModel.currentUserStatus.value == RequestStatus.REJECTED || viewModel.currentUserStatus.value == null) showUserAction(R.id.action_send_request)
        else if (viewModel.currentUserStatus.value == RequestStatus.PENDING) showUserAction(R.id.action_cancel_request)
        else if (viewModel.currentUserStatus.value == RequestStatus.ACCEPTED && !viewModel.isCurrentUserOwner()) showUserAction(R.id.action_leave_team)

        if (viewModel.isCurrentUserOwner() || viewModel.isCurrentUserAdmin()) binding.toolbar.menu.findItem(R.id.action_settings).isVisible = true

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_send_request -> {
                    viewModel.sendRequestToJoinTeam()
                    true
                }
                R.id.action_cancel_request -> {
                    viewModel.cancelRequest()
                    true
                }
                R.id.action_leave_team -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.fragment_team_detail_leave_team_dialog)
                        .setNegativeButton("Nie") { _, _ -> Unit }
                        .setPositiveButton("Tak") { _, _ ->
                            viewModel.leaveTeam()
                        }
                        .show()
                    true
                }
                R.id.action_change_data -> {
                    findNavController().navigate(TeamDetailFragmentDirections.actionTeamDetailFragmentToTeamUpdateFragment(viewModel.team.value!!.teamId!!))
                    true
                }
                R.id.action_delete_team -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.fragment_team_detail_menu_delete_dialog_message)
                        .setNegativeButton("Nie") { _, _ -> Unit }
                        .setPositiveButton("Tak") { _, _ -> viewModel.deleteTeam() }
                        .show()
                    true
                }
                else -> false
            }
        }
    }

    private fun showUserAction(menuItemId: Int) {
        if (binding.toolbar.menu.findItem(R.id.action_send_request).isVisible) binding.toolbar.menu.findItem(R.id.action_send_request).isVisible = false
        if (binding.toolbar.menu.findItem(R.id.action_cancel_request).isVisible) binding.toolbar.menu.findItem(R.id.action_cancel_request).isVisible = false
        if (binding.toolbar.menu.findItem(R.id.action_leave_team).isVisible) binding.toolbar.menu.findItem(R.id.action_leave_team).isVisible = false
        binding.toolbar.menu.findItem(menuItemId).isVisible = true
    }

    private fun setupTabLayout() {
        if (!viewModel.isCurrentUserOwner() && binding.tabs.getTabAt(4) != null)
            binding.tabs.removeTabAt(4)

        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabPosition = tab!!.position
                when (tab.position) {
                    0 -> {
                        if (!viewModel.isCurrentUserOwner() && viewModel.currentUserOwnedTeams.value!!.isNotEmpty())
                            binding.planFriendlyMatchButton.visibility = View.VISIBLE
                        tabChanged(matchAdapter, getString(R.string.fragment_team_detail_no_matches_info))
                    }
                    1 -> tabChanged(resultAdapter, getString(R.string.fragment_team_detail_no_results_info))
                    2 -> tabChanged(competitionAdapter, getString(R.string.fragment_team_detail_no_competitions_info))
                    3 -> tabChanged(playerAdapter, getString(R.string.fragment_team_detail_no_players_info))
                    4 -> tabChanged(requestUserAdapter, getString(R.string.fragment_team_detail_no_requests_info))
                    else -> Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) { }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                if (tab!!.position == 0)
                    binding.planFriendlyMatchButton.visibility = View.GONE
            }
        })
    }

    private fun <VH : RecyclerView.ViewHolder> tabChanged(adapter: Adapter<VH>, noItemsInfo: String) {
        if (adapter.itemCount == 0) {
            binding.noItemsInfoTextView.text = noItemsInfo
            if (binding.recyclerView.visibility == View.VISIBLE) {
                binding.recyclerView.visibility = View.GONE
                binding.noItemsInfoTextView.visibility = View.VISIBLE
            }
            binding.recyclerView.adapter = adapter
        } else {
            binding.recyclerView.adapter = adapter
            if (binding.noItemsInfoTextView.visibility == View.VISIBLE) {
                binding.noItemsInfoTextView.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
            binding.noItemsInfoTextView.text = noItemsInfo
        }
    }

    private fun tabDataRefreshed() {
        if (binding.recyclerView.adapter!!.itemCount == 0 && binding.recyclerView.visibility == View.VISIBLE) {
            binding.recyclerView.visibility = View.GONE
            binding.noItemsInfoTextView.visibility = View.VISIBLE
        } else if (binding.recyclerView.adapter!!.itemCount != 0 && binding.noItemsInfoTextView.visibility == View.VISIBLE) {
            binding.noItemsInfoTextView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun createAdapters() {
        matchAdapter = MatchAdapter(MatchListener {
            findNavController().navigate(TeamDetailFragmentDirections.actionTeamDetailFragmentToMatchDetailFragment(it))
        })

        binding.recyclerView.adapter = matchAdapter

        resultAdapter = ResultAdapter(ResultAdapter.ResultListener {
            findNavController().navigate(TeamDetailFragmentDirections.actionTeamDetailFragmentToMatchDetailFragment(it))
        }, true)

        competitionAdapter = CompetitionAdapter(CompetitionAdapter.CompetitionListener {
            findNavController().navigate(TeamDetailFragmentDirections.actionTeamDetailFragmentToLeagueDetailFragment(it))
        })

        playerAdapter = PlayerAdapter(PlayerAdapter.PlayerListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.fragment_team_detail_remove_user_dialog_message)
                .setNegativeButton("Nie") { _, _ -> Unit }
                .setPositiveButton("Tak") { _, _ ->
                    viewModel.removeUser(it)
                }
                .show()
        }, viewModel.isCurrentUserOwner())

        requestUserAdapter = RequestUserAdapter(RequestUserAdapter.RequestUserListener { userId: Int, requestUserAction: RequestUserAdapter.RequestUserListener.RequestAction ->
            when (requestUserAction) {
                RequestUserAdapter.RequestUserListener.RequestAction.REJECT -> viewModel.rejectPlayerRequest(userId)
                RequestUserAdapter.RequestUserListener.RequestAction.ACCEPT -> viewModel.acceptPlayerRequest(userId)
            }
        })
    }

    private fun setupRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setPlanFriendlyListener() {
        binding.planFriendlyMatchButton.setOnClickListener {
            var chosenItemPosition = 0
            var chosenTeamSite = 0
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Wybierz swoją drużynę do meczu towarzyskiego")
                .setNegativeButton("Anuluj") { _, _ -> Unit }
                .setPositiveButton("Ok") { _, _ ->
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Twoja drużyna jako")
                        .setNegativeButton("Anuluj") { _, _ -> Unit }
                        .setPositiveButton("Ok") { _, _ ->
                            when (chosenTeamSite) {
                                0 -> findNavController().navigate(TeamDetailFragmentDirections.actionTeamDetailFragmentToMatchCreateFragment(viewModel.currentUserOwnedTeams.value!![chosenItemPosition].teamId!!, viewModel.team.value!!.teamId!!))
                                1 -> findNavController().navigate(TeamDetailFragmentDirections.actionTeamDetailFragmentToMatchCreateFragment(viewModel.team.value!!.teamId!!, viewModel.currentUserOwnedTeams.value!![chosenItemPosition].teamId!!))
                            }
                        }
                        .setSingleChoiceItems(arrayOf("Drużyna domowa", "Drużyna wyjazdowa"), chosenTeamSite) { _, which -> chosenTeamSite = which }
                        .show()
                }
                .setSingleChoiceItems(viewModel.currentUserOwnedTeams.value!!.map { it.name }.toTypedArray(), chosenItemPosition) { _, which -> chosenItemPosition = which }
                .show()
        }
    }

    private fun showSnackbar(message: String) = Snackbar.make(binding.swipeRefreshLayout, message, Snackbar.LENGTH_SHORT).show()

    private fun usersToTeamPlayers(users: List<User>): List<PlayerAdapter.TeamPlayer> {
        return users.map { PlayerAdapter.TeamPlayer(it, it.userId == viewModel.team.value!!.ownerId) }
    }
}