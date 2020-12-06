package pl.pwr.footballcompetitionmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import pl.pwr.footballcompetitionmanager.databinding.PlayerItemViewBinding
import pl.pwr.footballcompetitionmanager.model.User

class PlayerAdapter(private val clickListener: PlayerListener, private val isCurrentUserOwner: Boolean)
    : ListAdapter<PlayerAdapter.TeamPlayer, PlayerAdapter.ViewHolder>(TeamPlayerDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), isCurrentUserOwner, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder.create(parent)

    class ViewHolder private constructor(private val binding: PlayerItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(teamPlayer: TeamPlayer, isCurrentUserOwner: Boolean, clickListener: PlayerListener) {
            binding.user = teamPlayer.user
            binding.clickListener = clickListener

            if (teamPlayer.isOwner) {
                binding.ownerIcon.visibility = View.VISIBLE
                binding.ownerInfo.visibility = View.VISIBLE
            }

            if (isCurrentUserOwner && !teamPlayer.isOwner)
                binding.removeButton.visibility = View.VISIBLE
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PlayerItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class TeamPlayerDiffCallback : DiffUtil.ItemCallback<TeamPlayer>() {
        override fun areItemsTheSame(oldItem: TeamPlayer, newItem: TeamPlayer): Boolean = oldItem.user.userId == newItem.user.userId
        override fun areContentsTheSame(oldItem: TeamPlayer, newItem: TeamPlayer): Boolean = oldItem == newItem
    }

    class PlayerListener(val clickListener: (userId: Int) -> Unit) {
        fun onClick(userId: Int) = clickListener(userId)
    }

    data class TeamPlayer(val user: User, val isOwner: Boolean)
}