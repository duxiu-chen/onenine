package me.mucool.testapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import cn.jpush.android.api.JPushInterface
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
import java.util.*

class LoginActivity : BaseActivity(), AuthCodeContract.View, LoginContract.View {
    var authCodePresenter: AuthCodePresenter? = null
    var loginPresenter: LoginPresenter? = null
    var timer: CountDownTimer? = null
    var phone:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authCodePresenter = AuthCodePresenter(this)
        loginPresenter = LoginPresenter(this)

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
        JPushInterface.setAlias(this, phone.hashCode(), phone)
        startActivity(Intent(this, ServiceRecordActivity::class.java))
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

}
