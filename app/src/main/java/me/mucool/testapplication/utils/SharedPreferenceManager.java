package me.mucool.testapplication.utils;

import android.text.TextUtils;

import com.google.gson.Gson;

import me.mucool.testapplication.App;
import me.mucool.testapplication.bean.UserInfoResponse;

public class SharedPreferenceManager {

    public static final String USER_INO_FILE = "user_info";

    public static void saveToken(String token) {
        SharedPreferencesHelper sph = new SharedPreferencesHelper(App.getContext(),
                USER_INO_FILE);
        sph.put("token", token);
    }

    public static String getToken() {
        SharedPreferencesHelper sph = new SharedPreferencesHelper(App.getContext(),
                USER_INO_FILE);
        return (String) sph.getPreference("token", "");
    }


    public static void clearUserInfo() {
        SharedPreferencesHelper sph = new SharedPreferencesHelper(App.getContext(),
                USER_INO_FILE);
        sph.clear();
    }

    public static void saveUserPhone(String userPhone) {
        SharedPreferencesHelper sph = new SharedPreferencesHelper(App.getContext(),
                USER_INO_FILE);
        sph.put("userPhone", userPhone);
    }

    public static String getUserPhone() {
        SharedPreferencesHelper sph = new SharedPreferencesHelper(App.getContext(),
                USER_INO_FILE);
        return (String) sph.getPreference("userPhone", "");
    }

    public static void saveSequence(int sequence) {
        SharedPreferencesHelper sph = new SharedPreferencesHelper(App.getContext(),
                USER_INO_FILE);
        sph.put("sequence", sequence);
    }

    public static int getSequence() {
        SharedPreferencesHelper sph = new SharedPreferencesHelper(App.getContext(),
                USER_INO_FILE);
        return (int) sph.getPreference("sequence", 0);
    }

    public static void saveLoginResponse(UserInfoResponse loginResponse) {
        if (loginResponse == null) {
            return;
        }
        SharedPreferencesHelper sph = new SharedPreferencesHelper(App.getContext(),
                USER_INO_FILE);
        sph.put("userInfoResponse", new Gson().toJson(loginResponse));
    }

    public static UserInfoResponse getLoginResponse() {
        SharedPreferencesHelper sph = new SharedPreferencesHelper(App.getContext(),
                USER_INO_FILE);
        String str = (String) sph.getPreference("userInfoResponse", "");
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Gson gson = new Gson();
        UserInfoResponse loginResponse = gson.fromJson(str, UserInfoResponse.class);
        return loginResponse;
    }

    public static void saveOpenTimes(int time) {
        SharedPreferencesHelper sph = new SharedPreferencesHelper(App.getContext(),
                USER_INO_FILE);
        sph.put("OpenTimes", time);
    }

    public static int getOpenTimes() {
        SharedPreferencesHelper sph = new SharedPreferencesHelper(App.getContext(),
                USER_INO_FILE);
        return (int) sph.getPreference("OpenTimes", 0);
    }

}
