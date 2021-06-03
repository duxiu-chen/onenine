package me.mucool.testapplication.ui.activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import cn.jpush.android.api.JPushInterface
import com.ywl5320.libenum.SampleRateEnum
import com.ywl5320.libmusic.WlMusic
import kotlinx.android.synthetic.main.activity_service_record.*
import me.mucool.testapplication.R
import me.mucool.testapplication.bean.UserInfoResponse
import me.mucool.testapplication.mvp.contract.user.UserInfoContract
import me.mucool.testapplication.mvp.presenter.user.UserInfoPresenter
import me.mucool.testapplication.ui.fragment.FinishedFragment
import me.mucool.testapplication.utils.BackgroundUtil
import me.mucool.testapplication.utils.IndicatorLineUtil
import me.mucool.testapplication.utils.SharedPreferenceManager

class ServiceRecordActivity : AppCompatActivity(), UserInfoContract.View {

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private lateinit var update : ServiceRecordActivity.UpdateNew

    private lateinit var userInfoPresenter: UserInfoPresenter

    private lateinit var tvTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {


        var wlMusic = WlMusic.getInstance();
        wlMusic.setCallBackPcmData(true);//是否返回音频PCM数据
        wlMusic.setShowPCMDB(true);//是否返回音频分贝大小
        wlMusic.isPlayCircle = false;//循环播放
        wlMusic.volume = 65;//声音大小65%
        wlMusic.playSpeed = 1.0f;//播放速度正常
        wlMusic.playPitch = 1.0f;//播放音调正常
        wlMusic.setConvertSampleRate(SampleRateEnum.RATE_44100);//设定恒定采样率（null为取消）

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_record)

        userInfoPresenter = UserInfoPresenter(this)

        tvTitle = findViewById(R.id.tv_title)
        if (SharedPreferenceManager.getLoginResponse() != null){
            tvTitle.text = SharedPreferenceManager.getLoginResponse().data.mahjongHall.name
        }
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        var fragment1 =  FinishedFragment.getInstance(1)

        viewPagerAdapter.addFragment(fragment1, getString(R.string.string_to_be_processed))
        viewPagerAdapter.addFragment(FinishedFragment.getInstance(2), getString(R.string.string_finished))

        viewPager.adapter = viewPagerAdapter
        tablayout.setupWithViewPager(viewPager)

        tablayout.post {
            IndicatorLineUtil.setIndicator(tablayout, 40, 40)
        }

        update = fragment1

        if (null != intent) {
            if (intent.getBooleanExtra("hasNew", false)){
                viewPager.setCurrentItem(0, true)
                //update.update()
            }
        }

        userCenter.setOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))
        }
        userInfoPresenter.getUserInfo()

        if (!BackgroundUtil.isIgnoringBatteryOptimizations(this))
            BackgroundUtil.requestIgnoreBatteryOptimizations(this)

        var times = SharedPreferenceManager.getOpenTimes()

        if (times%10==0){
            AlertDialog.Builder(this).setTitle("自启动设置").setMessage("为了确保能够收到消息，请将应用设置为自启动！")
                .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which ->

                })
                .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                    if (BackgroundUtil.isXiaomi())
                        BackgroundUtil.goXiaomiSetting(this)
                    if (BackgroundUtil.isHuawei())
                        BackgroundUtil.goHuaweiSetting(this)
                    if (BackgroundUtil.isMeizu())
                        BackgroundUtil.goMeizuSetting(this)
                    if (BackgroundUtil.isOPPO())
                        BackgroundUtil.goOPPOSetting(this)
                    if (BackgroundUtil.isVIVO())
                        BackgroundUtil.goVIVOSetting(this)
                    if (BackgroundUtil.isSamsung())
                        BackgroundUtil.goSamsungSetting(this)

                }).show()
        }
        SharedPreferenceManager.saveOpenTimes(++times)
    }


    class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

        private val fragmentList = mutableListOf<Fragment>()
        private val fragmentTitleList = mutableListOf<String>()

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitleList[position]
        }

        fun addFragment(fragment: Fragment, title: String) {
            fragmentList.add(fragment)
            fragmentTitleList.add(title)
        }
    }

    fun logout(view: View){
        AlertDialog.Builder(this).setMessage("确定退出登陆？")
            .setNegativeButton("取消", DialogInterface.OnClickListener { dialog, which ->

            })
            .setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                JPushInterface.deleteAlias(this, SharedPreferenceManager.getSequence())
                //JPushInterface.cleanTags(this, SharedPreferenceManager.getSequence())
                SharedPreferenceManager.clearUserInfo()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }).show()
    }


    interface UpdateNew{
        fun update()
    }

    override fun getUserInfoSuccess(userInfoResponse: UserInfoResponse?) {
        SharedPreferenceManager.saveLoginResponse(userInfoResponse)
        tvTitle.text = userInfoResponse!!.data.mahjongHall.name
        if (SharedPreferenceManager.getLoginResponse().data.workState == 0)
            JPushInterface.stopPush(application)
    }

    override fun getUserInfoFail(msg: String?) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        if (msg == "token无效！") {
            JPushInterface.deleteAlias(this, SharedPreferenceManager.getSequence())
            //JPushInterface.cleanTags(this, SharedPreferenceManager.getSequence())
            SharedPreferenceManager.clearUserInfo()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

}