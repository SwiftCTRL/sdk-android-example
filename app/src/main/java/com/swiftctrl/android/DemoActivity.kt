package com.swiftctrl.android

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import com.swiftctrl.android.databinding.ActivityDemoBinding

class DemoActivity : AppCompatActivity() {

    lateinit var binding: ActivityDemoBinding
    lateinit var adapter: MainPagerAdapter

    private var removeMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun showQrCode(userToken: String) {
        val fragment = QrFragment().apply {
            this.arguments = bundleOf(Const.KEY_USER_TOKEN to userToken)
        }
        supportFragmentManager.beginTransaction().replace(R.id.activity_demo_root, fragment).commit()
    }
}
