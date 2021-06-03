package me.mucool.testapplication.mvp.presenter.user;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.mucool.testapplication.bean.BaseResponse;
import me.mucool.testapplication.bean.StringResponse;
import me.mucool.testapplication.mvp.base.BasePresenter;
import me.mucool.testapplication.mvp.contract.user.UserContract;
import me.mucool.testapplication.mvp.model.UserModel;

public class UserPresenter extends BasePresenter<UserContract.View> implements UserContract.Presenter {
    UserModel userModel;
    public UserPresenter(UserContract.View view) {
        super(view);
        userModel = new UserModel();
    }

    @Override
    public void uploadAvatar(String base64Data) {
        userModel.uploadAvatar(base64Data, new Observer<BaseResponse<StringResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseResponse<StringResponse> stringBaseResponse) {
                if (!isViewAttach()) {
                    return;
                }
                if (stringBaseResponse.getSuccess()) {
                    view.uploadAvatarSuccess(stringBaseResponse.getBody().getData());
                    return;
                }
                view.uploadAvatarFail(stringBaseResponse.getMsg());
            }

            @Override
            public void onError(Throwable e) {
                view.uploadAvatarFail("网络请求错误");
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void updateStatus(int receiveCall, int workState) {
        userModel.updateStatus(receiveCall, workState, new Observer<BaseResponse<StringResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseResponse<StringResponse> stringResponseBaseResponse) {
                if (!isViewAttach()) {
                    return;
                }
                if (stringResponseBaseResponse!=null)
                    view.updateStatusSuccess(stringResponseBaseResponse.getMsg());
            }

            @Override
            public void onError(Throwable e) {
                view.updateStatusFail("网络请求错误");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
