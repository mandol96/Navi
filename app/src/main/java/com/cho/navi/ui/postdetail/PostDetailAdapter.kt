package com.cho.navi.ui.postdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.cho.navi.databinding.ItemPostDetailImageBinding

class PostDetailAdapter
    : RecyclerView.Adapter<PostDetailAdapter.PostDetailViewHolder>() {

    private val items = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostDetailViewHolder {
        return PostDetailViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PostDetailViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addImages(images: List<String>) {
        items.clear()
        items.addAll(images)
        notifyDataSetChanged()
    }

    class PostDetailViewHolder private constructor(
        private val binding: ItemPostDetailImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(image: String) {
            val colorInt = image.toColorInt()
            binding.ivPostDetailImage.setBackgroundColor(colorInt)
        }

        companion object {
            fun from(parent: ViewGroup): PostDetailViewHolder {
                return PostDetailViewHolder(
                    ItemPostDetailImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }
}