package com.example.appcontrol

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
   private val FRAGMENT_HOME = 0
   private val FRAGMENT_FAVOR = 1
   private val FRAGMENT_HISTORY = 2
   private var mCurrentFragment = FRAGMENT_HOME
   private lateinit var mDrawerLayout: DrawerLayout

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.activity_main)
      val toolbar: Toolbar = findViewById(R.id.toolbar)
      setSupportActionBar(toolbar)
      supportActionBar?.title = "ĐIỀU KHIỂN CHONG ĐÈN"
      mDrawerLayout = findViewById(R.id.drawer_layout)
      val toggle = ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close)
      mDrawerLayout.addDrawerListener(toggle)
      toggle.syncState()
      val navigationView: NavigationView = findViewById(R.id.design_navigation_view)
      navigationView.setNavigationItemSelectedListener(this)
      replaceFragment(GardenFragment())
      navigationView.menu.findItem(R.id.nav_home1).isChecked = true
   }

   override fun onNavigationItemSelected(item: MenuItem): Boolean {
      val id = item.itemId
      when (id) {
         R.id.nav_home1 -> {
            if (mCurrentFragment != FRAGMENT_HOME) {
               replaceFragment(GardenFragment())
               mCurrentFragment = FRAGMENT_HOME
            }
         }
         R.id.nav_home2 -> {
            if (mCurrentFragment != FRAGMENT_FAVOR) {
               replaceFragment(GardenFragment1())
               mCurrentFragment = FRAGMENT_FAVOR
            }
         }
         R.id.nav_add -> {
            addNewGarden()
         }
         else -> {
            if (item.groupId == R.id.dynamic_group) {
               // Load the GardenFragment for dynamically added items
               replaceFragment(GardenFragment())
            }
         }
      }
      mDrawerLayout.closeDrawer(GravityCompat.START)
      return true
   }

   private fun addNewGarden() {
      val navigationView: NavigationView = findViewById(R.id.design_navigation_view)
      val menu = navigationView.menu
      val gardenCount = menu.size() - 3 // Subtract 3 for the existing static items
      menu.add(R.id.dynamic_group, Menu.NONE, gardenCount, "Garden ${gardenCount + 3}")
         .setIcon(R.drawable.ic_home)
         .isCheckable = true
   }

   override fun onBackPressed() {
      if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
         mDrawerLayout.closeDrawer(GravityCompat.START)
      } else {
         finish()
      }
   }

   private fun replaceFragment(fragment: Fragment) {
      val transaction = supportFragmentManager.beginTransaction()
      transaction.replace(R.id.content_frame, fragment)
      transaction.commit()
   }


}