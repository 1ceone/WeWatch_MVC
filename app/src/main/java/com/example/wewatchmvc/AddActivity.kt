package com.example.wewatchmvc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.wewatchmvc.controller.AddController
import com.example.wewatchmvc.databinding.ActivityAddBinding
import com.example.wewatchmvc.model.Movie
import com.bumptech.glide.Glide

class AddActivity : AppCompatActivity(), AddController.AddCallback {

    private lateinit var binding: ActivityAddBinding
    private lateinit var controller: AddController
    private var selectedMovie: Movie? = null

    // Запуск SearchActivity для поиска фильма
    private val searchLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val movie = result.data?.getSerializableExtra("selected_movie") as? Movie
            movie?.let {
                selectedMovie = it
                displayMovieInfo(it)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = AddController(this)
        controller.setCallback(this)

        setupClickListeners()

        // Если передан фильм из другого места, отображаем его
        (intent.getSerializableExtra("movie") as? Movie)?.let {
            selectedMovie = it
            displayMovieInfo(it)
        }
    }

    private fun setupClickListeners() {
        // Кнопка для открытия экрана поиска
        binding.btnSearchMovie.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            searchLauncher.launch(intent)
        }

        // Кнопка добавления фильма в БД
        binding.btnAddMovie.setOnClickListener {
            selectedMovie?.let { movie ->
                controller.addMovie(movie)
            } ?: Toast.makeText(this, "Сначала найдите фильм", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displayMovieInfo(movie: Movie) {
        binding.tvMovieTitle.text = movie.title
        binding.tvMovieYear.text = movie.year

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

        binding.movieInfoContainer.visibility = android.view.View.VISIBLE
    }

    // Callback методы
    override fun onMovieAdded() {
        Toast.makeText(this, "Фильм добавлен в список", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }
}