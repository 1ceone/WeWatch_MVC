package com.example.wewatch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.wewatch.controller.AddController
import com.example.wewatch.databinding.ActivityAddBinding
import com.example.wewatch.model.Movie
import com.bumptech.glide.Glide

class AddActivity : AppCompatActivity(), AddController.AddCallback {

    private lateinit var binding: ActivityAddBinding
    private lateinit var controller: AddController
    private var selectedMovie: Movie? = null

    private val searchLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val movie = result.data?.getSerializableExtra("selected_movie") as? Movie
            movie?.let {
                selectedMovie = it
                updateUIWithMovie(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Создаем контроллер
        controller = AddController(this)
        controller.setCallback(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSearch.setOnClickListener {
            val searchQuery = binding.etSearchQuery.text.toString().trim()
            if (searchQuery.isNotEmpty()) {
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("search_query", searchQuery)
                val year = binding.etYear.text.toString().trim()
                if (year.isNotEmpty()) {
                    intent.putExtra("search_year", year)
                }
                searchLauncher.launch(intent)
            } else {
                Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAddMovie.setOnClickListener {
            selectedMovie?.let { movie ->
                controller.addMovie(movie)
            } ?: Toast.makeText(this, "Сначала найдите фильм", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUIWithMovie(movie: Movie) {
        binding.etTitle.setText(movie.title)
        binding.etYearResult.setText(movie.year)

        val posterUrl = movie.posterUrl
        if (posterUrl.isNotEmpty() && posterUrl != "N/A") {
            Glide.with(this)
                .load(posterUrl)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .into(binding.ivPoster)
        } else {
            binding.ivPoster.setImageResource(R.drawable.ic_placeholder)
        }

        binding.ivPoster.visibility = android.view.View.VISIBLE
    }

    // Callback методы от AddController
    override fun onMovieAdded() {
        Toast.makeText(this, "Фильм добавлен", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }
}