package com.octo.nickshulhin.ubus

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.View
import com.octo.nickshulhin.ubus.controller.ViewAdapter

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbar()
        setTabLayout()
    }

    fun setViewPager(tabLayout: TabLayout) {
        val viewPager = findViewById<View>(R.id.pager) as ViewPager
        val adapter = ViewAdapter(supportFragmentManager, tabLayout.getTabCount())
        viewPager.adapter = adapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager.currentItem = tab.position
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    fun setTabLayout() {
        val tabLayout = findViewById<View>(R.id.tab_layout) as TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Buses"))
        tabLayout.addTab(tabLayout.newTab().setText("Profile"))
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL)
        setViewPager(tabLayout)
    }

    fun setToolbar() {
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
    }
}
