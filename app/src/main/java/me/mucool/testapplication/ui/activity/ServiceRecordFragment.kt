package me.mucool.testapplication.ui.activity

import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import cn.jpush.android.api.JPushInterface
import com.google.android.material.tabs.TabLayout
import com.ywl5320.libenum.MuteEnum
import com.ywl5320.libenum.SampleRateEnum
import com.ywl5320.libmusic.WlMusic
import me.mucool.testapplication.App
import me.mucool.testapplication.R
import me.mucool.testapplication.bean.UserInfoResponse
import me.mucool.testapplication.mvp.contract.user.UserInfoContract
import me.mucool.testapplication.mvp.presenter.user.UserInfoPresenter
import me.mucool.testapplication.ui.base.BaseFragment
import me.mucool.testapplication.ui.fragment.FinishedFragment
import me.mucool.testapplication.utils.BackgroundUtil
import me.mucool.testapplication.utils.SharedPreferenceManager
import me.mucool.testapplication.utils.VoicePlay


class ServiceRecordFragment : BaseFragment(), UserInfoContract.View {

    private lateinit var viewPagerAdapter: ViewPagerAdapter

    private lateinit var update : ServiceRecordFragment.UpdateNew

    private lateinit var userInfoPresenter: UserInfoPresenter

    private lateinit var tvTitle: TextView

    private lateinit var fragment1: FinishedFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        var wlMusic = WlMusic.getInstance();
//        wlMusic.setCallBackPcmData(true);//是否返回音频PCM数据
//        wlMusic.setShowPCMDB(true);//是否返回音频分贝大小
//        wlMusic.isPlayCircle = false;//循环播放
//        wlMusic.volume = 65;//声音大小65%
//        wlMusic.playSpeed = 1.0f;//播放速度正常
//        wlMusic.playPitch = 1.0f;//播放音调正常
//        wlMusic.setMute(MuteEnum.MUTE_CENTER);
//        wlMusic.setConvertSampleRate(SampleRateEnum.RATE_44100);//设定恒定采样率（null为取消）


        userInfoPresenter = UserInfoPresenter(this)

        tvTitle = mRoot.findViewById(R.id.tv_title)
        if (SharedPreferenceManager.getLoginResponse() != null){
            tvTitle.text = SharedPreferenceManager.getLoginResponse().data.mahjongHall.name
        }
        viewPagerAdapter = ViewPagerAdapter(childFragmentManager)

        fragment1 =  FinishedFragment.getInstance(1)

        viewPagerAdapter.addFragment(fragment1, getString(R.string.string_to_be_processed))
        viewPagerAdapter.addFragment(FinishedFragment.getInstance(3), getString(R.string.string_process))
        viewPagerAdapter.addFragment(FinishedFragment.getInstance(2), getString(R.string.string_finished))

        var viewPager = mRoot.findViewById<ViewPager>(R.id.viewPager)
        var tablayout = mRoot.findViewById<TabLayout>(R.id.tablayout);

        viewPager.adapter = viewPagerAdapter
        tablayout.setupWithViewPager(viewPager)

        tablayout.post {
            //IndicatorLineUtil.setIndicator(tablayout, 20, 20)
        }

        update = fragment1

        userInfoPresenter.getUserInfo()

        if (!BackgroundUtil.isIgnoringBatteryOptimizations(mContext))
            BackgroundUtil.requestIgnoreBatteryOptimizations(mContext)

        var times = SharedPreferenceManager.getOpenTimes()

        if (times%10==0){

            var dialog = Dialog(mContext)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_custom_info)
            dialog.setCancelable(true)
            var layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window!!.attributes)
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            dialog.findViewById<AppCompatButton>(R.id.app_btn_cancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.findViewById<AppCompatButton>(R.id.app_btn_start).setOnClickListener {
                if (BackgroundUtil.isXiaomi())
                    BackgroundUtil.goXiaomiSetting(activity)
                if (BackgroundUtil.isHuawei())
                    BackgroundUtil.goHuaweiSetting(activity)
                if (BackgroundUtil.isMeizu())
                    BackgroundUtil.goMeizuSetting(activity)
                if (BackgroundUtil.isOPPO())
                    BackgroundUtil.goOPPOSetting(activity)
                if (BackgroundUtil.isVIVO())
                    BackgroundUtil.goVIVOSetting(activity)
                if (BackgroundUtil.isSamsung())
                    BackgroundUtil.goSamsungSetting(activity)
                dialog.dismiss()
            }
            dialog.show()
            dialog.window!!.attributes = layoutParams
        }
        SharedPreferenceManager.saveOpenTimes(++times)

        val filter = IntentFilter()
        filter.addAction("refresh")
        context?.registerReceiver(mBroadcastReceiver, filter)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_service_record
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

    interface UpdateNew{
        fun update()
    }

    override fun getUserInfoSuccess(userInfoResponse: UserInfoResponse?) {
        SharedPreferenceManager.saveLoginResponse(userInfoResponse)
        tvTitle.text = userInfoResponse!!.data.mahjongHall.name
        if (SharedPreferenceManager.getLoginResponse().data.workState == 0)
            JPushInterface.stopPush(mContext)
    }

    override fun getUserInfoFail(msg: String?) {
        App.getContext().showMessage(msg)
        if (msg == "token无效！") {
            JPushInterface.deleteAlias(mContext, SharedPreferenceManager.getSequence())
            //JPushInterface.cleanTags(this, SharedPreferenceManager.getSequence())
            SharedPreferenceManager.clearUserInfo()
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            activity?.finish()
        }
    }


    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) //onReceive函数不能做耗时的事情，参考值：10s以内
        {
            val action = intent.action
            if (action == "refresh") {
                fragment1.update()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(mBroadcastReceiver)
        VoicePlay.getInstance(mContext).stop()
        VoicePlay.getInstance(mContext).release()
    }

}