package me.mucool.testapplication.mvp.contract.event;

import java.util.List;

import me.mucool.testapplication.bean.BaseResponse;
import me.mucool.testapplication.bean.CallResponse;
import me.mucool.testapplication.mvp.base.BasePresenter;
import me.mucool.testapplication.mvp.base.IBasePresenter;
import me.mucool.testapplication.mvp.base.IBaseView;

public interface CallContract {
    interface View extends IBaseView{
        void getCallListSuccess(CallResponse callResponseList);

        void getCallListFail(String msg);

        void completeCallSuccess(BaseResponse baseResponse);

        void completeCallFail(String msg);

        void responseCallSuccess(BaseResponse baseResponse, int pos);

        void responseCallFail(String msg);

    }

    interface Presenter extends IBasePresenter {
        void getCallList(String phoneNumber, int state);
        void completeCall(String phoneNumber, int id);
        void responseCall(int position, int id);
    }

}
