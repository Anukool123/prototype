package com.mlizhi.modules.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.mlizhi.C0111R;
import com.mlizhi.base.MlzApplication;
import com.mlizhi.base.NetWorkManager;
import com.mlizhi.base.SecurityUtil;
import com.mlizhi.base.Session;
import com.mlizhi.base.Utils;
import com.mlizhi.modules.spec.SpecActivity;
import com.mlizhi.modules.spec.setting.SpecSettingFragment;
import com.mlizhi.modules.spec.util.JsonUtil;
import com.tencent.connect.common.Constants;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.StatusCode;
import com.umeng.socialize.common.SocialSNSHelper;
import com.umeng.socialize.common.SocializeConstants;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.net.utils.SocializeProtocolConstants;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.TencentWBSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import p016u.aly.bq;

public class LoginActivity extends Activity {
    private String account;
    private String fromFlag;
    ErrorListener listener4LoginError;
    Listener<String> listener4LoginSuccess;
    private Context mContext;
    private UMSocialService mController;
    private RequestQueue mRequestQueue;
    private Session mSession;
    private MlzApplication mlzApplication;
    private String password;
    private EditText passwordEditText;
    private TextView skipTextView;
    private String username;
    private EditText usernameEditText;

    /* renamed from: com.mlizhi.modules.login.LoginActivity.1 */
    class C03921 implements ErrorListener {
        C03921() {
        }

        public void onErrorResponse(VolleyError error) {
            Toast.makeText(LoginActivity.this.mContext, error.getMessage(), 0).show();
        }
    }

    /* renamed from: com.mlizhi.modules.login.LoginActivity.2 */
    class C03932 implements Listener<String> {
        C03932() {
        }

        public void onResponse(String response) {
            if (Constants.VIA_RESULT_SUCCESS.equals(JsonUtil.getHeaderCode(response))) {
                try {
                    JSONObject userJsonObject = JsonUtil.getBodyJsonObject(response).getJSONObject("customer");
                    LoginActivity.this.mSession.setAccount(String.valueOf(LoginActivity.this.usernameEditText.getText()));
                    LoginActivity.this.mSession.setPassword(String.valueOf(LoginActivity.this.passwordEditText.getText()));
                    LoginActivity.this.mSession.setUid(userJsonObject.getString(SocializeConstants.WEIBO_ID));
                    String username = userJsonObject.getString("nikeName");
                    if (username == null) {
                        username = bq.f888b;
                    }
                    LoginActivity.this.mSession.setUserName(username);
                    String headImgurl = userJsonObject.getString("headImgurl");
                    if (headImgurl == null) {
                        headImgurl = bq.f888b;
                    }
                    LoginActivity.this.mSession.setUserIcon(headImgurl);
                    String address = userJsonObject.getString("address");
                    if (address == null) {
                        address = bq.f888b;
                    }
                    LoginActivity.this.mSession.setUserAddress(address);
                    LoginActivity.this.mSession.setLoginType(com.mlizhi.utils.Constants.LOGIN_TYPE_PHONE);
                } catch (JSONException e) {
                    Utils.m13E("parse login result info json string error!!!", e);
                }
                if (SpecSettingFragment.class.getSimpleName().equals(LoginActivity.this.fromFlag)) {
                    LoginActivity.this.setResult(17);
                } else {
                    LoginActivity.this.startActivity(new Intent(LoginActivity.this.mContext, SpecActivity.class));
                }
                LoginActivity.this.finish();
                return;
            }
            Toast.makeText(LoginActivity.this.mContext, JsonUtil.getHeaderErrorInfo(response), 0).show();
        }
    }

    /* renamed from: com.mlizhi.modules.login.LoginActivity.6 */
    class C03946 implements UMDataListener {
        C03946() {
        }

        public void onStart() {
        }

        public void onComplete(int status, Map<String, Object> info) {
            if (info != null) {
                Toast.makeText(LoginActivity.this.mContext, info.toString(), 0).show();
            }
        }
    }

    /* renamed from: com.mlizhi.modules.login.LoginActivity.3 */
    class C05353 extends StringRequest {
        C05353(int $anonymous0, String $anonymous1, Listener $anonymous2, ErrorListener $anonymous3) {
            super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
        }

        protected Map<String, String> getParams() throws AuthFailureError {
            String timestamp = SecurityUtil.getTimestamp();
            Map<String, String> params = new HashMap();
            if (com.mlizhi.utils.Utils.matchEmail(LoginActivity.this.username)) {
                params.put(SocialSNSHelper.SOCIALIZE_EMAIL_KEY, LoginActivity.this.username);
            } else if (com.mlizhi.utils.Utils.matchPhoneNumber(LoginActivity.this.username)) {
                params.put(com.mlizhi.utils.Constants.SMS_MOBILE_NUMBER, LoginActivity.this.username);
            }
            params.put("password", String.valueOf(LoginActivity.this.passwordEditText.getText()));
            params.put("companyId", Constants.VIA_TO_TYPE_QQ_GROUP);
            params.put(com.mlizhi.utils.Constants.URL_TIMESTAMP, timestamp);
            params.put(com.mlizhi.utils.Constants.URL_KEY, SecurityUtil.getMd5String(timestamp));
            return params;
        }
    }

    /* renamed from: com.mlizhi.modules.login.LoginActivity.4 */
    class C05364 implements UMAuthListener {
        C05364() {
        }

        public void onStart(SHARE_MEDIA platform) {
            Toast.makeText(LoginActivity.this.mContext, "start", 0).show();
        }

        public void onError(SocializeException e, SHARE_MEDIA platform) {
            Toast.makeText(LoginActivity.this.mContext, "error", 0).show();
        }

        public void onComplete(Bundle value, SHARE_MEDIA platform) {
            Toast.makeText(LoginActivity.this.mContext, "onComplete", 0).show();
            if (TextUtils.isEmpty(value.getString(SocializeProtocolConstants.PROTOCOL_KEY_UID))) {
                Toast.makeText(LoginActivity.this.mContext, "\u6388\u6743\u5931\u8d25...", 0).show();
                return;
            }
            LoginActivity.this.getUserInfo(platform);
            if (platform == SHARE_MEDIA.SINA) {
                LoginActivity.this.mSession.setLoginType(SocialSNSHelper.SOCIALIZE_SINA_KEY);
            } else if (platform == SHARE_MEDIA.QQ) {
                LoginActivity.this.mSession.setLoginType(SocialSNSHelper.SOCIALIZE_QQ_KEY);
            } else if (platform == SHARE_MEDIA.WEIXIN) {
                LoginActivity.this.mSession.setLoginType(SocialSNSHelper.SOCIALIZE_WEIXIN_KEY);
            }
        }

        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(LoginActivity.this.mContext, "cancel", 0).show();
        }
    }

    /* renamed from: com.mlizhi.modules.login.LoginActivity.5 */
    class C05375 implements SocializeClientListener {
        private final /* synthetic */ SHARE_MEDIA val$platform;

        C05375(SHARE_MEDIA share_media) {
            this.val$platform = share_media;
        }

        public void onStart() {
        }

        public void onComplete(int status, SocializeEntity entity) {
            String showText = "\u89e3\u9664" + this.val$platform.toString() + "\u5e73\u53f0\u6388\u6743\u6210\u529f";
            if (status != StatusCode.ST_CODE_SUCCESSED) {
                showText = "\u89e3\u9664" + this.val$platform.toString() + "\u5e73\u53f0\u6388\u6743\u5931\u8d25[" + status + "]";
            }
            Toast.makeText(LoginActivity.this.mContext, showText, 0).show();
        }
    }

    public LoginActivity() {
        this.fromFlag = bq.f888b;
        this.account = bq.f888b;
        this.password = bq.f888b;
        this.mController = null;
        this.username = bq.f888b;
        this.listener4LoginError = new C03921();
        this.listener4LoginSuccess = new C03932();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0111R.layout.activity_login_login);
        this.mlzApplication = (MlzApplication) getApplication();
        this.mController = this.mlzApplication.getmController();
        this.mContext = this;
        this.mSession = Session.get(getApplicationContext());
        this.fromFlag = getIntent().getStringExtra(com.mlizhi.utils.Constants.LOGIN_FROM_FLAG);
        MobclickAgent.updateOnlineConfig(this.mContext);
        AnalyticsConfig.enableEncrypt(true);
        this.usernameEditText = (EditText) findViewById(C0111R.id.user_info_login_username);
        this.passwordEditText = (EditText) findViewById(C0111R.id.user_info_login_password);
        this.skipTextView = (TextView) findViewById(C0111R.id.id_out_login_tv);
        if (SpecSettingFragment.class.getSimpleName().equals(this.fromFlag)) {
            this.skipTextView.setVisibility(8);
        }
        this.account = this.mSession.getAccount();
        this.password = this.mSession.getPassword();
        this.usernameEditText.setText(this.account);
        this.passwordEditText.setText(this.password);
        this.mController.getConfig().setSsoHandler(new SinaSsoHandler());
        this.mController.getConfig().setSsoHandler(new TencentWBSsoHandler());
        addWXPlatform();
        addQQQZonePlatform();
    }

    private void login() {
        this.username = String.valueOf(this.usernameEditText.getText());
        if (!com.mlizhi.utils.Utils.matchEmail(this.username) && !com.mlizhi.utils.Utils.matchPhoneNumber(this.username)) {
            Toast.makeText(this.mContext, "\u4f60\u8f93\u5165\u7684\u5e10\u53f7\u683c\u5f0f\u4e0d\u6b63\u786e\uff01\uff01\uff01", 0).show();
        } else if (NetWorkManager.getNewInstance().isNetworkConnected(this.mContext)) {
            this.mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new C05353(1, com.mlizhi.utils.Constants.URL_USER_LOGIN_URL, this.listener4LoginSuccess, this.listener4LoginError);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(AsyncHttpClient.DEFAULT_SOCKET_TIMEOUT, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.mRequestQueue.add(stringRequest);
            this.mRequestQueue.start();
        } else {
            Toast.makeText(this.mContext, C0111R.string.net_connected_failure, 0).show();
        }
    }

    public void onLoginClick(View view) {
        switch (view.getId()) {
            case C0111R.id.id_out_login_tv:
                startActivity(new Intent(this.mContext, SpecActivity.class));
                finish();
            case C0111R.id.login_submit_btn:
                login();
            case C0111R.id.qq_login_btn:
                login4open(SHARE_MEDIA.QQ);
            case C0111R.id.wchat_login_btn:
                login4open(SHARE_MEDIA.WEIXIN);
            case C0111R.id.sina_login_btn:
                login4open(SHARE_MEDIA.SINA);
            case C0111R.id.user_forget_password:
                String username = String.valueOf(this.usernameEditText.getText());
                if (username == null || bq.f888b.equals(username)) {
                    Toast.makeText(this.mContext, "\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a\uff01", 0).show();
                    return;
                }
                this.mSession.setAccount(username);
                startActivity(new Intent(this.mContext, ForgetPassword1Activity.class));
            case C0111R.id.user_regist:
                startActivity(new Intent(this.mContext, Regist1Activity.class));
            default:
        }
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

    private void addWXPlatform() {
        String appId = com.mlizhi.utils.Constants.WEIXIN_APPID;
        String appSecret = com.mlizhi.utils.Constants.WEIXIN_APPSECRET;
        new UMWXHandler(this, appId, appSecret).addToSocialSDK();
        UMWXHandler wxCircleHandler = new UMWXHandler(this, appId, appSecret);
        wxCircleHandler.setToCircle(true);
        wxCircleHandler.addToSocialSDK();
    }

    private void addQQQZonePlatform() {
        new QZoneSsoHandler(this, com.mlizhi.utils.Constants.QQ_APPID, com.mlizhi.utils.Constants.QQ_APPSECRET).addToSocialSDK();
    }

    private void login4open(SHARE_MEDIA platform) {
        this.mController.doOauthVerify(this, platform, new C05364());
    }

    private void logout(SHARE_MEDIA platform) {
        this.mController.deleteOauth(this.mContext, platform, new C05375(platform));
    }

    private void getUserInfo(SHARE_MEDIA platform) {
        this.mController.getPlatformInfo(this, platform, new C03946());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = this.mController.getConfig().getSsoHandler(requestCode);
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
}
