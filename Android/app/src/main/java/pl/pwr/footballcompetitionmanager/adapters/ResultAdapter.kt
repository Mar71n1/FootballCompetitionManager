package pl.pwr.footballcompetitionmanager.adapters

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.ResultItemViewBinding
import pl.pwr.footballcompetitionmanager.model.Match
import pl.pwr.footballcompetitionmanager.model.MatchStatus

class ResultAdapter(private val clickListener: ResultListener, private val showCompetitionName: Boolean) : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {
    var matches = listOf<Match>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = matches.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(matches[position], clickListener, showCompetitionName)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.create(parent)

    class ViewHolder private constructor(private val binding: ResultItemViewBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match, clickListener: ResultListener, showCompetitionName: Boolean) {
            binding.match = match
            binding.clickListener = clickListener

            with(match) {
                if (showCompetitionName) {
                    if (competitionId == null)
                        binding.competitionNameTextView.text = context.getString(R.string.friendly_match)
                    else
                        binding.competitionNameTextView.text = competitionName
                    binding.competitionNameTextView.visibility = View.VISIBLE
                }

                if (homeTeamGoals != null && awayTeamGoals != null) {
                    if (homeTeamGoals!! > awayTeamGoals!!) {
                        binding.homeTeamNameTextView.typeface = Typeface.DEFAULT_BOLD
                        binding.homeTeamNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    } else if (homeTeamGoals!! < awayTeamGoals!!) {
                        binding.awayTeamNameTextView.typeface = Typeface.DEFAULT_BOLD
                        binding.awayTeamNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                    }
                } else {
                    binding.homeTeamNameTextView.text = ""
                    binding.awayTeamNameTextView.text = ""
                    binding.scoreTextView.text = context.getString(R.string.versus)
                }
            }

            when (match.matchStatus) {
                MatchStatus.SCORE_PROPOSED_HOME_TEAM_OWNER -> binding.infoTextView.text = context.getString(R.string.result_adapter_home_team_owner_proposed)
                MatchStatus.SCORE_PROPOSED_AWAY_TEAM_OWNER -> binding.infoTextView.text = context.getString(R.string.result_adapter_away_team_owner_proposed)
                MatchStatus.SCORE_PROPOSED_2ND_TIME_HOME_TEAM_OWNER -> binding.infoTextView.text = context.getString(R.string.result_adapter_home_team_owner_proposed_new)
                MatchStatus.SCORE_PROPOSED_2ND_TIME_AWAY_TEAM_OWNER -> binding.infoTextView.text = context.getString(R.string.result_adapter_away_team_owner_proposed_new)
                MatchStatus.SCORE_UNKNOWN -> binding.infoTextView.text = context.getString(R.string.result_adapter_match_score_unknown)
                else -> {
                    binding.infoIcon.visibility = View.GONE
                    binding.infoTextView.visibility = View.GONE
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ResultItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, parent.context)
            }
        }
    }

    class ResultListener(val clickListener: (matchId: Int) -> Unit) {
        fun onClick(matchId: Int) = clickListener(matchId)
    }
}