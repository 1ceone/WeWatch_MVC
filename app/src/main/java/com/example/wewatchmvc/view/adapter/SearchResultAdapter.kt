package com.example.wewatchmvc.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wewatchmvc.model.SearchResult
import com.example.wewatchmvc.R

class SearchResultAdapter(
    private var results: List<SearchResult>,
    private val onItemClick: (SearchResult) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder>() {

    fun updateResults(newResults: List<SearchResult>) {
        results = newResults
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(results[position])
    }

    override fun getItemCount() = results.size

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val posterImageView: ImageView = itemView.findViewById(R.id.iv_poster)
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_title)
        private val yearTextView: TextView = itemView.findViewById(R.id.tv_year)
        private val genreTextView: TextView = itemView.findViewById(R.id.tv_genre)

        fun bind(result: SearchResult) {
            titleTextView.text = result.title ?: ""
            yearTextView.text = result.year ?: ""
            genreTextView.text = result.genre ?: "Жанр не указан"

            val posterUrl = result.posterUrl
            if (!posterUrl.isNullOrEmpty() && posterUrl != "N/A") {
                Glide.with(itemView.context)
                    .load(posterUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(posterImageView)
            } else {
                posterImageView.setImageResource(R.drawable.ic_placeholder)
            }

            itemView.setOnClickListener {
                onItemClick(result)
            }
        }
    }
}