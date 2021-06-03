package me.mucool.testapplication.ui.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {

    protected Handler baseHandler = new Handler();

    Unbinder unbinder;

    protected ProgressDialog progressDialog;

    public boolean mIsDestroy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        unbinder= ButterKnife.bind(this);
    }

    protected abstract int getLayoutId();

    @Override
    protected void onDestroy() {
        mIsDestroy = true;
        unbinder.unbind();
        super.onDestroy();
    }

    public void showWaitingDialog(String msg, boolean cancelable) {
        showLoadingDialogInternal(msg, cancelable, null);
    }

    protected void showLoadingDialogInternal(final CharSequence msg, boolean cancelable,
                                             @Nullable DialogInterface.OnCancelListener cancelListener) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(cancelable);
            progressDialog.setMessage(msg);
        }
        baseHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean isDestroyed = isFinishing();
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
