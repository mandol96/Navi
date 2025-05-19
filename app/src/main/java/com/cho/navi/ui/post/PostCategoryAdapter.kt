package com.cho.navi.ui.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.cho.navi.PostClickListener
import com.cho.navi.data.Post
import com.cho.navi.databinding.ItemPostCategoryBinding

class PostCategoryAdapter(
    private val items: List<Post>,
    private val listener: PostClickListener
) : RecyclerView.Adapter<PostCategoryAdapter.PostCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCategoryViewHolder {
        return PostCategoryViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: PostCategoryViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class PostCategoryViewHolder private constructor(
        private val binding: ItemPostCategoryBinding,
        private val listener: PostClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            val color = post.imageUrl.getOrNull(0) ?: "#757575"
            val colorInt = color.toColorInt()

            itemView.setOnClickListener {
                listener.onPostClick(post)
            }

            with(binding) {
                ivPostImage.setBackgroundColor(colorInt)
                tvPostTitle.text = post.title
                tvPostDescription.text = post.description
                tvPostLocation.text = post.location
            }
        }

        companion object {
            fun from(parent: ViewGroup, listener: PostClickListener): PostCategoryViewHolder {
                return PostCategoryViewHolder(
                    ItemPostCategoryBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ), listener
                )
            }
        }
    }
}