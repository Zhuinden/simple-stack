package com.zhuinden.simplestackkotlindaggerexample.userdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.zhuinden.simplestackkotlindaggerexample.MainActivity
import com.zhuinden.simplestackkotlindaggerexample.R
import com.zhuinden.simplestackkotlindaggerexample.realmobjects.UserRO
import io.realm.RealmRecyclerViewAdapter
import io.realm.RealmResults

class UsersAdapter(
    items: RealmResults<UserRO>
) : RealmRecyclerViewAdapter<UserRO, UsersViewHolder>(items, true) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder =
        UsersViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.view_item_contact, parent, false))

    override fun onBindViewHolder(usersViewHolder: UsersViewHolder, position: Int) {
        val item = data?.get(position)
        usersViewHolder.bind(item!!)
        usersViewHolder.bindClickListener(View.OnClickListener {
            MainActivity[(usersViewHolder.itemView.context)]
                .navigateTo(UserDetailKey(item))
        })
    }
}
