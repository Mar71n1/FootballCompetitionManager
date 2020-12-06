package pl.pwr.footballcompetitionmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.CompetitionItemViewBinding
import pl.pwr.footballcompetitionmanager.model.Competition
import pl.pwr.footballcompetitionmanager.model.CompetitionStatus

class CompetitionAdapter(private val clickListener: CompetitionListener) : RecyclerView.Adapter<CompetitionAdapter.ViewHolder>() {
    var competitions = listOf<Competition>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = competitions.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(competitions[position], clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.create(parent)

    class ViewHolder private constructor(private val binding: CompetitionItemViewBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(competition: Competition, clickListener: CompetitionListener) {
            binding.competition = competition
            binding.clickListener = clickListener

            when (competition.competitionStatus) {
                CompetitionStatus.PLANNING -> binding.competitionStatusTextView.text = context.getString(R.string.competition_status_planning_label)
                CompetitionStatus.IN_PROGRESS -> binding.competitionStatusTextView.text = context.getString(R.string.competition_status_in_progress_label)
                CompetitionStatus.COMPLETED -> binding.competitionStatusTextView.text = context.getString(R.string.competition_status_completed_label)
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CompetitionItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, parent.context)
            }
        }
    }

    class CompetitionListener(val clickListener: (competitionId: Int) -> Unit) {
        fun onClick(competitionId: Int) = clickListener(competitionId)
    }
}