package com.mlizhi.modules.login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.philips.skincare.skincareprototype.R;

public class LoginActivity extends Activity {
    private String account;
    private String fromFlag;
    private Context mContext;
   // private UMSocialService mController;
    private RequestQueue mRequestQueue;
    //private MlzApplication mlzApplication;
    private String password;
    private EditText passwordEditText;
    private TextView skipTextView;
    private String username;
    private EditText usernameEditText;

    public LoginActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_login);
      //  this.mlzApplication = (MlzApplication) getApplication();
     //   this.mController = this.mlzApplication.getmController();
        this.mContext = this;
        this.fromFlag = getIntent().getStringExtra(com.mlizhi.utils.Constants.LOGIN_FROM_FLAG);
        this.usernameEditText = (EditText) findViewById(R.id.user_info_login_username);
        this.passwordEditText = (EditText) findViewById(R.id.user_info_login_password);
        this.skipTextView = (TextView) findViewById(R.id.id_out_login_tv);
       /* if (SpecSettingFragment.class.getSimpleName().equals(this.fromFlag)) {
            this.skipTextView.setVisibility(8);
        }*/
        this.usernameEditText.setText(this.account);
        this.passwordEditText.setText(this.password);
       // this.mController.getConfig().setSsoHandler(new SinaSsoHandler());
       // this.mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        //addWXPlatform();
        //addQQQZonePlatform();
    }


    public void onLoginClick(View view) {
        switch (view.getId()) {
            case R.id.id_out_login_tv:
                // TODO-Anukool
                //startActivity(new Intent(this.mContext, SpecActivity.class));
               // finish();
           /* case R.id.login_submit_btn:
                login();
            case R.id.qq_login_btn:
                login4open(SHARE_MEDIA.QQ);
            case R.id.wchat_login_btn:
                login4open(SHARE_MEDIA.WEIXIN);
            case R.id.sina_login_btn:
                login4open(SHARE_MEDIA.SINA);
            case R.id.user_forget_password:
                String username = String.valueOf(this.usernameEditText.getText());
                if (username == null || bq.f888b.equals(username)) {
                    Toast.makeText(this.mContext, "\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a\uff01", 0).show();
                    return;
                }
                this.mSession.setAccount(username);
                startActivity(new Intent(this.mContext, ForgetPassword1Activity.class));
            case R.id.user_regist:
                startActivity(new Intent(this.mContext, Regist1Activity.class));
            default:*/
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

   /* private void addWXPlatform() {
        String appId = com.mlizhi.utils.Constants.WEIXIN_APPID;
        String appSecret = com.mlizhi.utils.Constants.WEIXIN_APPSECRET;
        new UMWXHandler(this, appId, appSecret).addToSocialSDK();
        UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    private void addQQQZonePlatform() {
        new QZoneSsoHandler(this, com.mlizhi.utils.Constants.QQ_APPID, com.mlizhi.utils.Constants.QQ_APPSECRET).addToSocialSDK();
    }*/

   /* private void login4open(SHARE_MEDIA platform) {
        this.mController.doOauthVerify(this, platform, new C05364());
    }

    private void logout(SHARE_MEDIA platform) {
        this.mController.deleteOauth(this.mContext, platform, new C05375(platform));
    }

    private void getUserInfo(SHARE_MEDIA platform) {
        this.mController.getPlatformInfo(this, platform, new C03946());
    }*/

    /*protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = this.mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }*/
}
