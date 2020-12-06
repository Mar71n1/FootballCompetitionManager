package pl.pwr.footballcompetitionmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.pwr.footballcompetitionmanager.databinding.TeamItemViewBinding
import pl.pwr.footballcompetitionmanager.model.Team

class TeamAdapter(private val clickListener: TeamListener, private val currentUserId: Int) : RecyclerView.Adapter<TeamAdapter.ViewHolder>() {
    var teams = listOf<Team>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = teams.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(teams[position], clickListener, currentUserId)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.create(parent)

    class ViewHolder private constructor(private val binding: TeamItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(team: Team, clickListener: TeamListener, currentUserId: Int) {
            binding.team = team
            binding.clickListener = clickListener

            if (currentUserId == team.ownerId) {
                binding.infoIcon.visibility = View.VISIBLE
                binding.ownerInfo.visibility = View.VISIBLE
            } else {
                binding.infoIcon.visibility = View.GONE
                binding.ownerInfo.visibility = View.GONE
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TeamItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class TeamListener(val clickListener: (team: Team) -> Unit) {
        fun onClick(team: Team) = clickListener(team)
    }
}