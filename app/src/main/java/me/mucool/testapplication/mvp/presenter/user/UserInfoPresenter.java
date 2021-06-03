package me.mucool.testapplication.mvp.presenter.user;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.mucool.testapplication.bean.BaseResponse;
import me.mucool.testapplication.bean.UserInfoResponse;
import me.mucool.testapplication.mvp.base.BasePresenter;
import me.mucool.testapplication.mvp.contract.user.UserInfoContract;
import me.mucool.testapplication.mvp.model.UserModel;

public class UserInfoPresenter extends BasePresenter<UserInfoContract.View> implements UserInfoContract.Presenter {
    UserModel userModel;
    public UserInfoPresenter(UserInfoContract.View view) {
        super(view);
        userModel = new UserModel();
    }

    @Override
    public void getUserInfo() {
        userModel.getUserInfo(new Observer<BaseResponse<UserInfoResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseResponse<UserInfoResponse> userInfoResponseBaseResponse) {
                if (!isViewAttach()) {
                    return;
                }
                if (userInfoResponseBaseResponse.getSuccess()) {
                    view.getUserInfoSuccess(userInfoResponseBaseResponse.getBody());
                    return;
                }
                view.getUserInfoFail(userInfoResponseBaseResponse.getMsg());
            }

            @Override
            public void onError(Throwable e) {
                view.getUserInfoFail("系统错误，获取用户信息失败");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
