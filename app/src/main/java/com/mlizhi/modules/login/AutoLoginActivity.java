package com.mlizhi.modules.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.mlizhi.base.NetWorkManager;
import com.mlizhi.base.SecurityUtil;
import com.mlizhi.base.Session;
import com.mlizhi.base.Utils;
import com.mlizhi.modules.spec.SpecActivity;
import com.mlizhi.modules.spec.util.JsonUtil;
import com.tencent.connect.common.Constants;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.common.SocializeConstants;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import p016u.aly.bq;

public class AutoLoginActivity extends Activity {
    ErrorListener listener4LoginError;
    Listener<String> listener4LoginSuccess;
    private Context mContext;
    private RequestQueue mRequestQueue;
    private Session mSession;

    /* renamed from: com.mlizhi.modules.login.AutoLoginActivity.1 */
    class C03861 implements ErrorListener {
        C03861() {
        }

        public void onErrorResponse(VolleyError error) {
            Toast.makeText(AutoLoginActivity.this.mContext, error.getMessage(), 0).show();
        }
    }

    /* renamed from: com.mlizhi.modules.login.AutoLoginActivity.2 */
    class C03872 implements Listener<String> {
        C03872() {
        }

        public void onResponse(String response) {
            if (Constants.VIA_RESULT_SUCCESS.equals(JsonUtil.getHeaderCode(response))) {
                try {
                    JSONObject userJsonObject = JsonUtil.getBodyJsonObject(response).getJSONObject("customer");
                    String username = userJsonObject.getString("nikeName");
                    if (username == null) {
                        username = bq.f888b;
                    }
                    AutoLoginActivity.this.mSession.setUserName(username);
                    AutoLoginActivity.this.mSession.setUid(userJsonObject.getString(SocializeConstants.WEIBO_ID));
                    String headImgurl = userJsonObject.getString("headImgurl");
                    if (headImgurl == null) {
                        headImgurl = bq.f888b;
                    }
                    AutoLoginActivity.this.mSession.setUserIcon(headImgurl);
                    String address = userJsonObject.getString("address");
                    if (address == null) {
                        address = bq.f888b;
                    }
                    AutoLoginActivity.this.mSession.setUserAddress(address);
                } catch (JSONException e) {
                    Utils.m13E("parse login result info json string error!!!", e);
                }
                AutoLoginActivity.this.startActivity(new Intent(AutoLoginActivity.this.mContext, SpecActivity.class));
                AutoLoginActivity.this.finish();
                return;
            }
            Toast.makeText(AutoLoginActivity.this.mContext, JsonUtil.getHeaderErrorInfo(response), 0).show();
            AutoLoginActivity.this.startActivity(new Intent(AutoLoginActivity.this, LoginActivity.class));
            AutoLoginActivity.this.finish();
        }
    }

    /* renamed from: com.mlizhi.modules.login.AutoLoginActivity.3 */
    class C05323 extends StringRequest {
        C05323(int $anonymous0, String $anonymous1, Listener $anonymous2, ErrorListener $anonymous3) {
            super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
        }

        protected Map<String, String> getParams() throws AuthFailureError {
            String timestamp = SecurityUtil.getTimestamp();
            Map<String, String> params = new HashMap();
            params.put(com.mlizhi.utils.Constants.SMS_MOBILE_NUMBER, AutoLoginActivity.this.mSession.getAccount());
            params.put("password", AutoLoginActivity.this.mSession.getPassword());
            params.put("companyId", Constants.VIA_TO_TYPE_QQ_GROUP);
            params.put(com.mlizhi.utils.Constants.URL_TIMESTAMP, timestamp);
            params.put(com.mlizhi.utils.Constants.URL_KEY, SecurityUtil.getMd5String(timestamp));
            return params;
        }
    }

    public AutoLoginActivity() {
        this.listener4LoginError = new C03861();
        this.listener4LoginSuccess = new C03872();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(C0111R.layout.activity_login_auto);
        this.mContext = this;
        this.mSession = Session.get(getApplicationContext());
        MobclickAgent.updateOnlineConfig(this.mContext);
        AnalyticsConfig.enableEncrypt(true);
        autoLogin();
    }

    private void autoLogin() {
        if (NetWorkManager.getNewInstance().isNetworkConnected(this.mContext)) {
            this.mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new C05323(1, com.mlizhi.utils.Constants.URL_USER_LOGIN_URL, this.listener4LoginSuccess, this.listener4LoginError);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(AsyncHttpClient.DEFAULT_SOCKET_TIMEOUT, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.mRequestQueue.add(stringRequest);
            this.mRequestQueue.start();
            return;
        }
        Toast.makeText(this.mContext, C0111R.string.net_connected_failure, 0).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
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
}
