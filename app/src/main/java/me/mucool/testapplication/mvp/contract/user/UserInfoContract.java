package me.mucool.testapplication.mvp.contract.user;

import me.mucool.testapplication.bean.UserInfoResponse;
import me.mucool.testapplication.mvp.base.IBasePresenter;
import me.mucool.testapplication.mvp.base.IBaseView;

public interface UserInfoContract {
    interface View extends IBaseView{
        void getUserInfoSuccess(UserInfoResponse userInfoResponse);
        void getUserInfoFail(String msg);
    }
    interface Presenter extends IBasePresenter{
        void getUserInfo();
    }
}
