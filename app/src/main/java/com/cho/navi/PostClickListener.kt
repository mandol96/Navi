package com.cho.navi

import com.cho.navi.data.Category
import com.cho.navi.data.Post

interface PostClickListener {

    fun onPostClick(post: Post)
}