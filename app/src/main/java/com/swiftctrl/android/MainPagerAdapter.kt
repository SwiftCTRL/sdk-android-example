package com.swiftctrl.android

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private var fragmentCount = 0

    override fun createFragment(position: Int): Fragment {
        return with(TestFragment()) {
            this.arguments = bundleOf(Const.KEY_INDEX to position, Const.KEY_TYPE to getItemViewType(position))
            this
        }
    }

    override fun getItemCount(): Int = fragmentCount

    fun add(): Boolean {
        fragmentCount++
        notifyItemInserted(fragmentCount - 1)
        return true
    }

    override fun getItemViewType(position: Int): Int = if (position == 0) {
        Const.TEST_TYPE_MANUAL
    } else {
        Const.TEST_TYPE_AUTO
    }

    fun remove(): Boolean {
        fragmentCount--
        notifyItemRemoved(fragmentCount)
        return true
    }
}
