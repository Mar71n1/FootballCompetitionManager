package pl.pwr.footballcompetitionmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.pwr.footballcompetitionmanager.databinding.MatchItemViewBinding
import pl.pwr.footballcompetitionmanager.model.Match
import pl.pwr.footballcompetitionmanager.model.MatchStatus
import org.threeten.bp.LocalDateTime
import pl.pwr.footballcompetitionmanager.R

class MatchAdapter(private val clickListener: MatchListener) : RecyclerView.Adapter<MatchAdapter.ViewHolder>() {
    var matches = listOf<Match>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = matches.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(matches[position], clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.create(parent)

    class ViewHolder private constructor(private val binding: MatchItemViewBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match, clickListener: MatchListener) {
            binding.match = match
            binding.clickListener = clickListener

            when (match.matchStatus) {
                MatchStatus.PROPOSED_HOME_TEAM_OWNER -> binding.infoTextView.text = context.getString(R.string.match_adapter_home_team_owner_proposed)
                MatchStatus.PROPOSED_AWAY_TEAM_OWNER -> binding.infoTextView.text = context.getString(R.string.match_adapter_away_team_owner_proposed)
                else -> {
                    if (match.matchStatus == MatchStatus.PLANNED && LocalDateTime.now().isAfter(match.time.plusMinutes(match.length.toLong())))
                        binding.infoTextView.text = context.getString(R.string.match_adapter_match_waiting_for_score)
                    else {
                        binding.infoIcon.visibility = View.GONE
                        binding.infoTextView.visibility = View.GONE
                    }
                }
            }

            if (match.competitionId == null)
                binding.competitionNameTextView.text = context.getString(R.string.friendly_match)
            else
                binding.competitionNameTextView.text = match.competitionName
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MatchItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, parent.context)
            }
        }
    }
}