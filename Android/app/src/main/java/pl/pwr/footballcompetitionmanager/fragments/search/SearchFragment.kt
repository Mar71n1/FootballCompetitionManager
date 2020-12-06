package pl.pwr.footballcompetitionmanager.fragments.search

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getColor
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.FragmentSearchBinding
import pl.pwr.footballcompetitionmanager.adapters.CompetitionAdapter
import pl.pwr.footballcompetitionmanager.adapters.TeamAdapter
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModelFactoryTeams: SearchViewModelFactory
    private lateinit var viewModel: SearchViewModel

    private lateinit var teamAdapter: TeamAdapter
    private lateinit var competitionAdapter: CompetitionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate finished")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        viewModelFactoryTeams = SearchViewModelFactory(RemoteRepository())
        viewModel = ViewModelProvider(this, viewModelFactoryTeams).get(SearchViewModel::class.java)
        binding.viewModel = viewModel

        setupSearchBar()
        setupChipGroupListener()
        createAdapters()
        setRecyclerViewAdapter()
        observeViewModel()

        Timber.d("onCreateView finished")
        return binding.root
    }

    private fun setupSearchBar() {
        binding.searchBar.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                viewModel.search(p0!!)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean = false
        })
        binding.searchBar.isSubmitButtonEnabled = true
        binding.searchBar.setIconifiedByDefault(false)
    }

    private fun setupChipGroupListener() {
        binding.chipGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.teams_chip -> {
                    binding.recyclerView.adapter = teamAdapter
                    showContent()
                }
                R.id.competitions_chip -> {
                    binding.recyclerView.adapter = competitionAdapter
                    showContent()
                }
            }
        }
    }

    private fun createAdapters() {
        teamAdapter = TeamAdapter(TeamAdapter.TeamListener {
            findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToTeamDetailFragment(it.teamId!!))
        }, viewModel.getCurrentUserId())
        competitionAdapter = CompetitionAdapter(CompetitionAdapter.CompetitionListener {
            findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToLeagueDetailFragment(it))
        })
    }

    private fun setRecyclerViewAdapter() {
        when (binding.chipGroup.checkedChipId) {
            R.id.teams_chip -> binding.recyclerView.adapter = teamAdapter
            R.id.competitions_chip -> binding.recyclerView.adapter = competitionAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.teams.observe(viewLifecycleOwner, Observer {
            teamAdapter.teams = it
            showContent()
        })

        viewModel.competitions.observe(viewLifecycleOwner, Observer {
            competitionAdapter.competitions = it
            showContent()
        })
    }

    private fun showContent() {
        if (binding.recyclerView.adapter!!.itemCount == 0) {
            when (binding.chipGroup.checkedChipId) {
                R.id.teams_chip -> binding.noItemsInfoTextView.text = getString(R.string.fragment_search_no_teams_message)
                R.id.competitions_chip -> binding.noItemsInfoTextView.text = getString(R.string.fragment_search_no_competitions_message)
            }
            if (binding.noItemsInfoTextView.visibility == View.GONE) binding.noItemsInfoTextView.visibility = View.VISIBLE
        } else {
            if (binding.noItemsInfoTextView.visibility == View.VISIBLE) binding.noItemsInfoTextView.visibility = View.GONE
        }
    }
}