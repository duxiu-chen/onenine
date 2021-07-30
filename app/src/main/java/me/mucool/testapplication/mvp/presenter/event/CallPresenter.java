package me.mucool.testapplication.mvp.presenter.event;



import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.mucool.testapplication.bean.BaseResponse;
import me.mucool.testapplication.bean.CallResponse;
import me.mucool.testapplication.mvp.base.BasePresenter;
import me.mucool.testapplication.mvp.contract.event.CallContract;
import me.mucool.testapplication.mvp.model.CallModel;

public class CallPresenter extends BasePresenter<CallContract.View> implements CallContract.Presenter {

    CallModel eventModel;

    public CallPresenter(CallContract.View view) {
        super(view);
        eventModel = new CallModel();
    }

    @Override
    public void getCallList(String phoneNumber, int state) {
        eventModel.getCallList(state, new Observer<BaseResponse<CallResponse>>() {
            @Override
            public void onSubscribe(Disposable d) {
                addSubscription(d);
            }

            @Override
            public void onNext(BaseResponse<CallResponse> loginResponseBaseResponse) {
                if (!isViewAttach()) {
                    return;
                }
                if (loginResponseBaseResponse.getSuccess()) {
                    view.getCallListSuccess(loginResponseBaseResponse.getBody());
                    return;
                }
                view.getCallListFail(loginResponseBaseResponse.getMsg());
            }

            @Override
            public void onError(Throwable e) {
                if (!isViewAttach()) {
                    return;
                }
                view.getCallListFail(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void completeCall(String phoneNumber, int id) {
        eventModel.completeCall(id, new Observer<BaseResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                if (!isViewAttach()) {
                    return;
                }
                if (baseResponse.getSuccess()) {
                    view.completeCallSuccess(baseResponse);
                    return;
                }
                view.completeCallFail(baseResponse.getMsg());
            }

            @Override
            public void onError(Throwable e) {
                if (!isViewAttach()) {
                    return;
                }
                view.completeCallFail(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void responseCall(final int position, int id) {
        eventModel.responseCall(id, new Observer<BaseResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                if (!isViewAttach()) {
                    return;
                }
                if (baseResponse.getSuccess()) {
                    view.responseCallSuccess(baseResponse, position);
                    return;
                }
                view.responseCallFail(baseResponse.getMsg());
            }

            @Override
            public void onError(Throwable e) {
                if (!isViewAttach()) {
                    return;
                }
                view.responseCallFail(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
