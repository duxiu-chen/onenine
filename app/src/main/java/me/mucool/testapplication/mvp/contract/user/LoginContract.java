package me.mucool.testapplication.mvp.contract.user;


import me.mucool.testapplication.bean.LoginResponse;
import me.mucool.testapplication.mvp.base.IBasePresenter;
import me.mucool.testapplication.mvp.base.IBaseView;

public interface LoginContract {

    interface View extends IBaseView {

        void mobileLoginSuccess(LoginResponse loginResponse);

        void mobileLoginFail(String msg);

        void mobileLoginIntercept(String msg);

    }

    interface Presenter extends IBasePresenter {

        void mobileLogin(String phoneNumber, String captcha);

    }
}
