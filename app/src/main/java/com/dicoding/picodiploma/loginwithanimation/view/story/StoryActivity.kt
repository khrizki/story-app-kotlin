package com.dicoding.picodiploma.loginwithanimation.view.story

import android.content.Intent
import android.os.*
import android.view.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.paging.PagingStateAdapter
import com.dicoding.picodiploma.loginwithanimation.data.user.*
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStoryBinding
import com.dicoding.picodiploma.loginwithanimation.view.addStory.AddStoryActivity
import com.dicoding.picodiploma.loginwithanimation.view.maps.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.viewmodel.ViewModelProviderFactory
import com.dicoding.picodiploma.loginwithanimation.viewmodel.story.StoryViewModel
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class StoryActivity : AppCompatActivity() {
    private val storyViewModel by viewModels<StoryViewModel> {
        ViewModelProviderFactory.getInstance(this)
    }
    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyAdapter: StoryPagingAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyAdapter = StoryPagingAdapter()
        binding.recyclerView.adapter = storyAdapter.withLoadStateFooter(
            footer = PagingStateAdapter {
                storyAdapter.retry()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        sessionManager = SessionManager.getInstance(this.userDataStore)

        observeSession()

        setSupportActionBar(binding.toolbar)
        configureFullScreenMode()

        monitorViewModel()

        binding.fabAddStory.setOnClickListener {
            navigateToAddStoryScreen()
        }
    }

    private fun configureFullScreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            @Suppress("DEPRECATION")
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun observeSession() {
        storyViewModel.getUserSession().observe(this) { user ->
            if (!user.isLogin || user.token.isEmpty()) {
                navigateToWelcomeActivity()
            }
        }
    }


    private fun monitorViewModel() {
        storyViewModel.errorState.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        storyViewModel.pagedStories.observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }
    }


    private fun navigateToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_maps -> {
                navigateToMapScreen()
                true
            }
            R.id.action_logout -> {
                storyViewModel.logout()
                navigateToWelcomeActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun navigateToMapScreen() {
        val intent = Intent(this, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToAddStoryScreen() {
        val intent = Intent(this, AddStoryActivity::class.java)
        startActivity(intent)
    }
}