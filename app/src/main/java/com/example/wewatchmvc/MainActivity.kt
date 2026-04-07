package com.example.wewatch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wewatch.controller.MainController
import com.example.wewatch.databinding.ActivityMainBinding
import com.example.wewatch.model.Movie
import com.example.wewatch.view.adapter.MovieAdapter

class MainActivity : AppCompatActivity(), MainController.MainCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: MainController
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Создаем контроллер
        controller = MainController(this)
        controller.setCallback(this)

        setupRecyclerView()
        setupFab()
        setupDeleteButton()

        // Загружаем фильмы
        controller.loadMovies()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(emptyList()) { _, _ -> }
        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = movieAdapter
        }
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }
    }

    private fun setupDeleteButton() {
        binding.toolbar.inflateMenu(R.menu.menu_main)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    val selectedMovies = movieAdapter.getSelectedMovies()
                    if (selectedMovies.isNotEmpty()) {
                        controller.deleteMovies(selectedMovies)
                        movieAdapter.clearSelection()
                    } else {
                        Toast.makeText(this, "Выберите фильмы для удаления", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.ivEmpty.visibility = android.view.View.VISIBLE
            binding.tvEmpty.visibility = android.view.View.VISIBLE
            binding.rvMovies.visibility = android.view.View.GONE
        } else {
            binding.ivEmpty.visibility = android.view.View.GONE
            binding.tvEmpty.visibility = android.view.View.GONE
            binding.rvMovies.visibility = android.view.View.VISIBLE
        }
    }

    // Callback методы
    override fun onMoviesLoaded(movies: List<Movie>) {
        if (movies.isEmpty()) {
            showEmptyState(true)
        } else {
            showEmptyState(false)
            movieAdapter.updateMovies(movies)
        }
    }

    override fun onMoviesDeleted() {
        Toast.makeText(this, "Фильмы удалены", Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }
}