package me.mucool.testapplication.ui.activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.bugly.beta.Beta;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import de.hdodenhof.circleimageview.CircleImageView;
import me.mucool.testapplication.App;
import me.mucool.testapplication.R;
import me.mucool.testapplication.bean.UserInfoResponse;
import me.mucool.testapplication.mvp.contract.user.UserContract;
import me.mucool.testapplication.mvp.presenter.user.UserPresenter;
import me.mucool.testapplication.ui.base.BaseFragment;
import me.mucool.testapplication.utils.BackgroundUtil;
import me.mucool.testapplication.utils.GlideEngine;
import me.mucool.testapplication.utils.MyObjectTypeAdaptor;
import me.mucool.testapplication.utils.SharedPreferenceManager;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.content.Intent.ACTION_DELETE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class UserFragment extends BaseFragment implements UserContract.View {

    UserPresenter userPresenter;
    private AlertDialog alertDialog;
    private AlertDialog mDialog;
    private static final int NOT_NOTICE = 2;//如果勾选了不再询问
    public static final int CHOOSE_LOGO = 10;

    private UserInfoResponse userInfoResponse;

    private SwitchCompat switcherPush;
    private SwitchCompat switcherWork;
    private CircleImageView avatar;

    private TextView btnExit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        userInfoResponse = SharedPreferenceManager.getLoginResponse();
        switcherPush = mRoot.findViewById(R.id.switcherPush);
        switcherWork = mRoot.findViewById(R.id.switcherWork);
        avatar = mRoot.findViewById(R.id.avatar);
        btnExit = mRoot.findViewById(R.id.btnExit);
        TextView tvVersion = mRoot.findViewById(R.id.tv_version);
        TextView tvUserName = mRoot.findViewById(R.id.tv_username);
        TextView tvPhone = mRoot.findViewById(R.id.tv_phone);
        mRoot.findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mRoot.findViewById(R.id.tvMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PrivacyActivity.class));
            }
        });

        if (userInfoResponse!=null){
            Glide.with(this).load(userInfoResponse.getData().getAvatar()).error(R.mipmap.logo).placeholder(R.mipmap.logo).into(avatar);
            switcherPush.setChecked(userInfoResponse.getData().getReceiveCall()==1);
            switcherWork.setChecked(userInfoResponse.getData().getWorkState()==1);
            tvUserName.setText(userInfoResponse.getData().getName());
            tvPhone.setText(userInfoResponse.getData().getMobile());
        }

        userPresenter = new UserPresenter(this);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        switcherPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showWaitingDialog("更新消息接收状态...", false);
                userPresenter.updateStatus(isChecked?1:0, switcherWork.isChecked()?1:0);
            }
        });

        switcherWork.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showWaitingDialog("更新上班状态...", false);
                userPresenter.updateStatus(switcherPush.isChecked()?1:0, isChecked?1:0);
            }
        });

        //获取应用名和版本号
        try {
            PackageManager packageManager = getContext().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
            tvVersion.setText("v" + packageInfo.versionName);
        }catch (Exception e){
            e.printStackTrace();
        }

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAvatarPermission();
            }
        });

        mRoot.findViewById(R.id.tvUpgrade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /***** 检查更新 *****/
                Beta.checkUpgrade();
            }
        });


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_user;
    }

    @Override
    public void uploadAvatarSuccess(String url) {
        closeWaitingDialog();
        userInfoResponse.getData().setAvatar(url);
        SharedPreferenceManager.saveLoginResponse(userInfoResponse);
        Glide.with(this).load(url).into(avatar);
        App.getContext().showMessage("更新头像成功");
    }

    @Override
    public void uploadAvatarFail(String msg) {
        closeWaitingDialog();
        App.getContext().showMessage(msg);
        if (msg == "token无效！") {
            JPushInterface.deleteAlias(mContext, SharedPreferenceManager.getSequence());
            SharedPreferenceManager.clearUserInfo();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void updateStatusSuccess(String msg) {
        closeWaitingDialog();
        userInfoResponse.getData().setReceiveCall(switcherPush.isChecked()?1:0);
        userInfoResponse.getData().setWorkState(switcherWork.isChecked()?1:0);
        SharedPreferenceManager.saveLoginResponse(userInfoResponse);
        App.getContext().showMessage(msg);
        if (switcherWork.isChecked())
            JPushInterface.resumePush(mContext);
        else
            JPushInterface.stopPush(mContext);
    }

    @Override
    public void updateStatusFail(String msg) {
        closeWaitingDialog();
        App.getContext().showMessage(msg);
        if (msg == "token无效！") {
            JPushInterface.deleteAlias(mContext, SharedPreferenceManager.getSequence());
            SharedPreferenceManager.clearUserInfo();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void checkAvatarPermission(){
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 1);
        }else {
            chooseImage();
        }
    }

    private void chooseImage(){
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())
                .maxSelectNum(9)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .imageSpanCount(4)// 每行显示个数
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .previewImage(true)// 是否可预览图片
                .previewVideo(false)// 是否可预览视频
                .enablePreviewAudio(false) // 是否可播放音频
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .enableCrop(true)// 是否裁剪
                .compress(false)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .withAspectRatio(1, 1)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(true)// 是否显示uCrop工具栏，默认不显示
                .isGif(false)// 是否显示gif图片
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(CHOOSE_LOGO);//结果回调onActivityResult code
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PERMISSION_GRANTED) {//选择了“始终允许”
                    chooseImage();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permissions[i])){//用户选择了禁止不再询问

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("获取权限")
                                .setMessage("点击允许读取存储权限才可以修改头像")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (mDialog != null && mDialog.isShowing()) {
                                            mDialog.dismiss();
                                        }
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);//注意就是"package",不用改成自己的包名
                                        intent.setData(uri);
                                        startActivityForResult(intent, NOT_NOTICE);
                                    }
                                });
                        mDialog = builder.create();
                        mDialog.setCanceledOnTouchOutside(false);
                        mDialog.show();
                    }else {//选择禁止
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("permission")
                                .setMessage("点击允许才可以使用我们的app哦")
                                .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (alertDialog != null && alertDialog.isShowing()) {
                                            alertDialog.dismiss();
                                        }
                                        ActivityCompat.requestPermissions(getActivity(),
                                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 1);
                                    }
                                });
                        alertDialog = builder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }

                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==NOT_NOTICE){
            checkAvatarPermission();//由于不知道是否选择了允许所以需要再次判断
        }
        if (requestCode==CHOOSE_LOGO){
            List<LocalMedia> datas = PictureSelector.obtainMultipleResult(data);
            if (datas!=null && datas.size()!=0){
                LocalMedia localMedia = datas.get(0);
                String pat = "";
                if (localMedia.isCut()) {
                    pat = localMedia.getCutPath();
                } else {
                    pat = localMedia.getPath();
                }
                showWaitingDialog("头像上传中...", false);
                userPresenter.uploadAvatar(imgToBase64String(pat));
            }
        }
    }


    /**
     * 将图片转为base64 位
     *
     * @param filePath 图片的地址
     * @return
     */
    public static String imgToBase64String(String filePath) {
        if (filePath == null) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (filePath.indexOf(".") > 0) {
            if (filePath.contains(".png")) {
                filePath = filePath.substring(0, filePath.indexOf(".png"));
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            } else if (filePath.contains(".jpg")) {
                filePath = filePath.substring(0, filePath.indexOf(".jpg"));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            } else if(filePath.contains(".jpeg")){
                filePath = filePath.substring(0, filePath.indexOf(".jpeg"));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            }
        }

        byte[] byteServer = stream.toByteArray();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e("UserActivity", "图片的大小：" + byteServer.length);
        String base64String = Base64.encodeToString(byteServer, 0, byteServer.length, Base64.NO_WRAP);
        Log.e("dataBase64", base64String);
        return base64String;
    }

    private void logout(){
        new AlertDialog.Builder(mContext).setMessage("确定退出登陆？").setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JPushInterface.deleteAlias(mContext, SharedPreferenceManager.getSequence());
                //JPushInterface.cleanTags(this, SharedPreferenceManager.getSequence())
                SharedPreferenceManager.clearUserInfo();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        }).show();
    }

}
