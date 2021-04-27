package com.quizo.activity

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.quizo.*
import com.quizo.fragments.*

class StoryFullActivity:AppCompatActivity() {
    var tabLayout:TabLayout? = null
    var viewPager:ViewPager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stories_full)
        window.statusBarColor = resources.getColor(R.color.grey)
        tabLayout = findViewById(R.id.tabs)
        viewPager = findViewById(R.id.viewpager)
        tabLayout!!.setupWithViewPager(viewPager)
        val adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFragment(StoryFragment(),"Trending","okay")
        adapter.addFragment(HumourFragment(),"Humour","okay")
        adapter.addFragment(HorrorFragment(),"Horror","okay")
        adapter.addFragment(FantasyFragment(),"Fantasy","okay")
        adapter.addFragment(ScifiFragment(),"Science Fiction","okay")
        adapter.addFragment(AltHisFragment(),"Alternate History","okay")
        adapter.addFragment(CyberpunkFragment(),"Cyberpunk","okay")
        adapter.addFragment(HistoricalFragment(),"Historical","okay")
        adapter.addFragment(SearchFragment(),"Search","okay")
        viewPager?.adapter = adapter
        val search:ImageView = findViewById(R.id.search)
        search.setOnClickListener {
            viewPager?.setCurrentItem(8,true)
        }

        val back:ImageView = findViewById(R.id.back)
        back.setOnClickListener {
            finishAfterTransition()
        }


    }
}