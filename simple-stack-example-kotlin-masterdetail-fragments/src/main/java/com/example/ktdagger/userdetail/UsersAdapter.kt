package com.example.ktdagger.userdetail

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ktdagger.MainActivity
import com.example.ktdagger.R
import com.example.ktdagger.realmobjects.UserRO
import io.realm.Case
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmResults
import io.realm.Sort
import io.realm.kotlin.where

class UsersAdapter(private var realm: Realm, private var items: RealmResults<UserRO>?)
    : RecyclerView.Adapter<UsersViewHolder>() {

    private var realmChangeListener: RealmChangeListener<RealmResults<UserRO>>
        = RealmChangeListener { notifyDataSetChanged() }
    private var searchTerm: String? = null

    init {
        items?.addChangeListener(realmChangeListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        return UsersViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.view_item_contact, parent, false))
    }

    override fun onBindViewHolder(usersViewHolder: UsersViewHolder, position: Int) {
        val item = items!![position]
        usersViewHolder.bind(item!!)
        usersViewHolder.bindClickListener(View.OnClickListener {
            MainActivity[(usersViewHolder.itemView.context)]
                .navigateTo(UserDetailKey(item))
        })
    }

    fun updateInput(input: String) {
        this.searchTerm = input
        if (items != null && items!!.isValid) {
            items!!.removeAllChangeListeners()
        }
        var query = realm.where<UserRO>()
        if (!searchTerm.isNullOrBlank()) {
            query = query.contains("userName", searchTerm!!, Case.INSENSITIVE)
        }
        items = query.sort("userName", Sort.ASCENDING).findAllAsync()
        items!!.addChangeListener(realmChangeListener)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if (items == null || !items!!.isValid) {
            0
        } else items!!.size
    }
}
