package me.mucool.testapplication.ui.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_privacy.*
import me.mucool.testapplication.R
import me.mucool.testapplication.ui.base.BaseActivity

class PrivacyActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_privacy
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbar!!.setTitleTextColor(resources.getColor(R.color.white))
        toolbar!!.title = "隐私政策"
        toolbar!!.setNavigationIcon(R.drawable.ic_back)
        toolbar!!.setNavigationOnClickListener { finish() }
        webView!!.loadUrl("https://yaojiu.majiangyun.com/jeeplus/a/web/privacy")
    }
}