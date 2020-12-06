package pl.pwr.footballcompetitionmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.pwr.footballcompetitionmanager.databinding.CompetitionTeamItemViewBinding
import pl.pwr.footballcompetitionmanager.model.Team
import pl.pwr.footballcompetitionmanager.model.User

class CompetitionTeamAdapter(private val clickListener: CompetitionTeamListener, private val showRemoveButton: Boolean) : RecyclerView.Adapter<CompetitionTeamAdapter.ViewHolder>() {
    var teams = listOf<CompetitionTeam>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = teams.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(teams[position], showRemoveButton, clickListener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.create(parent)

    class ViewHolder private constructor(private val binding: CompetitionTeamItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(competitionTeam: CompetitionTeam, showRemoveButton: Boolean, clickListener: CompetitionTeamListener) {
            binding.team = competitionTeam.team
            binding.clickListener = clickListener

            if (competitionTeam.isOwner) {
                binding.ownerIcon.visibility = View.VISIBLE
                binding.ownerInfo.visibility = View.VISIBLE
            } else {
                binding.ownerIcon.visibility = View.GONE
                binding.ownerInfo.visibility = View.GONE
            }

            if (showRemoveButton) binding.removeButton.visibility = View.VISIBLE
            else binding.removeButton.visibility = View.GONE
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CompetitionTeamItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class CompetitionTeamListener(val clickListener: (Int, Action) -> Unit) {
        enum class Action {
            NAVIGATE,
            REMOVE
        }

        fun onTeamClick(teamId: Int) = clickListener(teamId, Action.NAVIGATE)
        fun onRemoveClick(teamId: Int) = clickListener(teamId, Action.REMOVE)
    }

    data class CompetitionTeam(val team: Team, val isOwner: Boolean)
}