package com.example.wewatchmvc.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wewatchmvc.model.Movie
import com.example.wewatchmvc.R

class MovieAdapter(
    private var movies: List<Movie>,
    private val onItemCheckChanged: (Movie, Boolean) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private val selectedMovies = mutableSetOf<Movie>()

    fun updateMovies(newMovies: List<Movie>) {
        movies = newMovies
        clearSelection()
        notifyDataSetChanged()
    }

    fun getSelectedMovies(): List<Movie> = selectedMovies.toList()

    fun clearSelection() {
        selectedMovies.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val posterImageView: ImageView = itemView.findViewById(R.id.iv_poster)
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_title)
        private val yearTextView: TextView = itemView.findViewById(R.id.tv_year)
        private val checkBox: CheckBox = itemView.findViewById(R.id.cb_select)

        fun bind(movie: Movie) {
            titleTextView.text = movie.title
            yearTextView.text = movie.year

            val posterUrl = movie.posterUrl
            if (posterUrl.isNotEmpty() && posterUrl != "N/A") {
                Glide.with(itemView.context)
                    .load(posterUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_error)
                    .into(posterImageView)
            } else {
                posterImageView.setImageResource(R.drawable.ic_placeholder)
            }

            checkBox.isChecked = selectedMovies.contains(movie)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedMovies.add(movie)
                } else {
                    selectedMovies.remove(movie)
                }
                onItemCheckChanged(movie, isChecked)
            }
        }
    }
}