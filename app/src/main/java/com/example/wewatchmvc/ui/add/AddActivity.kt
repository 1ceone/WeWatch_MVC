package com.example.wewatchmvc.ui.add

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.wewatchmvc.R
import com.example.wewatchmvc.databinding.ActivityAddBinding
import com.example.wewatchmvc.model.Movie
import com.example.wewatchmvc.ui.search.SearchActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding

    private val viewModel: AddViewModel by viewModels()

    private val searchLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val movie = result.data?.getSerializableExtra("selected_movie") as? Movie
            movie?.let {
                viewModel.handleIntent(AddIntent.SelectMovie(it))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()
        setupClickListeners()

        (intent.getSerializableExtra("movie") as? Movie)?.let {
            viewModel.handleIntent(AddIntent.SelectMovie(it))
        }
    }

    private fun setupClickListeners() {
        binding.btnSearchMovie.setOnClickListener {
            viewModel.handleIntent(AddIntent.NavigateToSearch)
        }

        binding.btnAddMovie.setOnClickListener {
            viewModel.handleIntent(AddIntent.AddMovie)
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

    private fun renderState(state: AddState) {
        when (state) {
            is AddState.Idle -> showIdle()
            is AddState.MovieSelected -> showMovieInfo(state.movie)
            is AddState.Adding -> showAdding()
            is AddState.Success -> showSuccess(state.message)
            is AddState.Error -> showError(state.message)
        }
    }

    private fun renderEffect(effect: AddEffect) {
        when (effect) {
            is AddEffect.ShowToast -> Toast.makeText(this, effect.message, Toast.LENGTH_SHORT).show()
            is AddEffect.NavigateBack -> finish()
            is AddEffect.NavigateToSearch -> {
                searchLauncher.launch(Intent(this, SearchActivity::class.java))
            }
        }
    }

    private fun showIdle() {
        binding.movieInfoContainer.visibility = android.view.View.GONE
        binding.btnAddMovie.isEnabled = false
    }

    private fun showMovieInfo(movie: Movie) {
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
        binding.btnAddMovie.isEnabled = true
    }

    private fun showAdding() {
        binding.btnAddMovie.isEnabled = false
        binding.btnAddMovie.text = "Добавление..."
    }

    private fun showSuccess(message: String) {
        binding.btnAddMovie.isEnabled = true
        binding.btnAddMovie.text = "Добавить в список"
    }

    private fun showError(message: String) {
        binding.btnAddMovie.isEnabled = true
        binding.btnAddMovie.text = "Добавить в список"
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}