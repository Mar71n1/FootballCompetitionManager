package pl.pwr.footballcompetitionmanager.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.pwr.footballcompetitionmanager.R
import pl.pwr.footballcompetitionmanager.databinding.CompetitionMatchItemViewBinding
import pl.pwr.footballcompetitionmanager.model.Match
import pl.pwr.footballcompetitionmanager.model.MatchStatus
import org.threeten.bp.LocalDateTime

class CompetitionMatchAdapter(private val clickListener: MatchListener) : RecyclerView.Adapter<CompetitionMatchAdapter.ViewHolder>() {
    var matches = listOf<Match>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = matches.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(matches[position], clickListener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.create(parent, parent.context)

    class ViewHolder private constructor(private val binding: CompetitionMatchItemViewBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(match: Match, clickListener: MatchListener) {
            binding.match = match
            binding.clickListener = clickListener

            with (match) {
                binding.infoIcon.visibility = View.VISIBLE
                binding.infoTextView.visibility = View.VISIBLE

                when {
                    matchStatus == MatchStatus.SCORE_UNKNOWN -> binding.infoTextView.text = context.getString(R.string.match_status_score_unknown_info)
                    LocalDateTime.now().isAfter(time.plusMinutes(length.toLong())) -> binding.infoTextView.text = context.getString(R.string.match_status_played_no_score_info)
                    else -> {
                        binding.infoIcon.visibility = View.GONE
                        binding.infoTextView.visibility = View.GONE
                    }
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup, context: Context): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CompetitionMatchItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, context)
            }
        }
    }
}