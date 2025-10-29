package com.dicoding.picodiploma.loginwithanimation.view.story

import android.app.Activity
import android.content.Intent
import android.view.*
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.loginwithanimation.databinding.ItemStoryBinding
import com.dicoding.picodiploma.loginwithanimation.service.response.ListStory
import com.dicoding.picodiploma.loginwithanimation.view.detail.StoryDetailActivity
import java.text.SimpleDateFormat
import java.util.*

class StoryPagingAdapter:
    PagingDataAdapter<ListStory, StoryPagingAdapter.StoryViewHolder>(STORY_DIFF_CALLBACK) {

    companion object {
        val STORY_DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it) }
    }

    class StoryViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ListStory) {
            binding.tvItemName.text = story.name
            binding.tvItemDescription.text = story.description
            binding.tvItemDate.text = story.createdAt?.let { formatStoryDate(it) }

            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(binding.ivItemPhoto)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, StoryDetailActivity::class.java).apply {
                    putExtra(StoryDetailActivity.STORY_ID_EXTRA, story.id)
                }

                val transitionOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    itemView.context as Activity,
                    Pair(binding.ivItemPhoto, "photo"),
                    Pair(binding.tvItemName, "name"),
                    Pair(binding.tvItemDescription, "description"),
                    Pair(binding.tvItemDate, "date")
                )
                itemView.context.startActivity(intent, transitionOptions.toBundle())
            }
        }

        private fun formatStoryDate(isoDate: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

            val parsedDate = inputFormat.parse(isoDate)
            return outputFormat.format(parsedDate ?: Date())
        }
    }
}