package com.mlizhi.modules.spec;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mlizhi.base.VersionManager;
import com.mlizhi.modules.spec.content.SpecContentFragment;
import com.mlizhi.modules.spec.detect.SpecDetectFragment;
import com.mlizhi.modules.spec.detect.SpecDetectFragment.BleBroadcastReceiver;
import com.mlizhi.modules.spec.detect.ble.BleService;
import com.mlizhi.modules.spec.record.SpecRecordFragment;
import com.mlizhi.modules.spec.setting.SpecSettingFragment;
import com.philips.skincare.skincareprototype.R;

@SuppressLint({"NewApi"})
public class SpecActivity extends FragmentActivity implements ISpecInterface {
    private static final int REQUEST_ENABLE_BT_CLICK = 3;
    public static final int SPEC_CONTENT_VIEW_ID = 1;
    public static final int SPEC_DETECT_VIEW_ID = 2;
    public static final int SPEC_RECORD_VIEW_ID = 3;
    public static final int SPEC_SETTING_VIEW_ID = 4;
    private boolean enable;
    FragmentTransaction ft;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private Context mContext;
    public BroadcastReceiver mGattUpdateReceiver;
    private SpecContentFragment specContentFragment;
    private SpecDetectFragment specDetectFragment;
    private ImageView specInfoImageView;
    private RadioGroup specNavRadioGroup;
    private SpecRecordFragment specRecordFragment;
    private SpecSettingFragment specSettingFragment;
    private RelativeLayout specTitleLayout;
    private TextView specTitleTextView;
    private boolean supportBle;

    public SpecActivity() {
        this.enable = false;
        this.supportBle = false;
        this.ft = null;
        this.mGattUpdateReceiver = null;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_spec);
        this.specInfoImageView = (ImageView) findViewById(R.id.id_spec_icon_iv);
        this.specTitleTextView = (TextView) findViewById(R.id.id_spec_title_tv);
        this.specNavRadioGroup = (RadioGroup) findViewById(R.id.id_spec_nav_view_group);
        this.specTitleLayout = (RelativeLayout) findViewById(R.id.id_spec_title_ly);
        initView();
        initBle();
        if (this.supportBle) {
            SpecDetectFragment specDetectFragment = this.specDetectFragment;
            specDetectFragment.getClass();
            this.mGattUpdateReceiver = new BleBroadcastReceiver();
            this.mContext.registerReceiver(this.mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
        new VersionManager(this).showVersionProcessDialog(true);
    }

    private void initView() {
        this.specContentFragment = SpecContentFragment.getNewInstance();
        this.specDetectFragment = SpecDetectFragment.getNewInstance();
        this.specRecordFragment = SpecRecordFragment.getNewInstance();
        this.specSettingFragment = SpecSettingFragment.getNewInstance();
        this.ft = getSupportFragmentManager().beginTransaction();
        this.ft.replace(R.id.id_spec_content_view, this.specDetectFragment);
        this.ft.commit();
        this.specNavRadioGroup.check(R.id.id_nav_detect_btn);
        this.specTitleTextView.setText(R.string.nav_detect);
        this.specInfoImageView.setVisibility(View.VISIBLE);
    }

    public void switchViewController(View view) {
        this.ft = getSupportFragmentManager().beginTransaction();
        this.specInfoImageView.setVisibility(View.GONE);
        switch (view.getId()) {
            case R.id.id_nav_content_btn:
                this.ft.replace(R.id.id_spec_content_view, this.specContentFragment);
                this.specNavRadioGroup.check(R.id.id_nav_content_btn);
                this.specTitleTextView.setText(R.string.nav_content);
                this.specTitleTextView.setTextColor(getResources().getColor(R.color.theme_primary_gray));
                this.specTitleLayout.setBackgroundResource(R.drawable.ic_nav_background);
                break;
            case R.id.id_nav_detect_btn:
                this.ft.replace(R.id.id_spec_content_view, this.specDetectFragment);
                this.specNavRadioGroup.check(R.id.id_nav_detect_btn);
                this.specTitleTextView.setText(R.string.nav_detect);
                this.specInfoImageView.setVisibility(View.VISIBLE);
                this.specTitleTextView.setTextColor(getResources().getColor(R.color.theme_primary_white));
                this.specTitleLayout.setBackgroundColor(0);
                break;
            case R.id.id_nav_record_btn:
                this.ft.replace(R.id.id_spec_content_view, this.specRecordFragment);
                this.specNavRadioGroup.check(R.id.id_nav_record_btn);
                this.specTitleTextView.setText(R.string.nav_record);
                this.specTitleTextView.setTextColor(getResources().getColor(R.color.theme_primary_white));
                this.specTitleLayout.setBackgroundColor(0);
                break;
            case R.id.id_nav_setting_btn:
                this.ft.replace(R.id.id_spec_content_view, this.specSettingFragment);
                this.specNavRadioGroup.check(R.id.id_nav_setting_btn);
                this.specTitleTextView.setText(R.string.nav_setting);
                this.specTitleTextView.setTextColor(getResources().getColor(R.color.theme_primary_white));
                this.specTitleLayout.setBackgroundColor(0);
                break;
            default:
                this.ft.replace(R.id.id_spec_content_view, this.specDetectFragment);
                this.specNavRadioGroup.check(R.id.id_nav_detect_btn);
                this.specTitleTextView.setText(R.string.nav_detect);
                this.specInfoImageView.setVisibility(View.VISIBLE);
                this.specTitleTextView.setTextColor(getResources().getColor(R.color.theme_primary_white));
                this.specTitleLayout.setBackgroundColor(0);
                break;
        }
        this.ft.commit();
    }

    public void onViewClick(View view) {
        // TODO-Anukool
      //  startActivity(new Intent(this, CommonQuestionActivity.class));
    }

    public void switchSpecViewById(int viewId) {
        this.ft = getSupportFragmentManager().beginTransaction();
        this.specInfoImageView.setVisibility(View.GONE);
        this.specNavRadioGroup.setVisibility(View.VISIBLE);
        switch (viewId) {
            case SPEC_CONTENT_VIEW_ID /*1*/:
                this.ft.replace(R.id.id_spec_content_view, this.specContentFragment);
                this.specNavRadioGroup.check(R.id.id_nav_content_btn);
                this.specTitleTextView.setText(R.string.nav_content);
                this.specTitleTextView.setTextColor(getResources().getColor(R.color.theme_primary_gray));
                this.specTitleLayout.setBackgroundResource(R.drawable.ic_nav_background);
                break;
            case SPEC_DETECT_VIEW_ID /*2*/:
                this.ft.replace(R.id.id_spec_content_view, this.specDetectFragment);
                this.specNavRadioGroup.check(R.id.id_nav_detect_btn);
                this.specTitleTextView.setText(R.string.nav_detect);
                this.specInfoImageView.setVisibility(View.VISIBLE);
                this.specTitleTextView.setTextColor(getResources().getColor(R.color.theme_primary_white));
                this.specTitleLayout.setBackgroundColor(0);
                break;
            case SPEC_RECORD_VIEW_ID /*3*/:
                this.ft.replace(R.id.id_spec_content_view, this.specRecordFragment);
                this.specNavRadioGroup.check(R.id.id_nav_record_btn);
                this.specTitleTextView.setText(R.string.nav_record);
                this.specTitleTextView.setTextColor(getResources().getColor(R.color.theme_primary_white));
                this.specTitleLayout.setBackgroundColor(0);
                break;
            case SPEC_SETTING_VIEW_ID /*4*/:
                this.ft.replace(R.id.id_spec_content_view, this.specSettingFragment);
                this.specNavRadioGroup.check(R.id.id_nav_setting_btn);
                this.specTitleTextView.setText(R.string.nav_setting);
                this.specTitleTextView.setTextColor(getResources().getColor(R.color.theme_primary_white));
                this.specTitleLayout.setBackgroundColor(0);
                break;
        }
        this.ft.commit();
    }

    public void initBle() {
        if (VERSION.SDK_INT < 18) {
            this.supportBle = false;
            Toast.makeText(this, R.string.ble_os_version_low, SPEC_CONTENT_VIEW_ID).show();
            return;
        }
        this.mBluetoothManager = (BluetoothManager) this.mContext.getSystemService("bluetooth");
        if (this.mBluetoothManager != null) {
            this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
            if (this.mBluetoothAdapter != null) {
                this.enable = this.mBluetoothAdapter.isEnabled();
                if (this.enable) {
                    this.mContext.startService(new Intent(this.mContext, BleService.class));
                    this.supportBle = true;
                    return;
                }
                ((Activity) this.mContext).startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), SPEC_RECORD_VIEW_ID);
                return;
            }
            this.supportBle = false;
            return;
        }
        this.supportBle = false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEC_RECORD_VIEW_ID && resultCode == 0) {
            this.supportBle = false;
        } else if (requestCode == 16 || requestCode == 49) {
            this.specSettingFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == SPEC_RECORD_VIEW_ID && resultCode == -1) {
            SpecDetectFragment specDetectFragment = this.specDetectFragment;
            specDetectFragment.getClass();
            this.mGattUpdateReceiver = new BleBroadcastReceiver();
            this.mContext.registerReceiver(this.mGattUpdateReceiver, makeGattUpdateIntentFilter());
            this.supportBle = true;
            this.specDetectFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BleService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BleService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BleService.SHOW_SCAN_BTN);
        intentFilter.addAction(BleService.HIDE_SCAN_BTN);
        return intentFilter;
    }

    protected void onDestroy() {
        super.onDestroy();
        if (this.supportBle) {
            this.mContext.unregisterReceiver(this.mGattUpdateReceiver);
            this.mContext.stopService(new Intent(this.mContext, BleService.class));
        }
    }
}
