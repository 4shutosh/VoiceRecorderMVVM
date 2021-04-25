package com.task.bbrassignment.adapter

import android.util.SparseArray
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import dagger.hilt.android.scopes.ActivityScoped
import java.lang.ref.WeakReference
import javax.inject.Inject

@ActivityScoped
class TabAdapter @Inject constructor(
    supportFragmentManager: FragmentManager,
    private val fragmentTitleList: List<String>
) : FragmentPagerAdapter(supportFragmentManager) {

    private val instantiatedFragments = SparseArray<WeakReference<Fragment>>()
    private var fragmentList: MutableList<Fragment> = mutableListOf()

    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        instantiatedFragments.put(position, WeakReference(fragment))
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        instantiatedFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }
}