package com.lunchfood.ui.main.view

import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.lunchfood.R
import com.lunchfood.R.id.homeItem
import com.lunchfood.R.id.locationSettingItem
import com.lunchfood.R.id.eatHistoryItem
import com.lunchfood.R.id.appTalkItem
import com.lunchfood.R.id.myMenuItem
import com.lunchfood.ui.base.BaseActivity
import com.lunchfood.ui.base.GlobalApplication
import com.lunchfood.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity: BaseActivity(TransitionMode.HORIZON) {

    private val fragmentManager: FragmentManager = supportFragmentManager
    private lateinit var transaction: FragmentTransaction
    private lateinit var homeFragment: HomeFragment
    private lateinit var locationSettingFragment: LocationSettingFragment
    private lateinit var historyFragment: HistoryFragment
    private lateinit var appTalkFragment: AppTalkFragment
    private lateinit var myMenuFragment: MyMenuFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupFragments()
    }

    private fun setupFragments() {
        homeFragment = HomeFragment()
        locationSettingFragment = LocationSettingFragment()
        historyFragment = HistoryFragment()
        appTalkFragment = AppTalkFragment()
        myMenuFragment = MyMenuFragment()

        transaction = fragmentManager.beginTransaction()
        val frameLayoutId = R.id.frameLayout
        transaction.replace(frameLayoutId, homeFragment).commitAllowingStateLoss()

        navigationView.setOnNavigationItemSelectedListener {
            transaction = fragmentManager.beginTransaction()
            when(it.itemId) {
                homeItem -> transaction.replace(frameLayoutId, homeFragment)
                        .commitAllowingStateLoss()
                locationSettingItem -> transaction.replace(frameLayoutId, locationSettingFragment)
                        .commitAllowingStateLoss()
                eatHistoryItem -> transaction.replace(frameLayoutId, historyFragment)
                        .commitAllowingStateLoss()
                appTalkItem -> transaction.replace(frameLayoutId, appTalkFragment)
                        .commitAllowingStateLoss()
                myMenuItem -> transaction.replace(frameLayoutId, myMenuFragment)
                        .commitAllowingStateLoss()
            }
            true
        }
    }
}