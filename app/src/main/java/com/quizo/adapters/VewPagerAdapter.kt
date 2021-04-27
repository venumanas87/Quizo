package com.quizo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(supportFragmentManager: FragmentManager) : FragmentStatePagerAdapter(supportFragmentManager) {

        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()
       public var urls:String? = null
        override fun getItem(position: Int): Fragment {
            return mFragmentList.get(position)
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            this.urls = mFragmentTitleList[position]
            return mFragmentTitleList[position]
        }

        fun addFragment(fragment: Fragment, title: String, urli:String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }



    }
