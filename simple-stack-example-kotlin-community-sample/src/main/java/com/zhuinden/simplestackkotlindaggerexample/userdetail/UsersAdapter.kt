package com.zhuinden.simplestackkotlindaggerexample.userdetail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zhuinden.simplestackkotlindaggerexample.MainActivity
import com.zhuinden.simplestackkotlindaggerexample.R
import com.zhuinden.simplestackkotlindaggerexample.onClick
import com.zhuinden.simplestackkotlindaggerexample.realmobjects.UserRO
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults
import kotlinx.android.synthetic.main.view_item_contact.view.*

class UsersAdapter(
    items: RealmResults<UserRO>
) : RealmRecyclerViewAdapter<UserRO, UsersAdapter.UsersViewHolder>(items, true) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder =
        UsersViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.view_item_contact, parent, false))

    override fun onBindViewHolder(usersViewHolder: UsersViewHolder, position: Int) {
        val item = data?.get(position)
        usersViewHolder.bind(item!!)
        usersViewHolder.onClick {
            MainActivity[(usersViewHolder.itemView.context)]
                .navigateTo(UserDetailKey(item))
        }
    }

    inner class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val userNameView: TextView = itemView.userName;

        fun bind(user: UserRO) {
            userNameView.text = user.userName;
        }

        fun onClick(click: (View?) -> Unit) {
            itemView.onClick(click)
        }
    }
}