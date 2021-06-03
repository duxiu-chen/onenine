package me.mucool.testapplication.mvp.base;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import me.mucool.testapplication.bean.BaseResponse;

public class BaseObserver<T extends BaseResponse> implements Observer<T>{

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
        if (t!=null){
            if (t.getErrorCode().equals("1")&& t.getMsg().equals("token无效！")){

            }
        }else {
            onError(new Throwable("数据为空！"));
        }
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
