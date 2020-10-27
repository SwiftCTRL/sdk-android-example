package com.swiftctrl.android

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.swiftctrl.android.databinding.ActivityMainBinding
import com.swiftctrl.sdk.connector.SwiftCtrlManualClient
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var adapter: MainPagerAdapter

    private var removeMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Timber.plant(Timber.DebugTree())

        adapter = MainPagerAdapter(this)
        binding.activityMainPager.adapter = adapter
        TabLayoutMediator(binding.activityMainTabs, binding.activityMainPager) { tab, position ->
            tab.text = if( adapter.getItemViewType(position) == Const.TEST_TYPE_MANUAL ) {
                getString(R.string.tab_title_manual, position+1)
            }else{
                getString(R.string.tab_title_auto, position+1)
            }
        }.attach()

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                removeMenuItem?.isVisible = adapter.itemCount > 0
                binding.activityMainEmptyMessage.visibility = if (adapter.itemCount == 0) {
                    VISIBLE
                } else {
                    GONE
                }
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                removeMenuItem?.isVisible = adapter.itemCount > 0
                binding.activityMainEmptyMessage.visibility = if (adapter.itemCount == 0) {
                    VISIBLE
                } else {
                    GONE
                }
            }
        })
        adapter.add()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        removeMenuItem = menu.findItem(R.id.remove_test)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.add_test -> adapter.add()
        R.id.remove_test -> adapter.remove()
        else -> super.onOptionsItemSelected(item)
    }
}
