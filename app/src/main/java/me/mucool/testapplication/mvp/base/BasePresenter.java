package me.mucool.testapplication.mvp.base;

import androidx.annotation.NonNull;

import java.util.LinkedList;

import io.reactivex.disposables.Disposable;

public class BasePresenter<V extends IBaseView> implements IBasePresenter {

    protected V view;

    private final LinkedList<Disposable> registerSubscriptions = new LinkedList<>();

    public BasePresenter(V view) {
        this.view = view;
    }

    @Override
    public boolean isViewAttach() {
        return view != null;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    protected void addSubscription(Disposable disposable) {
        synchronized (registerSubscriptions) {
            registerSubscriptions.add(disposable);
        }
    }

    protected boolean removeSubscription(@NonNull Disposable disposable) {
        synchronized (registerSubscriptions) {
            return registerSubscriptions.remove(disposable);
        }
    }

    protected boolean cancelSubscription(@NonNull Disposable disposable) {
        if (!disposable.isDisposed()) disposable.dispose();
        return removeSubscription(disposable);
    }

    @Override
    public void destroy() {
        detachView();
        if (registerSubscriptions.isEmpty()) {
            return;
        }
        synchronized (registerSubscriptions) {
            for (Disposable disposable : registerSubscriptions) {
                if (!disposable.isDisposed()) {
                    disposable.dispose();
                }
            }
            registerSubscriptions.clear();
        }
    }

}
