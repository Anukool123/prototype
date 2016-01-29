package com.philips.skincare.skincareprototype;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.mlizhi.modules.login.LoginActivity;

public class MainActivity extends Activity {
    private ImageView iconImageView;
    private AnimationDrawable loadAnim;

    /* renamed from: com.mlizhi.MainActivity.1 */
    class C01101 implements Runnable {
        C01101() {
        }

        public void run() {
                    MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    MainActivity.this.finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.iconImageView = (ImageView) findViewById(R.id.auto_loading_anim);
        this.loadAnim = (AnimationDrawable) this.iconImageView.getBackground();
        this.loadAnim.start();
        new Handler().postDelayed(new C01101(), 3500);
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
        this.loadAnim.stop();
    }


    public void testMethod()
    {

    }
}
