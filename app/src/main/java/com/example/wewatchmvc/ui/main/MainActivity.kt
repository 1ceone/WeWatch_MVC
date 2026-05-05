package com.example.wewatchmvc.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wewatchmvc.R
import com.example.wewatchmvc.databinding.ActivityMainBinding
import com.example.wewatchmvc.model.Movie
import com.example.wewatchmvc.ui.add.AddActivity
import com.example.wewatchmvc.view.adapter.MovieAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var movieAdapter: MovieAdapter

    // Исправленный способ создания ViewModel
    private val viewModel: MainViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(application) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupFab()
        setupDeleteButton()

        viewModel.handleIntent(MainIntent.LoadMovies)
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter(emptyList()) { movie: Movie, isChecked: Boolean ->
            viewModel.handleIntent(MainIntent.SelectMovie(movie.imdbId, isChecked))
        }
        binding.rvMovies.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = movieAdapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                renderState(state)
            }
        }

        lifecycleScope.launch {
            viewModel.effect.collect { effect ->
                renderEffect(effect)
            }
        }
    }

    private fun renderState(state: MainState) {
        when (state) {
            is MainState.Loading -> showLoading()
            is MainState.Success -> showMovies(state.movies, state.selectedMovies)
            is MainState.Error -> showError(state.message)
            is MainState.Empty -> showEmpty()
        }
    }

    private fun renderEffect(effect: MainEffect) {
        when (effect) {
            is MainEffect.ShowToast -> Toast.makeText(this, effect.message, Toast.LENGTH_SHORT).show()
            is MainEffect.NavigateToAdd -> startActivity(Intent(this, AddActivity::class.java))
        }
    }

    private fun showLoading() {
        // Показать прогресс
    }

    private fun showMovies(movies: List<Movie>, selectedIds: Set<String>) {
        binding.ivEmpty.visibility = android.view.View.GONE
        binding.tvEmpty.visibility = android.view.View.GONE
        binding.rvMovies.visibility = android.view.View.VISIBLE

        movieAdapter.updateMovies(movies)
        movieAdapter.setSelectedMovies(selectedIds)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun showEmpty() {
        binding.ivEmpty.visibility = android.view.View.VISIBLE
        binding.tvEmpty.visibility = android.view.View.VISIBLE
        binding.rvMovies.visibility = android.view.View.GONE
    }

    private fun setupFab() {
        binding.fabAdd.setOnClickListener {
            viewModel.handleIntent(MainIntent.NavigateToAdd)
        }
    }

    private fun setupDeleteButton() {
        binding.toolbar.inflateMenu(R.menu.menu_main)
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_delete -> {
                    val state = viewModel.state.value
                    if (state is MainState.Success && state.selectedMovies.isNotEmpty()) {
                        val moviesToDelete = state.movies.filter {
                            state.selectedMovies.contains(it.imdbId)
                        }
                        viewModel.handleIntent(MainIntent.DeleteMovies(moviesToDelete))
                    } else {
                        Toast.makeText(this, "Выберите фильмы для удаления", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }
    }
}