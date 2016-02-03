package com.mlizhi.modules.spec.setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.mlizhi.base.MlzApplication;
import com.mlizhi.base.Session;
import com.mlizhi.base.VersionManager;
import com.mlizhi.modules.login.LoginActivity;
import com.mlizhi.modules.spec.dao.ContentDao;
import com.mlizhi.modules.spec.dao.DaoSession;
import com.mlizhi.modules.spec.dao.DetectDao;
import com.philips.skincare.skincareprototype.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMSocialService;

import p016u.aly.bq;

public class SpecSettingListActivity extends Activity {
    private ProgressDialog cacheProgressDialog;
    private ContentDao contentDao;
    private Activity currrentActivity;
    private DaoSession daoSession;
    private DetectDao detectDao;
    private Dialog logoutDialog;
    private Context mContext;
    private UMSocialService mController;
    private RequestQueue mRequestQueue;
    private Session mSession;
    private MlzApplication mlzApplication;
    private TextView versionTextView;

    /* renamed from: com.mlizhi.modules.spec.setting.SpecSettingListActivity.1 */
    class C01571 implements OnClickListener {
        C01571() {
        }

        public void onClick(DialogInterface dialog, int which) {
            SpecSettingListActivity.this.mlzApplication.removeActivities();
            MobclickAgent.onEvent(SpecSettingListActivity.this.mContext, "logout");
            SpecSettingListActivity.this.mSession.setUid(bq.f888b);
            SpecSettingListActivity.this.startActivity(new Intent(SpecSettingListActivity.this, LoginActivity.class));
            SpecSettingListActivity.this.logoutDialog.dismiss();
        }
    }

    /* renamed from: com.mlizhi.modules.spec.setting.SpecSettingListActivity.2 */
    class C01582 implements OnClickListener {
        C01582() {
        }

        public void onClick(DialogInterface dialog, int which) {
            SpecSettingListActivity.this.logoutDialog.dismiss();
        }
    }

    /* renamed from: com.mlizhi.modules.spec.setting.SpecSettingListActivity.3 */
    class C01593 implements Runnable {
        C01593() {
        }

        public void run() {
            SpecSettingListActivity.this.cacheProgressDialog.dismiss();
        }
    }


    public SpecSettingListActivity() {
        this.mController = null;
        this.logoutDialog = null;
    }

    @SuppressLint({"NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        this.currrentActivity = this;
        this.mlzApplication = (MlzApplication) getApplication();
        this.mSession = Session.get(getApplicationContext());
        setContentView(R.layout.activity_spec_setting_list);
        MobclickAgent.updateOnlineConfig(this.mContext);
        this.versionTextView = (TextView) findViewById(R.id.id_user_info_version_value);
        this.versionTextView.setText("v" + this.mSession.getVersionName());
        this.mlzApplication.addActivity(this);
        this.daoSession = this.mlzApplication.getDaoSession();
        this.detectDao = this.daoSession.getDetectDao();
        this.contentDao = this.daoSession.getContentDao();
    }

    public void onSettingClick(View view) {
        String uid = this.mSession.getUid();
        switch (view.getId()) {
            case R.id.id_spec_setting_list_back:
                finish();
            case R.id.id_user_info_help_label:
                // TODO - Anukool
               // startActivity(new Intent(this, SpecSettingHelpActivity.class));
                MobclickAgent.onEvent(this.mContext, "viewHelp");
            case R.id.id_user_info_introduce_label:
                // TODO - ANUKOOL
                //startActivity(new Intent(this, SpecSettingIntroActivity.class));
                MobclickAgent.onEvent(this.mContext, "viewIntroduce");
            case R.id.id_user_info_clear_label:
                showCacheProcessDialog();
                MobclickAgent.onEvent(this.mContext, "clearCache");
            case R.id.id_user_info_version_label:
                new VersionManager(this.currrentActivity).showVersionProcessDialog(false);
                MobclickAgent.onEvent(this.mContext, "getNewVersion");
            case R.id.id_setting_feedback_label:
                // TODO - Anukool
                //startActivity(new Intent(this, SpecSettingListFeedBackAskActivity.class));
                MobclickAgent.onEvent(this.mContext, "viewFeedBack");
            case R.id.id_user_info_about_label:
                // TODO - Anukool
              //  startActivity(new Intent(this, SpecSettingAboutActivity.class));
                MobclickAgent.onEvent(this.mContext, "viewAbout");
            case R.id.id_user_logout_label:
                if (uid == null || bq.f888b.equals(uid)) {
                    Toast.makeText(this, "\u4eb2\uff0c\u60a8\u8fd8\u6ca1\u6709\u767b\u5f55\u54e6\uff01", Toast.LENGTH_LONG).show();
                } else {
                    logout();
                }
            default:
        }
    }

    private void logout() {
        this.logoutDialog = new Builder(this.mContext).setTitle(R.string.customer_logout_title).setMessage(R.string.customer_logout_msg).setPositiveButton(R.string.customer_logout_certain, new C01571()).setNegativeButton(R.string.customer_logout_cancel, new C01582()).create();
        this.logoutDialog.show();
    }

    private void showCacheProcessDialog() {
        this.cacheProgressDialog = ProgressDialog.show(this, bq.f888b, "\u6b63\u5728\u6e05\u7406\u7f13\u5b58\u2026\u2026", true, false);
        this.contentDao.deleteAll();
        this.detectDao.deleteAll();
        new Handler().postDelayed(new C01593(), 3000);
    }

    protected void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

}
