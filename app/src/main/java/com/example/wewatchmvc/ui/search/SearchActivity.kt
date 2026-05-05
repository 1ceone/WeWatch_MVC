package com.example.wewatchmvc.ui.search

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wewatchmvc.databinding.ActivitySearchBinding
import com.example.wewatchmvc.model.SearchResult
import com.example.wewatchmvc.view.adapter.SearchResultAdapter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: SearchResultAdapter

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupSearchButton()

        // Автоматический поиск если есть запрос
        intent.getStringExtra("search_query")?.let { query ->
            val year = intent.getStringExtra("search_year")
            binding.etSearchQuery.setText(query)
            year?.let { binding.etYear.setText(it) }
            viewModel.handleIntent(SearchIntent.Search(query, year))
        }
    }

    private fun setupRecyclerView() {
        adapter = SearchResultAdapter(emptyList()) { result: SearchResult ->
            viewModel.handleIntent(SearchIntent.SelectMovie(result))
        }

        binding.rvResults.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = this@SearchActivity.adapter
        }
    }

    private fun setupSearchButton() {
        binding.btnSearch.setOnClickListener {
            val query = binding.etSearchQuery.text.toString().trim()
            val year = binding.etYear.text.toString().trim()

            if (query.isNotEmpty()) {
                viewModel.handleIntent(SearchIntent.Search(query, year.takeIf { it.isNotEmpty() }))
            } else {
                Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupObservers() {
        viewModel.state.onEach { state ->
            renderState(state)
        }.launchIn(lifecycleScope)

        viewModel.effect.onEach { effect ->
            renderEffect(effect)
        }.launchIn(lifecycleScope)
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.Idle -> showIdle()
            is SearchState.Loading -> showLoading()
            is SearchState.Success -> showResults(state.results)
            is SearchState.Error -> showError(state.message)
        }
    }

    private fun renderEffect(effect: SearchEffect) {
        when (effect) {
            is SearchEffect.NavigateBackWithMovie -> {
                val intent = Intent().apply {
                    putExtra("selected_movie", effect.movie as java.io.Serializable)
                }
                setResult(RESULT_OK, intent)
                finish()
            }
            is SearchEffect.ShowToast -> Toast.makeText(this, effect.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showIdle() {
        binding.tvEmpty.visibility = android.view.View.GONE
        binding.rvResults.visibility = android.view.View.GONE
        binding.progressBar.visibility = android.view.View.GONE
    }

    private fun showLoading() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.rvResults.visibility = android.view.View.GONE
        binding.tvEmpty.visibility = android.view.View.GONE
    }

    private fun showResults(results: List<SearchResult>) {
        binding.progressBar.visibility = android.view.View.GONE
        adapter.updateResults(results)

        if (results.isEmpty()) {
            binding.tvEmpty.visibility = android.view.View.VISIBLE
            binding.rvResults.visibility = android.view.View.GONE
        } else {
            binding.tvEmpty.visibility = android.view.View.GONE
            binding.rvResults.visibility = android.view.View.VISIBLE
        }
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = android.view.View.GONE
        binding.tvEmpty.visibility = android.view.View.VISIBLE
        binding.tvEmpty.text = message
        binding.rvResults.visibility = android.view.View.GONE
    }
}