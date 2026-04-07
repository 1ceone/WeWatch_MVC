package com.example.wewatchmvc

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wewatch.controller.SearchController
import com.example.wewatch.model.Movie
import com.example.wewatch.model.SearchResult
import com.example.wewatch.view.adapter.SearchResultAdapter
import java.io.Serializable

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

        val searchQuery = intent.getStringExtra("search_query") ?: ""
        val searchYear = intent.getStringExtra("search_year")

        if (searchQuery.isNotEmpty()) {
            controller.searchMovies(searchQuery, searchYear)
        }
    }

    private fun setupRecyclerView() {
        adapter = SearchResultAdapter(emptyList()) { result: SearchResult ->
            onMovieSelected(result)
        }

        binding.rvResults.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
            adapter = this@SearchActivity.adapter
        }
    }

    private fun onMovieSelected(result: SearchResult) {
        val movie = Movie(
            title = result.title ?: "",
            year = result.year ?: "",
            posterUrl = result.posterUrl ?: "",
            imdbId = result.imdbId ?: ""
        )

        val intent = Intent().apply {
            putExtra("selected_movie", movie as Serializable)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    // Callback методы
    override fun onSearchResults(results: List<SearchResult>) {
        adapter.updateResults(results)

        if (results.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvResults.visibility = View.GONE
        } else {
            binding.tvEmpty.visibility = View.GONE
            binding.rvResults.visibility = View.VISIBLE
        }
    }

    override fun onLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }
}