package com.cho.navi.ui.post

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.cho.navi.data.Category

class TabAdapter(
    fragment: Fragment,
    private val categories: List<Category>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun createFragment(position: Int): Fragment {
        return PostCategoryFragment.newInstance(categories[position])
    }
}