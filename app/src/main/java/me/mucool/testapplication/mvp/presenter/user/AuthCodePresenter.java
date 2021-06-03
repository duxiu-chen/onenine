package me.mucool.testapplication.mvp.presenter.user;


import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.mucool.testapplication.bean.BaseResponse;
import me.mucool.testapplication.mvp.base.BasePresenter;
import me.mucool.testapplication.mvp.contract.user.AuthCodeContract;
import me.mucool.testapplication.mvp.model.UserModel;

public class AuthCodePresenter extends BasePresenter<AuthCodeContract.View> implements AuthCodeContract.Presenter {

    UserModel userModel;

    public AuthCodePresenter(AuthCodeContract.View view) {
        super(view);
        userModel = new UserModel();
    }

    @Override
    public void authCode(String mobile) {
        userModel.authCode(mobile, new Observer<BaseResponse>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscription(d);
            }

            @Override
            public void onNext(BaseResponse authCodeResponseBaseResponse) {
                if (!isViewAttach()) {
                    return;
                }
                if (authCodeResponseBaseResponse.getSuccess()) {
                    view.authCodeSuccess(authCodeResponseBaseResponse.getMsg());
                    return;
                }
                view.authCodeFail(authCodeResponseBaseResponse.getMsg());
            }

            @Override
            public void onError(Throwable e) {
                if (!isViewAttach()) {
                    return;
                }
                view.authCodeFail(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
