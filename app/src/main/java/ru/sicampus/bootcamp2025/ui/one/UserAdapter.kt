package ru.sicampus.bootcamp2025.ui.one

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.sicampus.bootcamp2025.R
import ru.sicampus.bootcamp2025.databinding.ItemUserBinding
import ru.sicampus.bootcamp2025.data.one.UserCenter

class UserAdapter(
    private var users: List<UserCenter>,
    private val onRegisterClick: (String) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemUserBinding.bind(view)

        fun bind(user: UserCenter) {
            binding.userName.text = user.name

            itemView.setOnClickListener {
                onRegisterClick(user.name)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    fun submitList(newList: List<UserCenter>) {
        users = newList
    }
}