package pl.pwr.footballcompetitionmanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.pwr.footballcompetitionmanager.databinding.UserRequestItemViewBinding
import pl.pwr.footballcompetitionmanager.model.User

class RequestUserAdapter(private val clickListener: RequestUserListener) : RecyclerView.Adapter<RequestUserAdapter.ViewHolder>() {
    var requestsUsers = listOf<User>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = requestsUsers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(requestsUsers[position], clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder.create(
            parent
        )

    class ViewHolder private constructor(private val binding: UserRequestItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(requestUser: User, clickListener: RequestUserListener) {
            binding.requestUser = requestUser
            binding.clickListener = clickListener
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = UserRequestItemViewBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class RequestUserListener(val clickListener: (userId: Int, requestAction: RequestAction) -> Unit) {
        enum class RequestAction {
            REJECT,
            ACCEPT
        }

        fun onRejectClick(userId: Int) = clickListener(userId,
            RequestAction.REJECT
        )
        fun onAcceptClick(userId: Int) = clickListener(userId,
            RequestAction.ACCEPT
        )
    }
}