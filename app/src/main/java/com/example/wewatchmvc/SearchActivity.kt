package com.example.wewatchmvc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wewatchmvc.controller.SearchController
import com.example.wewatchmvc.databinding.ActivitySearchBinding
import com.example.wewatchmvc.model.Movie
import com.example.wewatchmvc.model.SearchResult
import com.example.wewatchmvc.view.adapter.SearchResultAdapter

class SearchActivity : AppCompatActivity(), SearchController.SearchCallback {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var controller: SearchController
    private lateinit var adapter: SearchResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        controller = SearchController()
        controller.setCallback(this)

        setupRecyclerView()
        setupSearchButton()

        // Автоматический поиск если есть запрос
        val searchQuery = intent.getStringExtra("search_query") ?: ""
        val searchYear = intent.getStringExtra("search_year")

        if (searchQuery.isNotEmpty()) {
            binding.etSearchQuery.setText(searchQuery)
            searchYear?.let { binding.etYear.setText(it) }
            controller.searchMovies(searchQuery, searchYear)
        }
    }

    private fun setupRecyclerView() {
        adapter = SearchResultAdapter(emptyList()) { result: SearchResult ->
            // При клике на результат передаем фильм обратно
            onMovieSelected(result)
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
                controller.searchMovies(query, year.takeIf { it.isNotEmpty() })
            } else {
                Toast.makeText(this, "Введите название фильма", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun onMovieSelected(result: SearchResult) {
        val movie = Movie(
            title = result.title ?: "",
            year = result.year ?: "",
            posterUrl = result.posterUrl ?: "",
            imdbId = result.imdbId ?: ""
        )

        // Возвращаем выбранный фильм в AddActivity
        val intent = Intent().apply {
            putExtra("selected_movie", movie as java.io.Serializable)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    // Callback методы
    override fun onSearchResults(results: List<SearchResult>) {
        adapter.updateResults(results)

        if (results.isEmpty()) {
            binding.tvEmpty.visibility = android.view.View.VISIBLE
            binding.rvResults.visibility = android.view.View.GONE
        } else {
            binding.tvEmpty.visibility = android.view.View.GONE
            binding.rvResults.visibility = android.view.View.VISIBLE
        }
    }

    override fun onLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
    }

    override fun onError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }
}