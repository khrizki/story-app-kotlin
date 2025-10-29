package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStoryBinding
import com.dicoding.picodiploma.loginwithanimation.viewmodel.ViewModelProviderFactory
import com.dicoding.picodiploma.loginwithanimation.viewmodel.detail.StoryDetailViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StoryDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    private val detailViewModel by viewModels<StoryDetailViewModel> {
        ViewModelProviderFactory.getInstance(this)
    }

    companion object {
        const val STORY_ID_EXTRA = "STORY_ID_EXTRA"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(STORY_ID_EXTRA)
        storyId?.let {
            lifecycleScope.launch {
                detailViewModel.fetchStoryDetail(it)
            }
        }

        observeViewModel()
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel() {
        detailViewModel.loadingState.observe(this) { isLoading ->
            binding.progressBarDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        detailViewModel.errorState.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }

        detailViewModel.storyDetail.observe(this) { detailStory ->
            if (detailStory != null) {
                binding.tvDetailName.text = detailStory.story?.name
                binding.tvDetailDescription.text = detailStory.story?.description
                binding.storyCreatedAt.text = detailStory.story?.createdAt?.let { formatStoryDate(it) }

                Glide.with(this)
                    .load(detailStory.story?.photoUrl)
                    .apply(RequestOptions().transform(RoundedCorners(14)))
                    .into(binding.ivDetailPhoto)
            }
        }
    }

    private fun formatStoryDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

        val parsedDate = inputFormat.parse(dateString)
        return outputFormat.format(parsedDate ?: Date())
    }
}
