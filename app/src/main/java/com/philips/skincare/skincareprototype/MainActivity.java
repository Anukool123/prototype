package com.philips.skincare.skincareprototype;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import com.mlizhi.base.Session;
import com.mlizhi.modules.login.AutoLoginActivity;
import com.mlizhi.modules.login.LoginActivity;
import com.mlizhi.modules.splash.SplashActivity;
import com.umeng.analytics.MobclickAgent;
import p016u.aly.bq;

public class MainActivity extends Activity {
    private ImageView iconImageView;
    private AnimationDrawable loadAnim;
    private Session mSession;

    /* renamed from: com.mlizhi.MainActivity.1 */
    class C01101 implements Runnable {
        C01101() {
        }

        public void run() {
            long splashId = MainActivity.this.mSession.getSplashId();
            if (splashId == -1) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, SplashActivity.class));
                MainActivity.this.finish();
            } else if (splashId == 1) {
                String uid = MainActivity.this.mSession.getUid();
                if (uid == null || bq.f888b.equals(uid)) {
                    MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    MainActivity.this.finish();
                    return;
                }
                MainActivity.this.startActivity(new Intent(MainActivity.this, AutoLoginActivity.class));
                MainActivity.this.finish();
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSession = Session.get(getApplicationContext());
        setContentView(C0111R.layout.activity_main);
        this.iconImageView = (ImageView) findViewById(C0111R.id.auto_loading_anim);
        this.loadAnim = (AnimationDrawable) this.iconImageView.getBackground();
        this.loadAnim.start();
        new Handler().postDelayed(new C01101(), 3500);
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
        this.loadAnim.stop();
    }
}
