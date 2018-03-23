package com.octo.nickshulhin.ubus.controller

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.octo.nickshulhin.ubus.view.MapViewFragment
import com.octo.nickshulhin.ubus.view.ProfileViewFragment

/**
 * Created by nickshulhin on 23/3/18.
 */
class ViewAdapter(fragmentManager: FragmentManager, val tabs: Int) : FragmentStatePagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                return MapViewFragment()
            }
            1 -> {
                return ProfileViewFragment()
            }
            else -> return null
        }
    }

    override fun getCount(): Int {
        return tabs
    }
}