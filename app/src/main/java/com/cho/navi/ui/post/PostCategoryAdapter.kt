package com.cho.navi.ui.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.cho.navi.data.Post
import com.cho.navi.databinding.ItemPostCategoryBinding

class PostCategoryAdapter(private val items: List<Post>) :
    RecyclerView.Adapter<PostCategoryAdapter.PostCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCategoryViewHolder {
        return PostCategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PostCategoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PostCategoryViewHolder(
        private val binding: ItemPostCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            val color = post.imageUrl
            val colorInt = color.toColorInt()

            with(binding) {
                ivPostImage.setBackgroundColor(colorInt)
                tvPostTitle.text = post.title
                tvPostDescription.text = post.description
                tvPostLocation.text = post.location
            }
        }

        companion object {
            fun from(parent: ViewGroup): PostCategoryViewHolder {
                return PostCategoryViewHolder(
                    ItemPostCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}