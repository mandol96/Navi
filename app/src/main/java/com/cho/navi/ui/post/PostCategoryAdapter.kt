package com.cho.navi.ui.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cho.navi.PostClickListener
import com.cho.navi.R
import com.cho.navi.data.Post
import com.cho.navi.databinding.ItemPostCategoryBinding

class PostCategoryAdapter(
    private val listener: PostClickListener
) : ListAdapter<Post, PostCategoryAdapter.PostCategoryViewHolder>(PostCategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostCategoryViewHolder {
        return PostCategoryViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: PostCategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostCategoryViewHolder private constructor(
        private val binding: ItemPostCategoryBinding,
        private val listener: PostClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            itemView.setOnClickListener {
                listener.onPostClick(post)
            }

            with(binding) {
                val imageUrl = post.imageUrls.first()

                Glide.with(ivPostImage.context)
                    .load(imageUrl)
                    .placeholder(R.color.orange_700)
                    .error(R.color.black)
                    .centerCrop()
                    .into(ivPostImage)

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

class PostCategoryDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}