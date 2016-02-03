package com.mlizhi.modules.login;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

//import com.mlizhi.modules.spec.SpecActivity;

public class AutoLoginActivity extends Activity {
    ErrorListener listener4LoginError;
    Listener<String> listener4LoginSuccess;
    private Context mContext;
    private RequestQueue mRequestQueue;


    /* renamed from: com.mlizhi.modules.login.AutoLoginActivity.2 */
    class C03872 implements Listener<String> {
        C03872() {
        }

        public void onResponse(String response) {
           /* if (Constants.VIA_RESULT_SUCCESS.equals(JsonUtil.getHeaderCode(response))) {
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
                }*/
           // }
            //Toast.makeText(AutoLoginActivity.this.mContext, JsonUtil.getHeaderErrorInfo(response), 0).show();
            //AutoLoginActivity.this.startActivity(new Intent(AutoLoginActivity.this, LoginActivity.class));
           // AutoLoginActivity.this.finish();
        }
    }


    public AutoLoginActivity() {
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_auto);
        this.mContext = this;
        autoLogin();
    }

    private void autoLogin() {
            /*this.mRequestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new C05323(1, com.mlizhi.utils.Constants.URL_USER_LOGIN_URL, this.listener4LoginSuccess, this.listener4LoginError);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(AsyncHttpClient.DEFAULT_SOCKET_TIMEOUT, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            this.mRequestQueue.add(stringRequest);
            this.mRequestQueue.start();
            return;*/


        //TODO-Anukool
        //AutoLoginActivity.this.startActivity(new Intent(AutoLoginActivity.this.mContext, SpecActivity.class));
       // AutoLoginActivity.this.finish();
        return;
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }
}
