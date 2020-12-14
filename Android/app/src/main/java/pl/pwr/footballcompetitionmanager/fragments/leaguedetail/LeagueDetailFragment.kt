package pl.pwr.footballcompetitionmanager.fragments.leaguedetail

import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.adapters.*
import pl.pwr.footballcompetitionmanager.databinding.FragmentLeagueDetailBinding
import pl.pwr.footballcompetitionmanager.model.*
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber

class LeagueDetailFragment : Fragment() {

    private lateinit var args: LeagueDetailFragmentArgs
    private var isSnackbarArgsMessageUsed = false
    private lateinit var binding: FragmentLeagueDetailBinding
    private lateinit var viewModelFactory: LeagueDetailViewModelFactory
    private lateinit var viewModel: LeagueDetailViewModel

    private var matchAdapter: CompetitionMatchAdapter? = null
    private var resultAdapter: ResultAdapter? = null
    private var teamAdapter: CompetitionTeamAdapter? = null
    private var requestAdapter: RequestTeamAdapter? = null

    private var selectedTabPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        args = LeagueDetailFragmentArgs.fromBundle(requireArguments())
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_league_detail, container, false)
        viewModelFactory = LeagueDetailViewModelFactory(RemoteRepository(), args.competitionId)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LeagueDetailViewModel::class.java)
        binding.viewModel = viewModel

        observeViewModel()
        setupRefreshListener()

        Timber.d("onCreateView finished")
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Timber.d("Selected tab = $selectedTabPosition")
        binding.tabs.getTabAt(selectedTabPosition)!!.select()
        viewModel.refreshData()
        Timber.d("onResume finished")
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (!it) {
                setupToolbar()
                setupTeamMenuIcon()
                setupTable()
                binding.loadingIcon.visibility = View.GONE
                binding.swipeRefreshLayout.visibility = View.VISIBLE
                if (args.snackbarMessage != "" && !isSnackbarArgsMessageUsed) {
                    showSnackbar(args.snackbarMessage)
                    isSnackbarArgsMessageUsed = true
                }
            }
        })

        viewModel.leagueSeason.observe(viewLifecycleOwner, Observer {
            if (it == null) findNavController().navigate(LeagueDetailFragmentDirections.actionLeagueDetailFragmentToHomeFragment(getString(R.string.fragment_home_competition_deleted_snackbar_message)))
            else {
                binding.league = it

                if (viewModel.isCurrentUserOwner() || viewModel.isCurrentUserAdmin()) {
                    when (it.competition.competitionStatus) {
                        CompetitionStatus.PLANNING -> {
                            binding.startFinishCompetitionButtonBackground.visibility = View.VISIBLE
                        }
                        CompetitionStatus.IN_PROGRESS -> {
                            binding.startFinishCompetitionButton.text = getString(R.string.fragment_league_detail_finish_competition_button_label)
                            binding.startFinishCompetitionButtonBackground.visibility = View.VISIBLE
                            binding.planMatchButton.isEnabled = true
                        }
                    }
                }

                when (it.competition.competitionStatus) {
                    CompetitionStatus.PLANNING -> {
                        binding.toolbar.subtitle = getString(R.string.competition_status_planning_label)
                        binding.table.visibility = View.GONE
                        binding.legendTitleTextView.visibility = View.GONE
                        binding.legendTextView.visibility = View.GONE
                    }
                    CompetitionStatus.IN_PROGRESS -> {
                        binding.toolbar.subtitle = getString(R.string.competition_status_in_progress_label)
                    }
                    CompetitionStatus.COMPLETED -> {
                        binding.toolbar.subtitle = getString(R.string.competition_status_completed_label)
                        binding.startFinishCompetitionButtonBackground.visibility = View.GONE
                    }
                }
                setupTabLayout()
                setupStartCompetitionButton()
                setupPlanMatchButton()
                createAdapters()
            }
        })

        viewModel.matches.observe(viewLifecycleOwner, Observer {
            Timber.d("Czy adaptery są takie same? ${matchAdapter == binding.recyclerView.adapter}")
            matchAdapter!!.matches = it
            if (binding.tabs.selectedTabPosition == 1)
                tabDataRefreshed()
        })

        viewModel.results.observe(viewLifecycleOwner, Observer {
            resultAdapter!!.matches = it
            if (binding.tabs.selectedTabPosition == 2)
                tabDataRefreshed()
        })

        viewModel.teams.observe(viewLifecycleOwner, Observer {
            teamAdapter!!.teams = teamsToCompetitionTeams(it)
            if (binding.tabs.selectedTabPosition == 3)
                tabDataRefreshed()
            if (!viewModel.loading.value!!)
                setupTeamMenuIcon()
        })

        viewModel.requests.observe(viewLifecycleOwner, Observer {
            requestAdapter!!.requestsTeams = it
            if (binding.tabs.selectedTabPosition == 4)
                tabDataRefreshed()
            if (!viewModel.loading.value!!)
                setupTeamMenuIcon()

            if (it.isNotEmpty())
                binding.tabs.getTabAt(4)?.orCreateBadge?.number = requestAdapter!!.itemCount
            else
                binding.tabs.getTabAt(4)?.removeBadge()
        })

        viewModel.ownedTeams.observe(viewLifecycleOwner, Observer {
//            showToast("Pobrano drużyny, których użytkownik jest właścicielem, jest ich ${it.size}")
        })

        viewModel.table.observe(viewLifecycleOwner, Observer { setTable(it) })

        viewModel.getSnackbarMessage().observe(viewLifecycleOwner, Observer { if (it != null) showSnackbar(getString(it)) })
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

    private fun setupToolbar() {
//        val singleItems = arrayOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7", "Item 8", "Item 9", "Item 10", "Item 11", "Item 12")
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_send_request -> {
                    var chosenItemPosition = 0
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Wybierz drużynę")
                        .setNegativeButton("Anuluj") { _, _ -> Unit }
                        .setPositiveButton("Ok") { _, _ -> viewModel.sendRequestToJoin(chosenItemPosition) }
                        .setSingleChoiceItems(viewModel.ownedTeams.value!!.map { it.name }.toTypedArray(), chosenItemPosition) { _, which -> chosenItemPosition = which }
                        .show()
                    true
                }
                R.id.action_cancel_request -> {
                    viewModel.cancelRequest()
                    true
                }
                R.id.action_leave_competition -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.fragment_league_detail_leave_competition_dialog)
                        .setNegativeButton("Nie") { _, _ -> Unit }
                        .setPositiveButton("Tak") { _, _ -> viewModel.leaveCompetition() }
                        .show()
                    true
                }
                R.id.action_change_data -> {
                    findNavController().navigate(LeagueDetailFragmentDirections.actionLeagueDetailFragmentToLeagueUpdateFragment(viewModel.leagueSeason.value!!.leagueSeasonId!!))
                    true
                }
                R.id.action_delete_team -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(R.string.fragment_league_detail_menu_delete_dialog_message)
                        .setNegativeButton("Nie") { _, _ -> Unit }
                        .setPositiveButton("Tak") { _, _ -> viewModel.deleteLeague() }
                        .show()
                    true
                }
                else -> false
            }
        }

        if (viewModel.isCurrentUserOwner() || viewModel.isCurrentUserAdmin()) binding.toolbar.menu.findItem(R.id.action_settings).isVisible = true
    }

    private fun setupTeamMenuIcon() {
        if (viewModel.leagueSeason.value!!.competition.competitionStatus == CompetitionStatus.PLANNING) {
            Timber.d("${viewModel.requests.value == null}")

            when {
                viewModel.teams.value!!.map { it.ownerId }.contains(viewModel.getCurrentUserId()) -> showTeamAction(R.id.action_leave_competition)
                viewModel.requests.value!!.map { it.ownerId }.contains(viewModel.getCurrentUserId()) -> showTeamAction(R.id.action_cancel_request)
                viewModel.ownedTeams.value!!.isNotEmpty() -> showTeamAction(R.id.action_send_request)
            }
        } else
            binding.toolbar.menu.findItem(R.id.action_change_data).isVisible = false
    }

    private fun showTeamAction(menuItemId: Int) {
        if (binding.toolbar.menu.findItem(R.id.action_send_request).isVisible) binding.toolbar.menu.findItem(R.id.action_send_request).isVisible = false
        if (binding.toolbar.menu.findItem(R.id.action_cancel_request).isVisible) binding.toolbar.menu.findItem(R.id.action_cancel_request).isVisible = false
        if (binding.toolbar.menu.findItem(R.id.action_leave_competition).isVisible) binding.toolbar.menu.findItem(R.id.action_leave_competition).isVisible = false
        binding.toolbar.menu.findItem(menuItemId).isVisible = true
    }

    private fun setupTabLayout() {
        binding.tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                selectedTabPosition = tab!!.position
                when (tab.position) {
                    0 -> {
                        binding.recyclerViewLayout.visibility = View.GONE
                        binding.tableLayout.visibility = View.VISIBLE
                    }
                    1 -> {
                        tabChanged(matchAdapter!!, getString(R.string.fragment_league_detail_no_matches_info))
                        if (viewModel.isCurrentUserOwner() || viewModel.isCurrentUserAdmin()) {
                            binding.planMatchButton.visibility = View.VISIBLE
                        }
                    }
                    2 -> tabChanged(resultAdapter!!, getString(R.string.fragment_league_detail_no_results_info))
                    3 -> tabChanged(teamAdapter!!, getString(R.string.fragment_league_detail_no_teams_info))
                    4 -> tabChanged(requestAdapter!!, getString(R.string.fragment_league_detail_no_requests_info))
                    else -> Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) { }
            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        binding.tableLayout.visibility = View.GONE
                        binding.recyclerViewLayout.visibility = View.VISIBLE
                    }
                    1 -> binding.planMatchButton.visibility = View.GONE
                    2 -> Unit
                    3 -> Unit
                    4 -> Unit
                    else -> Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
        })

        if ((!viewModel.isCurrentUserOwner() || viewModel.leagueSeason.value!!.competition.competitionStatus != CompetitionStatus.PLANNING) && binding.tabs.getTabAt(4) != null)
            binding.tabs.removeTabAt(4)
    }

    private fun <VH : RecyclerView.ViewHolder> tabChanged(adapter: RecyclerView.Adapter<VH>, noItemsInfo: String) {
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

    private fun createAdapters() {
        if (matchAdapter == null) {
            matchAdapter = CompetitionMatchAdapter(MatchListener {
                findNavController().navigate(LeagueDetailFragmentDirections.actionLeagueDetailFragmentToMatchDetailFragment(it))
            })
        }


        if (resultAdapter == null) {
            resultAdapter = ResultAdapter(ResultAdapter.ResultListener {
                findNavController().navigate(LeagueDetailFragmentDirections.actionLeagueDetailFragmentToMatchDetailFragment(it))
            }, false)
        }


        if (teamAdapter == null) {
            teamAdapter = if (viewModel.leagueSeason.value!!.competition.competitionStatus == CompetitionStatus.PLANNING) {
                CompetitionTeamAdapter(CompetitionTeamAdapter.CompetitionTeamListener { teamId, action ->
                    when (action) {
                        CompetitionTeamAdapter.CompetitionTeamListener.Action.NAVIGATE -> findNavController().navigate(LeagueDetailFragmentDirections.actionLeagueDetailFragmentToTeamDetailFragment(teamId))
                        CompetitionTeamAdapter.CompetitionTeamListener.Action.REMOVE -> {
                            MaterialAlertDialogBuilder(requireContext())
                                .setMessage(R.string.fragment_league_detail_remove_team_dialog_message)
                                .setNegativeButton("Nie") { _, _ -> Unit }
                                .setPositiveButton("Tak") { _, _ -> viewModel.removeTeam(teamId) }
                                .show()
                        }
                    }
                }, viewModel.isCurrentUserOwner())
            } else {
                CompetitionTeamAdapter(CompetitionTeamAdapter.CompetitionTeamListener { teamId, action ->
                    when (action) {
                        CompetitionTeamAdapter.CompetitionTeamListener.Action.NAVIGATE -> findNavController().navigate(LeagueDetailFragmentDirections.actionLeagueDetailFragmentToTeamDetailFragment(teamId ))
                        CompetitionTeamAdapter.CompetitionTeamListener.Action.REMOVE -> Unit
                    }
                }, false)
            }
        }

        if (requestAdapter == null) {
            requestAdapter = RequestTeamAdapter(RequestTeamAdapter.RequestTeamListener { teamId, requestTeamAction ->
                when (requestTeamAction) {
                    RequestTeamAdapter.RequestTeamListener.Action.NAVIGATE -> findNavController().navigate(LeagueDetailFragmentDirections.actionLeagueDetailFragmentToTeamDetailFragment(teamId))
                    RequestTeamAdapter.RequestTeamListener.Action.ACCEPT -> {
                        if (viewModel.teams.value!!.size < viewModel.leagueSeason.value!!.competition.numberOfTeams)
                            viewModel.acceptTeamRequest(teamId)
                        else
                            showSnackbar("Nie możesz dodać więcej drużyn niż ${viewModel.leagueSeason.value!!.competition.numberOfTeams}")
                    }
                    RequestTeamAdapter.RequestTeamListener.Action.REJECT -> viewModel.rejectTeamRequest(teamId)
                }
            })
        }
    }

    private fun setupRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupTable() {
//        val tableRowsData = mutableListOf<LeagueTableRow>()
//        val namesOfTeamsWithoutResult = viewModel.teams.value!!.map { it.name }.toMutableList()
//
//        viewModel.results.value!!.forEach {
//            with (it) {
//                namesOfTeamsWithoutResult.removeIf { teamName -> teamName == homeTeamName || teamName == awayTeamName }
//
//                // Drużyna domowa
//                val homeTeamTableRow = tableRowsData.find { it.teamName == homeTeamName }
//                if (homeTeamTableRow == null) {
//                    tableRowsData.add(LeagueTableRow(
//                        homeTeamName!!,
//                        1,
//                        if (homeTeamGoals!! > awayTeamGoals!!) 1 else 0,
//                        if (homeTeamGoals!! == awayTeamGoals!!) 1 else 0,
//                        if (homeTeamGoals!! < awayTeamGoals!!) 1 else 0,
//                        homeTeamGoals!!,
//                        awayTeamGoals!!
//                    ))
//                } else {
//                    with (homeTeamTableRow) {
//                        playedMatches++
//                        if (homeTeamGoals!! > awayTeamGoals!!) wins++
//                        if (homeTeamGoals!! == awayTeamGoals!!) draws++
//                        if (homeTeamGoals!! < awayTeamGoals!!) loses++
//                        goalsFor += homeTeamGoals!!
//                        goalsAgainst += awayTeamGoals!!
//                    }
//                }
//
//                // Drużyna wyjazdowa
//                val awayTeamTableRow = tableRowsData.find { it.teamName == awayTeamName }
//                if (awayTeamTableRow == null) {
//                    tableRowsData.add(LeagueTableRow(
//                        awayTeamName!!,
//                        1,
//                        if (homeTeamGoals!! < awayTeamGoals!!) 1 else 0,
//                        if (homeTeamGoals!! == awayTeamGoals!!) 1 else 0,
//                        if (homeTeamGoals!! > awayTeamGoals!!) 1 else 0,
//                        awayTeamGoals!!,
//                        homeTeamGoals!!
//                    ))
//                } else {
//                    with (awayTeamTableRow) {
//                        playedMatches++
//                        if (homeTeamGoals!! < awayTeamGoals!!) wins++
//                        if (homeTeamGoals!! == awayTeamGoals!!) draws++
//                        if (homeTeamGoals!! > awayTeamGoals!!) loses++
//                        goalsFor += awayTeamGoals!!
//                        goalsAgainst += homeTeamGoals!!
//                    }
//                }
//            }
//        }
//
//        namesOfTeamsWithoutResult.forEach {
//            tableRowsData.add(LeagueTableRow(
//                it,
//                0,
//                0,
//                0,
//                0,
//                0,
//                0
//            ))
//        }
//
//        tableRowsData.sortByDescending { it.playedMatches }
//        tableRowsData.sortByDescending { it.goalsFor }
//        tableRowsData.sortByDescending { it.goalsFor - it.goalsAgainst }
//        tableRowsData.sortByDescending { it.wins }
//        tableRowsData.sortByDescending { it.points }
//
//        val numberOfTeams = viewModel.leagueSeason.value!!.competition.numberOfTeams
//        while (tableRowsData.size < numberOfTeams) {
//            tableRowsData.add(LeagueTableRow(
//                "Unknown",
//                0,
//                0,
//                0,
//                0,
//                0,
//                0
//            ))
//        }

//        addNewRows(tableRowsData)
    }

    private fun setTable(tableRowsData: List<LeagueTableRow>) {
        binding.table.removeViews(1, binding.table.childCount-1)

        lateinit var newTableRow: TableRow

        for ((i, tableRowData) in tableRowsData.withIndex()) {
            newTableRow = layoutInflater.inflate(R.layout.table_row_layout, null) as TableRow

            (newTableRow.getChildAt(0) as TextView).text = "${i+1}."
            (newTableRow.getChildAt(1) as TextView).text = tableRowData.teamName
            (newTableRow.getChildAt(2) as TextView).text = tableRowData.playedMatches.toString()
            (newTableRow.getChildAt(3) as TextView).text = tableRowData.wins.toString()
            (newTableRow.getChildAt(4) as TextView).text = tableRowData.draws.toString()
            (newTableRow.getChildAt(5) as TextView).text = tableRowData.loses.toString()
            (newTableRow.getChildAt(6) as TextView).text = "${tableRowData.goalsFor}:${tableRowData.goalsAgainst}"
            (newTableRow.getChildAt(7) as TextView).text = tableRowData.points.toString()

            binding.table.addView(newTableRow)
        }

/*
val tableRow = TableRow(context)
tableRow.setBackgroundResource(android.R.color.darker_gray)

val positionTextView = TextView(context)
val teamNameTextView = TextView(context)
textView = teamNameTextView
val playedMatchesTextView = TextView(context)
val winsTextView = TextView(context)
val drawsTextView = TextView(context)
val losesTextView = TextView(context)
val goalsTextView = TextView(context)
val pointsTextView = TextView(context)

//        var params = TableRow.LayoutParams(0)
//        //params.height = TableRow.LayoutParams.MATCH_PARENT
//        params.setMargins(dpToPx(1f),0,0,dpToPx(1f))
//        positionTextView.layoutParams = params
//
//        params = TableRow.LayoutParams(1)
////        params.height = TableRow.LayoutParams.WRAP_CONTENT
//        //params.width = dpToPx(120f)
//        params.setMargins(0, 0, 0, dpToPx(1f))
//        teamNameTextView.layoutParams = params
//
//        params = TableRow.LayoutParams(2)
//        //params.height = TableRow.LayoutParams.MATCH_PARENT
//        //params.width = dpToPx(40f)
//        params.setMargins(0, 0, 0, dpToPx(1f))
//        playedMatchesTextView.layoutParams = params
//
//        params = TableRow.LayoutParams(3)
////        params.height = TableRow.LayoutParams.MATCH_PARENT
//        params.setMargins(0, 0, 0, dpToPx(1f))
//        winsTextView.layoutParams = params
//
//        params = TableRow.LayoutParams(4)
////        params.height = TableRow.LayoutParams.MATCH_PARENT
//        params.setMargins(0, 0, 0, dpToPx(1f))
//        drawsTextView.layoutParams = params
//
//        params = TableRow.LayoutParams(5)
////        params.height = TableRow.LayoutParams.MATCH_PARENT
//        params.setMargins(0, 0, 0, dpToPx(1f))
//        losesTextView.layoutParams = params
//
//        params = TableRow.LayoutParams(6)
////        params.height = TableRow.LayoutParams.MATCH_PARENT
//        params.setMargins(0, 0, 0, dpToPx(1f))
//        goalsTextView.layoutParams = params
//
//        params = TableRow.LayoutParams(7)
////        params.height = TableRow.LayoutParams.MATCH_PARENT
//        params.setMargins(0, 0, dpToPx(1f), dpToPx(1f))
//        pointsTextView.layoutParams = params

//        positionTextView.setPadding(dpToPx(4f))
//        teamNameTextView.setPadding(dpToPx(4f))
//        playedMatchesTextView.setPadding(dpToPx(4f))
//        winsTextView.setPadding(dpToPx(4f))
//        drawsTextView.setPadding(dpToPx(4f))
//        losesTextView.setPadding(dpToPx(4f))
//        goalsTextView.setPadding(dpToPx(4f))
//        pointsTextView.setPadding(dpToPx(4f))
//
//        positionTextView.setBackgroundResource(android.R.color.white)
//        teamNameTextView.setBackgroundResource(android.R.color.white)
//        playedMatchesTextView.setBackgroundResource(android.R.color.white)
//        winsTextView.setBackgroundResource(android.R.color.white)
//        drawsTextView.setBackgroundResource(android.R.color.white)
//        losesTextView.setBackgroundResource(android.R.color.white)
//        goalsTextView.setBackgroundResource(android.R.color.white)
//        pointsTextView.setBackgroundResource(android.R.color.white)


tableRow.addView(positionTextView)
//        tableRow.addView(teamNameTextView)
tableRow.addView(playedMatchesTextView)
tableRow.addView(winsTextView)
tableRow.addView(drawsTextView)
tableRow.addView(losesTextView)
//        tableRow.addView(goalsTextView)
tableRow.addView(pointsTextView)
binding.table.addView(tableRow)

binding.linearLayout.addView(teamNameTextView)
binding.linearLayout.addView(goalsTextView)
*/
    }

    private fun setupStartCompetitionButton() {
        if (viewModel.isCurrentUserOwner() && viewModel.leagueSeason.value!!.competition.competitionStatus == CompetitionStatus.PLANNING) {
            binding.startFinishCompetitionButton.setOnClickListener {
                if (viewModel.leagueSeason.value!!.competition.numberOfTeams != viewModel.teams.value!!.size)
                    showSnackbar("Aby rozpocząć rozgrywki, dodanych musi być ${viewModel.leagueSeason.value!!.competition.numberOfTeams} drużyn")
                else {
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage("Czy na pewno chcesz rozpocząć rozgrywki? Nie będzie już można zmienić drużyn i zasad.")
                        .setNegativeButton("Anuluj") { _, _ -> Unit }
                        .setPositiveButton("Ok") { _, _ -> viewModel.startCompetition() }
                        .show()
                }
            }
        } else if (viewModel.isCurrentUserOwner() && viewModel.leagueSeason.value!!.competition.competitionStatus == CompetitionStatus.IN_PROGRESS) {
            binding.startFinishCompetitionButton.setOnClickListener {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("Czy na pewno chcesz zakończyć rozgrywki? Wszystkie zaplanowane mecze zostaną usunięte.")
                    .setNegativeButton("Anuluj") { _, _ -> Unit }
                    .setPositiveButton("Ok") { _, _ -> viewModel.finishCompetition() }
                    .show()
            }
        } else if (!viewModel.isCurrentUserOwner() || viewModel.leagueSeason.value!!.competition.competitionStatus == CompetitionStatus.COMPLETED) {
            binding.startFinishCompetitionButton.visibility = View.GONE
        }
    }

    private fun setupPlanMatchButton() {
        if (viewModel.isCurrentUserOwner() && viewModel.leagueSeason.value!!.competition.competitionStatus == CompetitionStatus.IN_PROGRESS) {
            binding.planMatchButton.setOnClickListener {
                val possibleHomeTeams = viewModel.getPossibleHomeTeams()
                if (possibleHomeTeams.isNotEmpty()) {
                    var homeTeamId: Int
                    var awayTeamId: Int

                    var chosenItemPosition = 0

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Wybierz drużynę domową")
                        .setNegativeButton("Anuluj") { _, _ -> Unit }
                        .setPositiveButton("Ok") { _, _ ->
                            homeTeamId = possibleHomeTeams[chosenItemPosition].teamId!!
                            chosenItemPosition = 0
                            val possibleAwayTeams = viewModel.getPossibleAwayTeam(homeTeamId)
                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Wybierz drużynę wyjazdową")
                                .setNegativeButton("Anuluj") { _, _ -> Unit }
                                .setPositiveButton("Ok") { _, _ ->
                                    awayTeamId = possibleAwayTeams[chosenItemPosition].teamId!!
//                                showToast("Nawigacja do ekranu planowania meczu między drużynami $homeTeamId i $awayTeamId")
                                    findNavController().navigate(LeagueDetailFragmentDirections.actionLeagueDetailFragmentToMatchCreateFragment(homeTeamId, awayTeamId, viewModel.leagueSeason.value!!.competition.competitionId!!))
                                }
                                .setSingleChoiceItems(possibleAwayTeams.map { it.name }.toTypedArray(), chosenItemPosition) { _, which -> chosenItemPosition = which }
                                .show()
                        }
                        .setSingleChoiceItems(possibleHomeTeams.map { it.name }.toTypedArray(), chosenItemPosition) { _, which -> chosenItemPosition = which }
                        .show()
                } else
                    showSnackbar("Wszystkie mecze zostały już rozegrane lub zaplanowane")
            }
            binding.planMatchButton.isEnabled = true
        } else
            binding.planMatchButton.visibility = View.GONE
    }

    private fun showSnackbar(message: String) = Snackbar.make(binding.swipeRefreshLayout, message, Snackbar.LENGTH_SHORT).show()

    private fun showToast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    private fun teamsToCompetitionTeams(teams: List<Team>): List<CompetitionTeamAdapter.CompetitionTeam> {
        return teams.map { CompetitionTeamAdapter.CompetitionTeam(it, it.ownerId == viewModel.getCurrentUserId()) }
    }

    private fun getTableRowsData(): List<LeagueTableRow> {
        return listOf(
            LeagueTableRow("Drużyna z podwórka", 6, 6, 0, 0, 18, 6),
            LeagueTableRow("Wybrzeże klatki schodowej FC", 6, 4, 0, 2, 15, 7),
            LeagueTableRow("Hefalumpy", 6, 0, 2, 4, 6, 9),
            LeagueTableRow("Znicz Leśniów Wielki Wiejski Klub Sportowy", 6, 0, 2, 4, 3, 10)
        )
    }

    private fun dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}