package me.mucool.testapplication.mvp.presenter.user;



import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.mucool.testapplication.bean.BaseResponse;
import me.mucool.testapplication.bean.LoginResponse;
import me.mucool.testapplication.mvp.base.BasePresenter;
import me.mucool.testapplication.mvp.contract.user.LoginContract;
import me.mucool.testapplication.mvp.model.UserModel;

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter {

    UserModel userModel;

    public LoginPresenter(LoginContract.View view) {
        super(view);
        userModel = new UserModel();
    }

    @Override
    public void mobileLogin(String phoneNumber, String captcha) {
        userModel.mobileLogin(phoneNumber, captcha, new Observer<BaseResponse<LoginResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscription(d);
            }

            @Override
            public void onNext(BaseResponse<LoginResponse> loginResponseBaseResponse) {
                if (!isViewAttach()) {
                    return;
                }
                if (loginResponseBaseResponse.getSuccess()) {
                    view.mobileLoginSuccess(loginResponseBaseResponse.getBody());
                    return;
                }
                view.mobileLoginFail(loginResponseBaseResponse.getMsg());
            }

            @Override
            public void onError(Throwable e) {
                if (!isViewAttach()) {
                    return;
                }
                view.mobileLoginFail("登陆失败，请稍后重试");
            }

            @Override
            public void onComplete() {

            }
        });



    }
}
