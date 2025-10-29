package com.dicoding.picodiploma.loginwithanimation.data.paging

import android.view.*
import androidx.core.view.isVisible
import androidx.paging.*
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.picodiploma.loginwithanimation.databinding.FooterPagingBinding

class PagingStateAdapter(private val onRetry: () -> Unit) : LoadStateAdapter<PagingStateAdapter.PagingStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): PagingStateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FooterPagingBinding.inflate(inflater, parent, false)
        return PagingStateViewHolder(binding, onRetry)
    }

    override fun onBindViewHolder(holder: PagingStateViewHolder, loadState: LoadState) {
        holder.bindState(loadState)
    }

    class PagingStateViewHolder(
        private val binding: FooterPagingBinding,
        private val onRetry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButtonPaging.setOnClickListener { onRetry.invoke() }
        }

        fun bindState(state: LoadState) {
            if (state is LoadState.Error) {
                binding.errorMessage.text = state.error.localizedMessage
            }
            binding.progressBarFooter.isVisible = state is LoadState.Loading
            binding.retryButtonPaging.isVisible = state is LoadState.Error
            binding.errorMessage.isVisible = state is LoadState.Error
        }
    }
}
