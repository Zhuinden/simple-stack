package com.community.simplestackkotlindaggerexample.screens.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.community.simplestackkotlindaggerexample.R
import com.community.simplestackkotlindaggerexample.data.database.User
import com.community.simplestackkotlindaggerexample.screens.userdetail.UserDetailKey
import com.community.simplestackkotlindaggerexample.utils.onClick
import com.zhuinden.simplestack.navigator.Navigator
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

class UsersAdapter(
    items: RealmResults<User>
) : RealmRecyclerViewAdapter<User, UsersAdapter.UsersViewHolder>(items, true) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder =
        UsersViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.view_item_contact, parent, false))

    override fun onBindViewHolder(usersViewHolder: UsersViewHolder, position: Int) {
        val item = data!!.get(position)

        usersViewHolder.bind(item!!)
        usersViewHolder.itemView.onClick { view ->
            Navigator.getBackstack(view.context).goTo(UserDetailKey(item))
        }
    }

    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameView: TextView = itemView.findViewById(R.id.textUsername)

        fun bind(user: User) {
            userNameView.text = user.userName
        }
    }
}