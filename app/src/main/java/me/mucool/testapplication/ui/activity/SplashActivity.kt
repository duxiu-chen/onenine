package me.mucool.testapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import me.mucool.testapplication.utils.SharedPreferenceManager

class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("Token", SharedPreferenceManager.getToken());
        if (TextUtils.isEmpty(SharedPreferenceManager.getToken()))
            startActivity(Intent(this, LoginActivity::class.java))
        else
            startActivity(Intent(this, ServiceRecordActivity::class.java))
        finish()
    }

}