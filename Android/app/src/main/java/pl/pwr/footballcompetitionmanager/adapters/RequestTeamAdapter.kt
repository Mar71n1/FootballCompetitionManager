package pl.pwr.footballcompetitionmanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.pwr.footballcompetitionmanager.databinding.TeamRequestItemViewBinding
import pl.pwr.footballcompetitionmanager.model.Team

class RequestTeamAdapter(private val clickListener: RequestTeamListener) : RecyclerView.Adapter<RequestTeamAdapter.ViewHolder>() {
    var requestsTeams = listOf<Team>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = requestsTeams.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(requestsTeams[position], clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.create(parent)

    class ViewHolder private constructor(private val binding: TeamRequestItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(requestTeam: Team, clickListener: RequestTeamListener) {
            binding.team = requestTeam
            binding.clickListener = clickListener
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TeamRequestItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class RequestTeamListener(val clickListener: (teamId: Int, requestAction: Action) -> Unit) {
        enum class Action {
            NAVIGATE,
            REJECT,
            ACCEPT
        }

        fun onTeamClick(teamId: Int) = clickListener(teamId, Action.NAVIGATE)
        fun onRejectClick(teamId: Int) = clickListener(teamId, Action.REJECT)
        fun onAcceptClick(teamId: Int) = clickListener(teamId, Action.ACCEPT)
    }
}