package com.cho.navi.ui.recommendation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cho.navi.data.Post
import com.cho.navi.databinding.ItemRecommendationPostBinding
import com.cho.navi.ui.extensions.load

class RecommendationPostAdapter :
    RecyclerView.Adapter<RecommendationPostAdapter.RecommendationPostViewHolder>() {

    private val items = mutableListOf<Post>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendationPostViewHolder {
        return RecommendationPostViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecommendationPostViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(newItems: List<Post>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class RecommendationPostViewHolder private constructor(
        private val binding: ItemRecommendationPostBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.tvRecommendationTitle.text = post.title
            binding.ivRecommendationImage.load(post.imageUrls.first())
        }

        companion object {
            fun from(parent: ViewGroup): RecommendationPostViewHolder {
                return RecommendationPostViewHolder(
                    ItemRecommendationPostBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}