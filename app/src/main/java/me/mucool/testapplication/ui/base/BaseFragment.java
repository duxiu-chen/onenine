package me.mucool.testapplication.ui.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    protected Handler baseHandler = new Handler();

    protected Context mContext;
    protected View mRoot;
    protected Bundle mBundle;
    protected LayoutInflater mInflater;

    protected ProgressDialog progressDialog;

    protected boolean mIsDestroy;
    Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
        initBundle(mBundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRoot != null) {
            ViewGroup parent = (ViewGroup) mRoot.getParent();
            if (parent != null)
                parent.removeView(mRoot);
        } else {
            mRoot = inflater.inflate(getLayoutId(), container, false);
            mInflater = inflater;
            // Do something
            onBindViewBefore(mRoot);
            // Bind view
            unbinder = ButterKnife.bind(this, mRoot);
            // Get savedInstanceState
            if (savedInstanceState != null)
                onRestartInstance(savedInstanceState);
            // Init
            initWidget(mRoot);
            initData();
        }
        return mRoot;
    }

    protected abstract int getLayoutId();

    protected void initBundle(Bundle bundle) {

    }

    protected void initWidget(View root) {

    }

    protected void initData() {

    }

    protected void onBindViewBefore(View root) {
        // ...
    }

    protected void onRestartInstance(Bundle bundle) {

    }

    @Override
    public void onDestroy() {
        mIsDestroy = true;
        try {
            if (unbinder != null)
                unbinder.unbind();
        } catch (Exception e) {
        }
        mBundle = null;
        super.onDestroy();
    }

    public void showWaitingDialog(String msg, boolean cancelable) {
        showLoadingDialogInternal(msg, cancelable, null);
    }

    protected void showLoadingDialogInternal(final CharSequence msg, boolean cancelable,
                                             @Nullable DialogInterface.OnCancelListener cancelListener) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCancelable(cancelable);
            progressDialog.setMessage(msg);
        }
        baseHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean isDestroyed = getActivity().isFinishing();
                    if (!isDestroyed)
                        progressDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void closeWaitingDialog() {
        Log.d("dialog", getClass() + " closeWaitingDialog ====== " + progressDialog);
        baseHandler.post(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null) {
                    try {
                        progressDialog.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialog = null;
                }
            }
        });
    }


}
