package pl.pwr.footballcompetitionmanager.fragments.reportlist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.adapters.ReportAdapter
import pl.pwr.footballcompetitionmanager.databinding.FragmentReportListBinding
import pl.pwr.footballcompetitionmanager.repository.RemoteRepository
import timber.log.Timber

class ReportListFragment : Fragment() {

    private lateinit var binding: FragmentReportListBinding
    private lateinit var viewModelFactory: ReportListViewModelFactory
    private lateinit var viewModel: ReportListViewModel

    private lateinit var reportAdapter: ReportAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_list, container, false)
        viewModelFactory = ReportListViewModelFactory(RemoteRepository())
        viewModel = ViewModelProvider(this, viewModelFactory).get(ReportListViewModel::class.java)
        binding.viewModel = viewModel

        setupRecyclerView()
        observeViewModel()
        setupRefreshListener()

        Timber.d("onCreateView finished")
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if (!it) {
                setupTabLayout()
                binding.loadingIcon.visibility = View.GONE
                binding.mainLinearLayout.visibility = View.VISIBLE
            }
        })

        viewModel.unsolvedReports.observe(viewLifecycleOwner, Observer {
            if (binding.tabLayout.selectedTabPosition == 0) {
                reportAdapter.reports = it
                tabDataRefreshed()
            }
        })

        viewModel.solvedReports.observe(viewLifecycleOwner, Observer {
            if (binding.tabLayout.selectedTabPosition == 1) {
                reportAdapter.reports = it
                tabDataRefreshed()
            }
        })


        viewModel.markedAsSolvedMessage.observe(viewLifecycleOwner, Observer {
            Snackbar.make(binding.mainLinearLayout, getString(it), Snackbar.LENGTH_SHORT).show()
        })

        viewModel.getSnackbarMessage().observe(viewLifecycleOwner, Observer {
            if (it != null)
                Snackbar.make(binding.mainLinearLayout, getString(it), Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun setupTabLayout() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab!!.position) {
                    0 -> {
                        reportAdapter.reports = viewModel.unsolvedReports.value!!
                        binding.noReportsInfoTextView.text = getString(R.string.fragment_report_list_no_unsolved_reports_message)
                        tabDataRefreshed()
                    }
                    1 -> {
                        reportAdapter.reports = viewModel.solvedReports.value!!
                        binding.noReportsInfoTextView.text = getString(R.string.fragment_report_list_no_solved_reports_message)
                        tabDataRefreshed()
                    }
                    else -> Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onTabReselected(tab: TabLayout.Tab?) { }
            override fun onTabUnselected(tab: TabLayout.Tab?) { }
        })
    }

    private fun tabDataRefreshed() {
        if (binding.recyclerView.adapter!!.itemCount == 0 && binding.recyclerView.visibility == View.VISIBLE) {
            binding.recyclerView.visibility = View.GONE
            binding.noReportsInfoTextView.visibility = View.VISIBLE
        } else if (binding.recyclerView.adapter!!.itemCount != 0 && binding.noReportsInfoTextView.visibility == View.VISIBLE) {
            binding.noReportsInfoTextView.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

//    private fun tabChanged(noItemsInfo: String) {
//        if (reportAdapter.itemCount == 0) {
//            if (binding.recyclerView.visibility == View.VISIBLE) {
//                binding.recyclerView.visibility = View.GONE
//                binding.noReportsInfoTextView.visibility = View.VISIBLE
//            }
//        } else {
//            Timber.d("tabChanged są zgłoszenia")
//            if (binding.noReportsInfoTextView.visibility == View.VISIBLE) {
//                binding.noReportsInfoTextView.visibility = View.GONE
//                binding.recyclerView.visibility = View.VISIBLE
//            }
//        }
//    }

    private fun setupRecyclerView() {
        reportAdapter = ReportAdapter(ReportAdapter.ReportListener {
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.fragment_report_list_dialog_title)
                .setNegativeButton(R.string.fragment_report_list_dialog_negative_button_label) { _, _ -> }
                .setPositiveButton(R.string.fragment_report_list_dialog_positive_button_label) { _, _ ->
                    viewModel.markReportAsSolved(it)
                }
                .show()
        })
        binding.recyclerView.adapter = reportAdapter
    }

    private fun setupRefreshListener() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshReports()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}