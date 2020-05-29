package com.zhuinden.simplestackexamplescoping.features.words

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zhuinden.simplestackexamplescoping.R
import com.zhuinden.simplestackexamplescoping.utils.inflate
import kotlinx.android.extensions.LayoutContainer
import java.util.*

class WordListAdapter : RecyclerView.Adapter<WordListAdapter.ViewHolder>() {
    private var list: List<String> = Collections.emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent.inflate(R.layout.word_list_item))

    override fun getItemCount(): Int = list.size

    fun updateWords(list: List<String>) {
        this.list = list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        private val text: TextView = containerView.findViewById(R.id.text)

        fun bind(word: String) {
            text.text = word
        }
    }
}