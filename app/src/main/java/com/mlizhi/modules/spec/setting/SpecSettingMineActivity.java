package com.mlizhi.modules.spec.setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images.Media;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mlizhi.base.NetWorkManager;
import com.mlizhi.base.SecurityUtil;
import com.mlizhi.base.Session;
import com.mlizhi.base.imageloader.core.DisplayImageOptions;
import com.mlizhi.base.imageloader.core.DisplayImageOptions.Builder;
import com.mlizhi.base.imageloader.core.ImageLoader;
import com.mlizhi.modules.spec.util.JsonUtil;
import com.philips.skincare.skincareprototype.R;
import com.tencent.connect.common.Constants;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import p016u.aly.bq;

//import com.mlizhi.modules.login.UpdatePasswordActivity;
//import com.umeng.socialize.common.SocializeConstants;
//import com.umeng.socialize.net.utils.SocializeProtocolConstants;

public class SpecSettingMineActivity extends Activity {
    private static final int PHOTO_REQUEST_CUT = 3;
    private static final int PHOTO_REQUEST_GALLERY = 2;
    private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
    public static final int REQUEST_CODE_ADDRESS = 33;
    public static final int REQUEST_CODE_BIRTHDAY = 35;
    public static final int REQUEST_CODE_NAME = 32;
    public static final int REQUEST_CODE_SEX = 34;
    public static final int REQUEST_CODE_SKIN_TYPE = 36;
    private String bucket;
    private DisplayImageOptions displayImageOptions;
    private String formApiSecret;
    Listener<String> listener;
    private String localDirPath;
    private String localFilePath;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private Session mSession;
    private String savePath;
    File tempFile;
    private TextView userAccount;
    private TextView userAddress;
    private TextView userBriday;
    private ImageView userInfoPhoto;
    private TextView userNickName;
    private TextView userSex;
    private TextView userSkinType;
    private String visitPath;

    /* renamed from: com.mlizhi.modules.spec.setting.SpecSettingMineActivity.2 */
    class C01602 implements OnClickListener {
        C01602() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            intent.putExtra("output", Uri.fromFile(SpecSettingMineActivity.this.tempFile));
            SpecSettingMineActivity.this.startActivityForResult(intent, SpecSettingMineActivity.PHOTO_REQUEST_TAKEPHOTO);
        }
    }

    /* renamed from: com.mlizhi.modules.spec.setting.SpecSettingMineActivity.3 */
    class C01613 implements OnClickListener {
        C01613() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            Intent intent = new Intent("android.intent.action.PICK", null);
            intent.setDataAndType(Media.EXTERNAL_CONTENT_URI, "image/*");
            SpecSettingMineActivity.this.startActivityForResult(intent, SpecSettingMineActivity.PHOTO_REQUEST_GALLERY);
        }
    }

   /* class UploadUserPic extends AsyncTask<Void, Void, String> {

        *//* renamed from: com.mlizhi.modules.spec.setting.SpecSettingMineActivity.UploadUserPic.1 *//*
        class C04141 implements ProgressListener {
            C04141() {
            }

            public void transferred(long transferedBytes, long totalBytes) {
            }
        }

        *//* renamed from: com.mlizhi.modules.spec.setting.SpecSettingMineActivity.UploadUserPic.2 *//*
        class C04152 implements CompleteListener {
            C04152() {
            }

            public void result(boolean isComplete, String result, String error) {
            }
        }

        UploadUserPic() {
        }

        protected String doInBackground(Void... params) {
            SpecSettingMineActivity.this.savePath = "mlizhi4bbc/" + String.valueOf(UUID.randomUUID()) + ".jpg";
            SpecSettingMineActivity.this.visitPath = "http://web-images.b0.upaiyun.com/" + SpecSettingMineActivity.this.savePath;
            File localFile = new File(SpecSettingMineActivity.this.localFilePath);
            try {
                ProgressListener progressListener = new C04141();
                CompleteListener completeListener = new C04152();
                UploaderManager uploaderManager = UploaderManager.getInstance(SpecSettingMineActivity.this.bucket);
                uploaderManager.setConnectTimeout(60);
                uploaderManager.setResponseTimeout(60);
                Map<String, Object> paramsMap = uploaderManager.fetchFileInfoDictionaryWith(localFile, SpecSettingMineActivity.this.savePath);
                paramsMap.put("return_url", "http://httpbin.org/get");
                uploaderManager.upload(UpYunUtils.getPolicy(paramsMap), UpYunUtils.getSignature(paramsMap, SpecSettingMineActivity.this.formApiSecret), localFile, progressListener, completeListener);
                SpecSettingMineActivity.this.mSession.setUserIcon(SpecSettingMineActivity.this.visitPath);
                SpecSettingMineActivity.this.updateUserIcon();
                return "success";
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }*/

    /* renamed from: com.mlizhi.modules.spec.setting.SpecSettingMineActivity.1 */
    class C04121 implements Listener<String> {
        C04121() {
        }

        public void onResponse(String response) {
            if (Constants.VIA_RESULT_SUCCESS.equals(JsonUtil.getHeaderCode(response))) {
                ImageLoader.getInstance().displayImage(SpecSettingMineActivity.this.mSession.getUserIcon(), SpecSettingMineActivity.this.userInfoPhoto, SpecSettingMineActivity.this.displayImageOptions);
                Toast.makeText(SpecSettingMineActivity.this.mContext, "\u5934\u50cf\u8bbe\u7f6e\u6210\u529f\uff01", Toast.LENGTH_LONG).show();
                return;
            }
            Toast.makeText(SpecSettingMineActivity.this.mContext, JsonUtil.getHeaderErrorInfo(response), Toast.LENGTH_LONG).show();
        }
    }

    /* renamed from: com.mlizhi.modules.spec.setting.SpecSettingMineActivity.4 */
    class C04134 implements ErrorListener {
        C04134() {
        }

        public void onErrorResponse(VolleyError error) {
            Toast.makeText(SpecSettingMineActivity.this.mContext, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /* renamed from: com.mlizhi.modules.spec.setting.SpecSettingMineActivity.5 */
    class C05465 extends StringRequest {
        C05465(int $anonymous0, String $anonymous1, Listener $anonymous2, ErrorListener $anonymous3) {
            super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
        }

        protected Map<String, String> getParams() throws AuthFailureError {
            String timestamp = SecurityUtil.getTimestamp();
            Map<String, String> params = new HashMap();
            params.put("id", SpecSettingMineActivity.this.mSession.getUid());
            params.put("headImgurl", SpecSettingMineActivity.this.visitPath);
            params.put(com.mlizhi.utils.Constants.URL_TIMESTAMP, timestamp);
            params.put(com.mlizhi.utils.Constants.URL_KEY, SecurityUtil.getMd5String(timestamp));
            return params;
        }
    }

    public SpecSettingMineActivity() {
        this.userInfoPhoto = null;
        this.displayImageOptions = null;
        this.bucket = "web-images";
        this.formApiSecret = "9kxLSWbTMu39pWDfaPA5KtzdRXU=";
        this.savePath = bq.f888b;
        this.visitPath = bq.f888b;
        this.localDirPath = bq.f888b;
        this.localFilePath = bq.f888b;
        this.tempFile = null;
        this.listener = new C04121();
    }

    @SuppressLint({"NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSession = Session.get(getApplicationContext());
        this.mContext = this;
        setContentView(R.layout.activity_spec_setting_mine);
        MobclickAgent.updateOnlineConfig(this.mContext);
        this.displayImageOptions = new Builder().showImageOnLoading((int) R.drawable.ic_tourist).showImageForEmptyUri((int) R.drawable.ic_tourist).showImageOnFail((int) R.drawable.ic_tourist).cacheInMemory(true).cacheOnDisk(true).build();
        this.userInfoPhoto = (ImageView) findViewById(R.id.id_user_photo_detail);
        this.userSex = (TextView) findViewById(R.id.id_user_info_sex_value);
        this.userBriday = (TextView) findViewById(R.id.id_user_info_age_value);
        this.userNickName = (TextView) findViewById(R.id.id_user_info_nickname_value);
        this.userAccount = (TextView) findViewById(R.id.id_user_info_account_value);
        this.userSkinType = (TextView) findViewById(R.id.id_user_info_skintype_value);
        this.userAddress = (TextView) findViewById(R.id.id_user_info_address_value);
        String userPhoto = this.mSession.getUserIcon();
        if (!(userPhoto == null || bq.f888b.equals(userPhoto))) {
            ImageLoader.getInstance().displayImage(this.mSession.getUserIcon(), this.userInfoPhoto, this.displayImageOptions);
        }
        String sex = bq.f888b;
        if (Constants.VIA_RESULT_SUCCESS.equals(this.mSession.getUserSex())) {
            sex = getString(R.string.user_pre_setting_sex_female);
        } else if (Constants.VIA_TO_TYPE_QQ_GROUP.equals(this.mSession.getUserSex())) {
            sex = getString(R.string.user_pre_setting_sex_male);
        }
        this.userSex.setText(sex);
        this.userBriday.setText(this.mSession.getUserBriday());
        String userName = this.mSession.getUserName();
        if (userName == null || bq.f888b.equals(userName) || "null".equals(userName)) {
            userName = "\u60a8\u8fd8\u672a\u767b\u5f55";
        }
        this.userNickName.setText(userName);
        this.userAccount.setText(this.mSession.getAccount());
        String skinType = getString(R.string.user_pre_setting_skin_type_unknow);
        if (Constants.VIA_TO_TYPE_QQ_GROUP.equals(this.mSession.getUserSkinType())) {
            skinType = getString(R.string.user_pre_setting_skin_type_dry);
        } else if (Constants.VIA_SSO_LOGIN.equals(this.mSession.getUserSkinType())) {
            skinType = getString(R.string.user_pre_setting_skin_type_oil);
        } else if (Constants.VIA_TO_TYPE_QQ_DISCUSS_GROUP.equals(this.mSession.getUserSkinType())) {
            skinType = getString(R.string.user_pre_setting_skin_type_hybird);
        } else if (Constants.VIA_TO_TYPE_QZONE.equals(this.mSession.getUserSkinType())) {
            skinType = getString(R.string.user_pre_setting_skin_type_sensitive);
        } else if (Constants.VIA_SHARE_TYPE_TEXT.equals(this.mSession.getUserSkinType())) {
            skinType = getString(R.string.user_pre_setting_skin_type_unknow);
        }
        this.userSkinType.setText(skinType);
        String address = this.mSession.getUserAddress();
        if (address == null || bq.f888b.equals(address) || "null".equals(address)) {
            address = bq.f888b;
        }
        this.userAddress.setText(address);
        this.localDirPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
        this.localFilePath = this.localDirPath + getPhotoFileName();
        this.tempFile = new File(this.localFilePath);
    }

    public void mine2main(View view) {
        switch (view.getId()) {
            case R.id.id_spec_setting_mine_back:
                setResult(-1);
                finish();
            case R.id.id_user_info_photo_ly:
                showDialog();
                MobclickAgent.onEvent(this.mContext, "editPhoto");
            case R.id.id_user_info_password_label:
                // TODO - Anukool
               // startActivity(new Intent(this, UpdatePasswordActivity.class));
            case R.id.id_user_info_nickname_ly:
                // TODO - Anukool
               // startActivityForResult(new Intent(this, SpecSettingMineUserNameActivity.class), REQUEST_CODE_NAME);
                MobclickAgent.onEvent(this.mContext, "editNickName");
            case R.id.id_user_info_sex_ly:
                // TODO - Anukool
                //startActivityForResult(new Intent(this, SpecSettingMineUserSexActivity.class), REQUEST_CODE_SEX);
                MobclickAgent.onEvent(this.mContext, "editSex");
            case R.id.id_user_info_age_ly:
                // TODO - Anukool
               // startActivityForResult(new Intent(this, SpecSettingMineUserBridayActivity.class), REQUEST_CODE_BIRTHDAY);
                MobclickAgent.onEvent(this.mContext, "editBirthday");
            case R.id.id_user_info_skintype_ly:
                // TODO - Anukool
              //  startActivityForResult(new Intent(this, SpecSettingMineUserSkinTypeActivity.class), REQUEST_CODE_SKIN_TYPE);
                MobclickAgent.onEvent(this.mContext, "editSkinType");
            case R.id.id_user_info_address_ly:
                // TODO - Anukool
                //startActivityForResult(new Intent(this, SpecSettingMineUserAddressActivity.class), REQUEST_CODE_ADDRESS);
                MobclickAgent.onEvent(this.mContext, "editAddress");
            default:
        }
    }

    protected void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SplashScreen");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SplashScreen");
        MobclickAgent.onPause(this);
    }

    private void showDialog() {
        File file = new File(this.localDirPath);
        if (file.exists()) {
            file.mkdirs();
            try {
                this.tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new AlertDialog.Builder(this).setTitle(R.string.user_info_header_icon_setting).setPositiveButton(R.string.user_info_header_icon_cemera, new C01602()).setNegativeButton(R.string.user_info_header_icon_setting, new C01613()).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PHOTO_REQUEST_TAKEPHOTO /*1*/:
                startPhotoZoom(Uri.fromFile(this.tempFile), 150);
                break;
            case PHOTO_REQUEST_GALLERY /*2*/:
                if (data != null) {
                    startPhotoZoom(data.getData(), 150);
                    break;
                }
                break;
            case PHOTO_REQUEST_CUT /*3*/:
                if (data != null) {
                   // setPicToView(data);
                    break;
                }
                break;
            case REQUEST_CODE_NAME /*32*/:
                if (resultCode == -1) {
                    this.userNickName.setText(this.mSession.getUserName());
                    break;
                }
                break;
            case REQUEST_CODE_ADDRESS /*33*/:
                if (resultCode == -1) {
                    this.userAddress.setText(this.mSession.getUserAddress());
                    break;
                }
                break;
            case REQUEST_CODE_SEX /*34*/:
                if (resultCode == -1) {
                    if (!Constants.VIA_RESULT_SUCCESS.equals(this.mSession.getUserSex())) {
                        if (Constants.VIA_TO_TYPE_QQ_GROUP.equals(this.mSession.getUserSex())) {
                            this.userSex.setText(R.string.user_pre_setting_sex_male);
                            break;
                        }
                    }
                    this.userSex.setText(R.string.user_pre_setting_sex_female);
                    break;
                }
                break;
            case REQUEST_CODE_BIRTHDAY /*35*/:
                if (resultCode == -1) {
                    this.userBriday.setText(this.mSession.getUserBriday());
                    break;
                }
                break;
            case REQUEST_CODE_SKIN_TYPE /*36*/:
                if (resultCode == -1) {
                    if (!Constants.VIA_TO_TYPE_QQ_GROUP.equals(this.mSession.getUserSkinType())) {
                        if (!Constants.VIA_SSO_LOGIN.equals(this.mSession.getUserSkinType())) {
                            if (!Constants.VIA_TO_TYPE_QZONE.equals(this.mSession.getUserSkinType())) {
                                if (!Constants.VIA_TO_TYPE_QQ_DISCUSS_GROUP.equals(this.mSession.getUserSkinType())) {
                                    if (Constants.VIA_SHARE_TYPE_TEXT.equals(this.mSession.getUserSkinType())) {
                                        this.userSkinType.setText(R.string.user_pre_setting_skin_type_unknow);
                                        break;
                                    }
                                }
                                this.userSkinType.setText(R.string.user_pre_setting_skin_type_hybird);
                                break;
                            }
                            this.userSkinType.setText(R.string.user_pre_setting_skin_type_sensitive);
                            break;
                        }
                        this.userSkinType.setText(R.string.user_pre_setting_skin_type_oil);
                        break;
                    }
                    this.userSkinType.setText(R.string.user_pre_setting_skin_type_dry);
                    break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPhotoZoom(Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", PHOTO_REQUEST_TAKEPHOTO);
        intent.putExtra("aspectY", PHOTO_REQUEST_TAKEPHOTO);
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

   /* private void setPicToView(Intent picdata) {
        FileNotFoundException e;
        Throwable th;
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = (Bitmap) bundle.getParcelable("data");
            FileOutputStream b = null;
            try {
                FileOutputStream b2 = new FileOutputStream(this.localFilePath);
                try {
                    photo.compress(CompressFormat.JPEG, 100, b2);
                    if (b2 != null) {
                        try {
                            b2.flush();
                            b2.close();
                            b = b2;
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                        new UploadUserPic().execute(new Void[0]);
                    }
                    b = b2;
                } catch (FileNotFoundException e3) {
                    e = e3;
                    b = b2;
                    try {
                        e.printStackTrace();
                        if (b != null) {
                            try {
                                b.flush();
                                b.close();
                            } catch (IOException e22) {
                                e22.printStackTrace();
                            }
                        }
                        new UploadUserPic().execute(new Void[0]);
                    } catch (Throwable th2) {
                        th = th2;
                        if (b != null) {
                            try {
                                b.flush();
                                b.close();
                            } catch (IOException e222) {
                                e222.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    b = b2;
                    if (b != null) {
                        b.flush();
                        b.close();
                    }
                    throw th;
                }
            } catch (FileNotFoundException e4) {
                e = e4;
                e.printStackTrace();
                if (b != null) {
                    b.flush();
                    b.close();
                }
                new UploadUserPic().execute(new Void[0]);
            }
            new UploadUserPic().execute(new Void[0]);
        }
    }*/

    @SuppressLint({"SimpleDateFormat"})
    private String getPhotoFileName() {
        return new StringBuilder(String.valueOf(new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis())))).append(".jpg").toString();
    }

    public void updateUserIcon() {
        if (NetWorkManager.getNewInstance().isNetworkConnected(this.mContext)) {
            this.mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            this.mRequestQueue.add(new C05465(PHOTO_REQUEST_TAKEPHOTO, com.mlizhi.utils.Constants.URL_POST_UPDATE_INFO, this.listener, new C04134()));
            this.mRequestQueue.start();
            return;
        }
        Toast.makeText(this.mContext, R.string.net_connected_failure, Toast.LENGTH_LONG).show();
    }
}
