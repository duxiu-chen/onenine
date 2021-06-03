package me.mucool.testapplication.mvp.contract.user;


import me.mucool.testapplication.bean.BaseResponse;
import me.mucool.testapplication.mvp.base.IBasePresenter;
import me.mucool.testapplication.mvp.base.IBaseView;

public interface AuthCodeContract {

    interface View extends IBaseView {

        void authCodeSuccess(String msg);

        void authCodeFail(String msg);

        void authCodeIntercept();

    }

    interface Presenter extends IBasePresenter {

        void authCode(String mobile);

    }
}
