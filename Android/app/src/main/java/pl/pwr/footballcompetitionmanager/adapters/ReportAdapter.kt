package pl.pwr.footballcompetitionmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.pwr.footballcompetitionmanager.databinding.ReportItemViewBinding
import pl.pwr.footballcompetitionmanager.model.Report

class ReportAdapter(private val clickListener: ReportListener) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {
    var reports = listOf<Report>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = reports.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reports[position], clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.create(
            parent
        )

    class ViewHolder private constructor(private val binding: ReportItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(report: Report, clickListener: ReportListener) {
            binding.report = report
            binding.clickListener = clickListener

            if (!report.isSolved) {
                binding.markAsSolvedButton.visibility = View.VISIBLE
                binding.solvedDateTextView.visibility = View.GONE
            }
            else {
                binding.markAsSolvedButton.visibility = View.GONE
                binding.solvedDateTextView.visibility = View.VISIBLE
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ReportItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(
                    binding
                )
            }
        }
    }

    class ReportListener(val clickListener: (reportId: Int) -> Unit) {
        fun onClick(reportId: Int) = clickListener(reportId)
    }
}