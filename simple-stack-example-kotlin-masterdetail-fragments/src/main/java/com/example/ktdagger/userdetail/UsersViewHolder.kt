package com.example.ktdagger.userdetail

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.ktdagger.realmobjects.UserRO
import kotlinx.android.synthetic.main.view_item_contact.view.userName

class UsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val userNameView: TextView = itemView.userName;

    fun bind(user: UserRO) {
        userNameView.text = user.userName;
    }

    fun bindClickListener(onClickListener: View.OnClickListener) {
        itemView.setOnClickListener(onClickListener);
    }
}
