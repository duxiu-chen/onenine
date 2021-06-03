package me.mucool.testapplication.mvp.contract.user;

import me.mucool.testapplication.mvp.base.IBasePresenter;
import me.mucool.testapplication.mvp.base.IBaseView;

public interface UserContract {
    interface View extends IBaseView {
        void uploadAvatarSuccess(String url);
        void uploadAvatarFail(String msg);

        void updateStatusSuccess(String msg);
        void updateStatusFail(String msg);

    }

    interface Presenter extends IBasePresenter {
        void uploadAvatar(String base64Data);

        void updateStatus(int receiveCall, int workState);
    }

}
