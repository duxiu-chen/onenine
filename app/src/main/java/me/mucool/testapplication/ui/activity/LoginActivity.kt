package me.mucool.testapplication.ui.activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.view.View
import android.view.Window
import android.widget.TextView
import cn.jpush.android.api.JPushInterface
import com.tencent.bugly.Bugly
import kotlinx.android.synthetic.main.activity_login.*
import me.mucool.testapplication.App
import me.mucool.testapplication.R
import me.mucool.testapplication.bean.LoginResponse
import me.mucool.testapplication.mvp.contract.user.AuthCodeContract
import me.mucool.testapplication.mvp.contract.user.LoginContract
import me.mucool.testapplication.mvp.presenter.user.AuthCodePresenter
import me.mucool.testapplication.mvp.presenter.user.LoginPresenter
import me.mucool.testapplication.ui.base.BaseActivity
import me.mucool.testapplication.utils.SharedPreferenceManager
import me.mucool.testapplication.utils.SharedPreferencesHelper
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.UnsupportedEncodingException


class LoginActivity : BaseActivity(), AuthCodeContract.View, LoginContract.View {
    var authCodePresenter: AuthCodePresenter? = null
    var loginPresenter: LoginPresenter? = null
    var timer: CountDownTimer? = null
    var phone:String? = null

    private var sph: SharedPreferencesHelper? = null
    val IS_FIRST = "first_open"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authCodePresenter = AuthCodePresenter(this)
        loginPresenter = LoginPresenter(this)

        sph = SharedPreferencesHelper(App.getContext(), "OPEN_INFO")
        val is_first = sph!!.getPreference(IS_FIRST, true) as Boolean
        if (is_first) openDialog()

    }

    override fun getLayoutId(): Int {
        return  R.layout.activity_login
    }

    override fun authCodeIntercept() {
        closeWaitingDialog()
        getVerifCodeView.isEnabled = true
        App.getContext().showMessage("验证码发送失败")
    }

    override fun authCodeFail(msg: String?) {
        closeWaitingDialog()
        getVerifCodeView.isEnabled = true
        App.getContext().showMessage(msg)
    }

    override fun authCodeSuccess(msg: String?) {
        closeWaitingDialog()
        App.getContext().showMessage("验证码发送成功")
        startCountDown()
    }

    override fun mobileLoginFail(msg: String?) {
        closeWaitingDialog()
        login.isEnabled = true
        App.getContext().showMessage("登陆失败：$msg")
    }

    override fun mobileLoginSuccess(loginResponse: LoginResponse?) {
        closeWaitingDialog()
        SharedPreferenceManager.saveToken(loginResponse!!.data.token)//存储token
        SharedPreferenceManager.saveUserPhone(phone)//存储手机号
        SharedPreferenceManager.saveSequence(phone.hashCode())
        //设置极光推送tag
        setAlias()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun mobileLoginIntercept(msg: String?) {
        closeWaitingDialog()
        login.isEnabled = true
        App.getContext().showMessage("登陆失败：$msg")
    }

    override fun onDestroy() {
        cancelTimer()
        authCodePresenter!!.destroy()
        loginPresenter!!.destroy()
        super.onDestroy()
    }

    /**
     * 获取验证码
     */
    fun getVerifCodeViewClick(view: View) {
        val phone: String = edit_phone.text.toString().trim()
        if (TextUtils.isEmpty(phone)) {
            App.getContext().showMessage("请输入手机号")
            return
        }
        if (phone.length != 11) {
            App.getContext().showMessage("请输入11位手机号")
            return
        }
        getVerifCodeView.isEnabled = false
        showWaitingDialog("请求发送验证码", false)
        authCodePresenter!!.authCode(phone)
    }

    fun login(view: View){
        phone = edit_phone.text.toString().trim()
        val captcha: String = edit_pwd.text.toString().trim()
        if (TextUtils.isEmpty(phone)) {
            App.getContext().showMessage("请输入手机号")
            return
        }
        if (phone!!.length != 11) {
            App.getContext().showMessage("请输入11位手机号")
            return
        }
        if (TextUtils.isEmpty(captcha)) {
            App.getContext().showMessage("请输入验证码")
            return
        }
        login.isEnabled = false
        showWaitingDialog("登陆中...", false)
        loginPresenter!!.mobileLogin(phone, captcha)
    }

    private fun startCountDown() {
        timer = object : CountDownTimer(60500, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val s = (millisUntilFinished / 1000 + 0.1).toInt()
                val ss = "($s s)    "
                getVerifCodeView.text = ss
            }

            override fun onFinish() {
                enableVerifCode()
            }
        }
        timer!!.start()
    }

    private fun cancelTimer() {
        if (timer != null) {
            timer!!.cancel()
        }
    }

    private fun enableVerifCode() {
        if (!mIsDestroy) {
            getVerifCodeView.text = "获取验证码"
            getVerifCodeView.isEnabled = true
        }
    }


    private fun setAlias() {
        val url = "https://device.jpush.cn/v3/aliases/${phone}"
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .addHeader("Authorization","Basic "+getBase64("ff2faad87a10cced246c6782:a7fc66202d093f1a0c6d96e9"))
            .get() //默认就是GET请求，可以不写
            .build()
        val call: Call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {

            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response) {
                var dataString = response.body()!!.string()
                var data = JSONObject(dataString)
                var arrays = data.getJSONArray("registration_ids")
                if (arrays.length()>7){
                    deleteAlias(arrays)
                }else{
                    JPushInterface.setAlias(this@LoginActivity, phone.hashCode(), phone)
                }
            }
        })
    }

    private fun deleteAlias(array : JSONArray) {
        val json = JSONObject()
        var registration = JSONObject()
        registration.put("remove", array)
        json.put("registration_ids", registration)
        val body = RequestBody.create(MediaType.parse("application/json"), json.toString())
        val request: Request = Request.Builder()
            .url("https://device.jpush.cn/v3/aliases/${phone}")
            .addHeader("Authorization","Basic "+getBase64("ff2faad87a10cced246c6782:a7fc66202d093f1a0c6d96e9"))
            .post(body)
            .build()
        val call: Call = OkHttpClient().newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {

            }

            @Throws(IOException::class)
            override fun onResponse(call: Call?, response: Response) {
                if (response.code()==200){
                    JPushInterface.setAlias(this@LoginActivity, phone.hashCode(), phone)
                }
            }
        })
    }

    fun getBase64(str: String?): String? {
        var result = ""
        if (str != null) {
            try {
                result = String(Base64.encode(str.toByteArray(charset("utf-8")), Base64.NO_WRAP))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
            }
        }
        return result
    }


    open fun openDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_privacy)
        dialog.setCancelable(false)
        dialog.show()
        val tv = dialog.findViewById<TextView>(R.id.tvContent)
        val style = SpannableStringBuilder()
        //设置文字
        style.append("请你务必审慎阅读、充分理解“服务协议”和“隐私政策”的各项条款，包括但不限于：为了向你提供上传视频、图片，消息推送服务，我们需要收集你的设备信息，操作日志等个人信息。你可以在“设置”里查看、变更、删除个人信息并管理你的授权。\n你可阅读《服务协议》和《隐私政策》了解详细信息。如果你同意，请点击“同意”开始接受我们的服务。")
        //设置部分文字点击事件
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@LoginActivity, PrivacyActivity::class.java))
            }
        }
        style.setSpan(clickableSpan, 117, 130, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        tv.text = style
        //设置部分文字颜色
        val foregroundColorSpan = ForegroundColorSpan(Color.parseColor("#18B6FF"))
        style.setSpan(foregroundColorSpan, 117, 130, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        //配置给TextView
        tv.movementMethod = LinkMovementMethod.getInstance()
        tv.text = style
        dialog.findViewById<View>(R.id.tv_agree).setOnClickListener {
            sph!!.put(IS_FIRST, false)

            Bugly.init(applicationContext, "0b7b28eb56", false)

            JPushInterface.setDebugMode(true)
            JPushInterface.init(this)

            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.tv_disagree).setOnClickListener {
            System.exit(
                0
            )
        }
    }


}
