package com.mlizhi.modules.spec.detect;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Switch;
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
import com.mlizhi.base.MlzApplication;
import com.mlizhi.base.NetWorkManager;
import com.mlizhi.base.SecurityUtil;
import com.mlizhi.base.Session;
import com.mlizhi.base.imageloader.core.download.BaseImageDownloader;
import com.mlizhi.base.upyun.block.api.common.Params;
import com.mlizhi.modules.spec.ISpecInterface;
import com.mlizhi.modules.spec.dao.DaoSession;
import com.mlizhi.modules.spec.dao.DetectDao;
import com.mlizhi.modules.spec.dao.model.DetectModel;
import com.mlizhi.modules.spec.detect.ble.BleService;
import com.mlizhi.modules.spec.util.JsonUtil;
import com.mlizhi.widgets.wave.CircularView;
import com.philips.skincare.skincareprototype.R;
import com.tencent.connect.common.Constants;

import org.java_websocket.framing.CloseFrame;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import p016u.aly.bq;

@SuppressLint({"NewApi"})
public class SpecDetectFragment extends Fragment {
    public static final int BLE_DETECT_FAILED_TIMELESS = 4102;
    public static final int BLE_DETECT_FAILED_VALUE_LOW = 4103;
    public static final int BLE_DETECT_WATER_VALUE = 4104;
    private static final int BLE_FLAG_CONNECTED = 4099;
    private static final int BLE_FLAG_DATA_AVAILABLE = 4101;
    private static final int BLE_FLAG_DISCONNECT = 4098;
    private static final int BLE_FLAG_DISCOVER_SERVICE = 4100;
    private static final int BLE_FLAG_SEARCH_START = 4096;
    private static final int BLE_FLAG_SEARCH_STOP = 4097;
    public static final int BLE_FLAG_START_DETECT = 4113;
    private static SpecDetectFragment specDetectFragment;
    Timer animTimer;
    private Animation animation2in;
    private Animation animation2out;
    private CircularView circularView;
    private DaoSession daoSession;
    private DetectDao detectDao;
    private TextView detectResultValue;
    private TextView detectStatus;
    Listener<String> listener4success;
    private Activity mContext;
    private Handler mHandler;
    private RequestQueue mRequestQueue;
    private Session mSession;
    private MlzApplication mlzApplication;
    private Switch nurserTypeSwitch;
    private TextView nurserTypeSwitchLabel;
    private ObjectAnimator objAnimator;
    private RadioGroup partTypeGroup;
    private LinearLayout popResult;
    double receivedWaterData;
    private View rootView;
    private ISpecInterface specCallback;
    private int tempNurserType;
    private int tempPartType;
    private HashMap<String, String> tempValueMap;
    private List<HashMap<String, String>> tempValueMapList;
    float tempWaterValue;
    int waterValue;

    /* renamed from: com.mlizhi.modules.spec.detect.SpecDetectFragment.1 */
    class C01351 implements Callback {
        C01351() {
        }

        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case SpecDetectFragment.BLE_FLAG_SEARCH_START /*4096*/:
                    SpecDetectFragment.this.detectStatus.setText(R.string.device_status_start_scan);
                    SpecDetectFragment.this.circularView.setClickable(false);
                    break;
                case SpecDetectFragment.BLE_FLAG_SEARCH_STOP /*4097*/:
                    SpecDetectFragment.this.circularView.setClickable(true);
                    SpecDetectFragment.this.detectStatus.setText(R.string.device_status_stop_scan);
                    break;
                case SpecDetectFragment.BLE_FLAG_DISCONNECT /*4098*/:
                    SpecDetectFragment.this.detectStatus.setText(R.string.device_status_not_connected);
                    SpecDetectFragment.this.circularView.setClickable(true);
                    break;
                case SpecDetectFragment.BLE_FLAG_CONNECTED /*4099*/:
                    SpecDetectFragment.this.detectStatus.setText(R.string.device_status_only_connected);
                    break;
                case SpecDetectFragment.BLE_FLAG_DISCOVER_SERVICE /*4100*/:
                    SpecDetectFragment.this.detectStatus.setText(R.string.device_status_connecting);
                    break;
                case SpecDetectFragment.BLE_FLAG_DATA_AVAILABLE /*4101*/:
                    SpecDetectFragment.this.detectStatus.setText(R.string.device_status_detecting);
                    SpecDetectFragment.this.detectResultValue.setText(String.valueOf(new StringBuilder(String.valueOf(Math.ceil((double) (SpecDetectFragment.this.tempWaterValue * 10.0f)) / 10.0d)).append("%").toString()));
                    break;
                case SpecDetectFragment.BLE_DETECT_FAILED_TIMELESS /*4102*/:
                    SpecDetectFragment.this.detectStatus.setText(R.string.device_status_detecting_less_time);
                    SpecDetectFragment.this.detectResultValue.setText("0.0%");
                    break;
                case SpecDetectFragment.BLE_DETECT_FAILED_VALUE_LOW /*4103*/:
                    SpecDetectFragment.this.detectStatus.setText(R.string.device_status_detecting_low_value);
                    SpecDetectFragment.this.detectResultValue.setText("0.0%");
                    break;
                case SpecDetectFragment.BLE_DETECT_WATER_VALUE /*4104*/:
                    SpecDetectFragment.this.waterValue = ((Integer) msg.obj).intValue();
                    float water = ((float) ((int) (((float) SpecDetectFragment.this.waterValue) / 10.0f))) * 0.1f;
                    SpecDetectFragment.this.detectStatus.setText(SpecDetectFragment.this.mContext.getString(R.string.device_status_success_water_value));
                    SpecDetectFragment.this.detectResultValue.setText(water + "%");
                    SpecDetectFragment.this.popResult.setVisibility(View.INVISIBLE);
                    SpecDetectFragment.this.popResult.startAnimation(SpecDetectFragment.this.animation2in);
                    if (SpecDetectFragment.this.tempNurserType == 21) {
                        SpecDetectFragment.this.tempValueMap.put("beforeValue", String.valueOf(water));
                    } else if (SpecDetectFragment.this.tempNurserType == 22) {
                        SpecDetectFragment.this.tempValueMap.put("afterValue", String.valueOf(water));
                    }
                    SpecDetectFragment.this.saveDetectResult();
                    break;
                case SpecDetectFragment.BLE_FLAG_START_DETECT /*4113*/:
                    SpecDetectFragment.this.detectStatus.setText("\u5f00\u59cb\u68c0\u6d4b");
                    if (SpecDetectFragment.this.circularView != null) {
                        SpecDetectFragment.this.animate(SpecDetectFragment.this.circularView, null);
                        break;
                    }
                    break;
            }
            return false;
        }
    }

    /* renamed from: com.mlizhi.modules.spec.detect.SpecDetectFragment.3 */
    class C01363 implements OnGlobalLayoutListener {
        C01363() {
        }

        public void onGlobalLayout() {
            SpecDetectFragment.this.circularView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            SpecDetectFragment.this.circularView.startWave();
        }
    }

    /* renamed from: com.mlizhi.modules.spec.detect.SpecDetectFragment.4 */
    class C01374 implements OnClickListener {
        C01374() {
        }

        public void onClick(View v) {
            if (SpecDetectFragment.this.popResult != null) {
                SpecDetectFragment.this.popResult.setVisibility(View.GONE);
            }
        }
    }

    /* renamed from: com.mlizhi.modules.spec.detect.SpecDetectFragment.5 */
    class C01385 implements OnClickListener {
        C01385() {
        }

        public void onClick(View v) {
            SpecDetectFragment.this.mContext.startService(new Intent(SpecDetectFragment.this.mContext, BleService.class));
        }
    }

    /* renamed from: com.mlizhi.modules.spec.detect.SpecDetectFragment.6 */
    class C01396 implements OnClickListener {
        C01396() {
        }

        public void onClick(View v) {
            SpecDetectFragment.this.popResult.startAnimation(SpecDetectFragment.this.animation2out);
            SpecDetectFragment.this.popResult.setVisibility(View.GONE);
            Intent intent = new Intent(SpecDetectFragment.this.mContext, SpecDetectResultActivity.class);
            intent.putExtra("current_water_value", ((float) ((int) (((float) SpecDetectFragment.this.waterValue) / 10.0f))) * 0.1f);
            intent.putExtra("nurser_type", SpecDetectFragment.this.tempNurserType);
            SpecDetectFragment.this.mContext.startActivity(intent);
        }
    }

    /* renamed from: com.mlizhi.modules.spec.detect.SpecDetectFragment.7 */
    class C01407 implements OnCheckedChangeListener {
        C01407() {
        }

        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.id_detect_pos_face:
                    SpecDetectFragment.this.tempPartType = 17;
                    SpecDetectFragment.this.tempValueMap.put(SocializeProtocolConstants.PROTOCOL_SHARE_TYPE, Constants.VIA_TO_TYPE_QQ_GROUP);
                    break;
                case R.id.id_detect_pos_eye:
                    SpecDetectFragment.this.tempPartType = 18;
                    SpecDetectFragment.this.tempValueMap.put(SocializeProtocolConstants.PROTOCOL_SHARE_TYPE, Constants.VIA_TO_TYPE_QQ_DISCUSS_GROUP);
                    break;
                case R.id.id_detect_pos_neck:
                    SpecDetectFragment.this.tempPartType = 20;
                    SpecDetectFragment.this.tempValueMap.put(SocializeProtocolConstants.PROTOCOL_SHARE_TYPE, Constants.VIA_SHARE_TYPE_TEXT);
                    break;
                case R.id.id_detect_pos_hand:
                    SpecDetectFragment.this.tempPartType = 19;
                    SpecDetectFragment.this.tempValueMap.put(SocializeProtocolConstants.PROTOCOL_SHARE_TYPE, Constants.VIA_SSO_LOGIN);
                    break;
                default:
                    SpecDetectFragment.this.tempPartType = 19;
                    break;
            }
            SpecDetectFragment.this.mlzApplication.putGlobalVariable("current_part_type", String.valueOf(SpecDetectFragment.this.tempPartType));
        }
    }

    /* renamed from: com.mlizhi.modules.spec.detect.SpecDetectFragment.8 */
    class C01418 implements CompoundButton.OnCheckedChangeListener {
        C01418() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                SpecDetectFragment.this.tempNurserType = 22;
                SpecDetectFragment.this.nurserTypeSwitchLabel.setText(R.string.nurser_status_post);
                SpecDetectFragment.this.circularView.setProgress(0.0f);
                SpecDetectFragment.this.detectResultValue.setText("0.0%");
            } else {
                SpecDetectFragment.this.tempNurserType = 21;
                SpecDetectFragment.this.nurserTypeSwitchLabel.setText(R.string.nurser_status_pre);
                SpecDetectFragment.this.circularView.setProgress(0.0f);
                SpecDetectFragment.this.detectResultValue.setText("0.0%");
            }
            if (SpecDetectFragment.this.popResult != null) {
                SpecDetectFragment.this.popResult.setVisibility(View.GONE);
            }
            SpecDetectFragment.this.mlzApplication.putGlobalVariable("current_nurser_status", String.valueOf(SpecDetectFragment.this.tempNurserType));
        }
    }

    /* renamed from: com.mlizhi.modules.spec.detect.SpecDetectFragment.9 */
    class C01429 implements AnimatorListener {
        private final /* synthetic */ CircularView val$circularView;

        C01429(CircularView circularView) {
            this.val$circularView = circularView;
        }

        public void onAnimationCancel(Animator animation) {
            this.val$circularView.setProgress(0.0f);
        }

        public void onAnimationEnd(Animator animation) {
            this.val$circularView.setProgress(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }

        public void onAnimationRepeat(Animator animation) {
        }

        public void onAnimationStart(Animator animation) {
            this.val$circularView.setProgress(0.0f);
        }
    }

    public class BleBroadcastReceiver extends BroadcastReceiver {
        Message msg;
        boolean received;

        /* renamed from: com.mlizhi.modules.spec.detect.SpecDetectFragment.BleBroadcastReceiver.1 */
        class C01431 extends TimerTask {
            C01431() {
            }

            public void run() {
                BleBroadcastReceiver.this.msg = SpecDetectFragment.this.mHandler.obtainMessage();
                BleBroadcastReceiver.this.msg.what = SpecDetectFragment.BLE_FLAG_DATA_AVAILABLE;
                SpecDetectFragment.this.mHandler.sendMessage(BleBroadcastReceiver.this.msg);
                SpecDetectFragment access$0;
                if (SpecDetectFragment.this.tempWaterValue <= com.mlizhi.utils.Constants.BODY_PART_MIN) {
                    access$0 = SpecDetectFragment.this;
                    access$0.tempWaterValue += 0.27f;
                } else if (SpecDetectFragment.this.tempWaterValue > com.mlizhi.utils.Constants.BODY_PART_MIN && SpecDetectFragment.this.receivedWaterData > 0.0d && SpecDetectFragment.this.tempWaterValue <= ((float) SpecDetectFragment.this.receivedWaterData)) {
                    access$0 = SpecDetectFragment.this;
                    access$0.tempWaterValue += (((float) SpecDetectFragment.this.receivedWaterData) - com.mlizhi.utils.Constants.BODY_PART_MIN) / 15.0f;
                } else if (SpecDetectFragment.this.receivedWaterData > 0.0d && SpecDetectFragment.this.tempWaterValue >= ((float) SpecDetectFragment.this.receivedWaterData)) {
                    if (SpecDetectFragment.this.animTimer != null) {
                        SpecDetectFragment.this.animTimer.cancel();
                    }
                    SpecDetectFragment.this.saveDetectValue();
                    BleBroadcastReceiver.this.msg = SpecDetectFragment.this.mHandler.obtainMessage();
                    BleBroadcastReceiver.this.msg.what = SpecDetectFragment.BLE_DETECT_WATER_VALUE;
                    BleBroadcastReceiver.this.msg.obj = Integer.valueOf(((int) SpecDetectFragment.this.receivedWaterData) * 100);
                    SpecDetectFragment.this.mHandler.sendMessage(BleBroadcastReceiver.this.msg);
                }
            }
        }

        BleBroadcastReceiver() {
            this.received = false;
            this.msg = null;
        }

        @SuppressLint({"HandlerLeak"})
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BleService.HIDE_SCAN_BTN.equals(action)) {
                this.msg = SpecDetectFragment.this.mHandler.obtainMessage();
                this.msg.what = SpecDetectFragment.BLE_FLAG_SEARCH_START;
                SpecDetectFragment.this.mHandler.sendMessage(this.msg);
            } else if (BleService.SHOW_SCAN_BTN.equals(action)) {
                this.msg = SpecDetectFragment.this.mHandler.obtainMessage();
                this.msg.what = SpecDetectFragment.BLE_FLAG_SEARCH_STOP;
                SpecDetectFragment.this.mHandler.sendMessage(this.msg);
            } else if (BleService.ACTION_GATT_DISCONNECTED.equals(action)) {
                this.msg = SpecDetectFragment.this.mHandler.obtainMessage();
                this.msg.what = SpecDetectFragment.BLE_FLAG_DISCONNECT;
                SpecDetectFragment.this.mHandler.sendMessage(this.msg);
                if (SpecDetectFragment.this.animTimer != null) {
                    SpecDetectFragment.this.animTimer.cancel();
                }
            } else if (BleService.ACTION_GATT_CONNECTED.equals(action)) {
                this.msg = SpecDetectFragment.this.mHandler.obtainMessage();
                this.msg.what = SpecDetectFragment.BLE_FLAG_CONNECTED;
                SpecDetectFragment.this.mHandler.sendMessage(this.msg);
            } else if (BleService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                this.msg = SpecDetectFragment.this.mHandler.obtainMessage();
                this.msg.what = SpecDetectFragment.BLE_FLAG_DISCOVER_SERVICE;
                SpecDetectFragment.this.mHandler.sendMessage(this.msg);
            } else if (BleService.ACTION_DATA_AVAILABLE.equals(action)) {
                int data = intent.getIntExtra(BleService.EXTRA_DATA, 0);
                if (!this.received && data == 2) {
                    this.received = true;
                    SpecDetectFragment.this.tempWaterValue = 0.0f;
                    this.msg = SpecDetectFragment.this.mHandler.obtainMessage();
                    this.msg.what = SpecDetectFragment.BLE_FLAG_START_DETECT;
                    SpecDetectFragment.this.mHandler.sendMessage(this.msg);
                    SpecDetectFragment.this.animTimer = new Timer();
                    SpecDetectFragment.this.animTimer.schedule(new C01431(), 0, 50);
                } else if (this.received && data == 3) {
                    this.msg = SpecDetectFragment.this.mHandler.obtainMessage();
                    this.msg.what = SpecDetectFragment.BLE_DETECT_FAILED_TIMELESS;
                    SpecDetectFragment.this.mHandler.sendMessage(this.msg);
                    if (SpecDetectFragment.this.objAnimator != null) {
                        SpecDetectFragment.this.objAnimator.cancel();
                        SpecDetectFragment.this.circularView.setProgress(0.0f);
                    }
                    if (SpecDetectFragment.this.animTimer != null) {
                        SpecDetectFragment.this.animTimer.cancel();
                    }
                    this.received = false;
                } else if (this.received && data != 0 && data != 2 && data != 3) {
                    this.received = false;
                    if (data < BaseImageDownloader.DEFAULT_HTTP_READ_TIMEOUT) {
                        this.msg = SpecDetectFragment.this.mHandler.obtainMessage();
                        this.msg.what = SpecDetectFragment.BLE_DETECT_FAILED_VALUE_LOW;
                        SpecDetectFragment.this.mHandler.sendMessage(this.msg);
                        if (SpecDetectFragment.this.objAnimator != null) {
                            SpecDetectFragment.this.objAnimator.cancel();
                            SpecDetectFragment.this.circularView.setProgress(0.0f);
                        }
                        if (SpecDetectFragment.this.animTimer != null) {
                            SpecDetectFragment.this.animTimer.cancel();
                        }
                    } else if (data >= BaseImageDownloader.DEFAULT_HTTP_READ_TIMEOUT && data < 60000) {
                        SpecDetectFragment.this.receivedWaterData = (double) (data / CloseFrame.NORMAL);
                    }
                }
            }
        }
    }

    /* renamed from: com.mlizhi.modules.spec.detect.SpecDetectFragment.2 */
    class C04092 implements Listener<String> {
        C04092() {
        }

        public void onResponse(String response) {
            if (!Constants.VIA_RESULT_SUCCESS.equals(JsonUtil.getHeaderCode(response))) {
                Toast.makeText(SpecDetectFragment.this.mContext, JsonUtil.getHeaderErrorInfo(response), Toast.LENGTH_LONG).show();
            }
        }
    }

    /* renamed from: com.mlizhi.modules.spec.detect.SpecDetectFragment.12 */
    class AnonymousClass12 extends StringRequest {
        AnonymousClass12(int $anonymous0, String $anonymous1, Listener $anonymous2, ErrorListener $anonymous3) {
            super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
        }

        protected Map<String, String> getParams() throws AuthFailureError {
            String timestamp = SecurityUtil.getTimestamp();
            Map<String, String> params = new HashMap();
            params.put("device", Constants.VIA_TO_TYPE_QQ_GROUP);
            params.put("customerId", SpecDetectFragment.this.mSession.getUid());
            SpecDetectFragment.this.tempValueMap.put("top", Constants.VIA_RESULT_SUCCESS);
            SpecDetectFragment.this.tempValueMap.put("productId", "-1");
            SpecDetectFragment.this.tempValueMap.put("avg", Constants.VIA_RESULT_SUCCESS);
            SpecDetectFragment.this.tempValueMap.put("hardwareType", Constants.VIA_TO_TYPE_QQ_GROUP);
            if (SpecDetectFragment.this.tempValueMapList.size() > 0) {
                params.put("detectionLists", new JSONArray(SpecDetectFragment.this.tempValueMapList).toString());
            }
            params.put(com.mlizhi.utils.Constants.URL_TIMESTAMP, timestamp);
            params.put(com.mlizhi.utils.Constants.URL_KEY, SecurityUtil.getMd5String(timestamp));
            return params;
        }
    }

    public SpecDetectFragment() {
        this.mSession = null;
        this.mlzApplication = null;
        this.tempPartType = 19;
        this.tempNurserType = 21;
        this.tempValueMapList = new ArrayList();
        this.tempValueMap = new HashMap();
        this.waterValue = 0;
        this.receivedWaterData = 0.0d;
        this.tempWaterValue = 0.0f;
        this.mHandler = new Handler(new C01351());
        this.animTimer = null;
        this.listener4success = new C04092();
    }

    public static SpecDetectFragment getNewInstance() {
        if (specDetectFragment == null) {
            specDetectFragment = new SpecDetectFragment();
        }
        return specDetectFragment;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
        this.mSession = Session.get(getActivity().getApplicationContext());
        this.mlzApplication = (MlzApplication) getActivity().getApplication();
        this.daoSession = this.mlzApplication.getDaoSession();
        this.detectDao = this.daoSession.getDetectDao();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.rootView == null) {
            this.rootView = inflater.inflate(R.layout.fragment_spec_detect, container, false);
        }
        ViewGroup parent = (ViewGroup) this.rootView.getParent();
        if (parent != null) {
            parent.removeView(this.rootView);
        }
        this.circularView = (CircularView) this.rootView.findViewById(R.id.id_water_circular_view);
        this.circularView.setLayerType(1, null);
        this.detectStatus = (TextView) this.rootView.findViewById(R.id.id_detect_status);
        this.detectResultValue = (TextView) this.rootView.findViewById(R.id.id_detect_result_value);
        this.popResult = (LinearLayout) this.rootView.findViewById(R.id.pop_result);
        this.partTypeGroup = (RadioGroup) this.rootView.findViewById(R.id.id_detect_pos_group);
        this.nurserTypeSwitch = (Switch) this.rootView.findViewById(R.id.id_nurser_status);
        this.nurserTypeSwitchLabel = (TextView) this.rootView.findViewById(R.id.id_nurser_status_label);
        this.circularView.getViewTreeObserver().addOnGlobalLayoutListener(new C01363());
        return this.rootView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.specCallback = (ISpecInterface) getActivity();
        this.animation2in = AnimationUtils.loadAnimation(this.mContext, R.anim.in_from_down);
        this.animation2in.setDuration(1000);
        this.animation2out = AnimationUtils.loadAnimation(this.mContext, R.anim.out_to_down);
        this.animation2out.setDuration(1000);
        this.rootView.setOnClickListener(new C01374());
        this.circularView.setOnClickListener(new C01385());
        this.popResult.setOnClickListener(new C01396());
        this.tempValueMap.put(SocializeProtocolConstants.PROTOCOL_SHARE_TYPE, Constants.VIA_SSO_LOGIN);
        this.mlzApplication.putGlobalVariable("current_part_type", String.valueOf(this.tempPartType));
        this.partTypeGroup.setOnCheckedChangeListener(new C01407());
        this.mlzApplication.putGlobalVariable("current_nurser_status", String.valueOf(this.tempNurserType));
        this.nurserTypeSwitch.setOnCheckedChangeListener(new C01418());
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        if (this.popResult != null) {
            this.popResult.setVisibility(View.GONE);
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public void onDetach() {
        super.onDetach();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.mContext.startService(new Intent(this.mContext, BleService.class));
    }

    private void animate(CircularView circularView, AnimatorListener listener) {
        circularView.setProgress(0.0f);
        animate(circularView, listener, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT, 6000);
    }

    private void animate(CircularView circularView, AnimatorListener listener, float progress, int duration) {
        if (this.objAnimator == null) {
            this.objAnimator = ObjectAnimator.ofFloat(circularView, Notification.CATEGORY_PROGRESS, new float[]{progress});
        }
        this.objAnimator.setDuration((long) duration);
        this.objAnimator.addListener(new C01429(circularView));
        if (listener != null) {
            this.objAnimator.addListener(listener);
        }
        this.objAnimator.addUpdateListener(new AnimatorUpdateListener() {
            float count;

            {
                this.count = 0.0f;
            }

            public void onAnimationUpdate(ValueAnimator animation) {
            }
        });
        this.objAnimator.start();
    }

    private void saveDetectValue() {
        DetectModel entity = new DetectModel();
        entity.setDetectType(23);
        entity.setDetectValue(this.receivedWaterData * 0.1d);
        entity.setId(null);
        entity.setInsertTime(new Date());
        entity.setNurserType(this.tempNurserType);
        entity.setPartType(this.tempPartType);
        String userId = this.mSession.getUid();
        if (userId == null || bq.f888b.equals(userId)) {
            userId = this.mSession.getMac();
        }
        entity.setUserId(userId);
        this.detectDao.insert(entity);
    }

    private void saveDetectResult() {
        if (NetWorkManager.getNewInstance().isNetworkConnected(this.mContext)) {
            this.mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            this.mRequestQueue.add(new AnonymousClass12(1, com.mlizhi.utils.Constants.URL_POST_DETECT_WATER_VALUE, this.listener4success, new ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(SpecDetectFragment.this.mContext, "aaaaaaaaa", Toast.LENGTH_LONG).show();
                }
            }));
            this.mRequestQueue.start();
            return;
        }
        Toast.makeText(this.mContext, R.string.net_connected_failure, Toast.LENGTH_LONG).show();
    }

     static class SocializeProtocolConstants {
        public static final String PROTOCOL_KEY_ACCESSTOKEN = "access_token";
        public static final String PROTOCOL_KEY_ACCOUNT = "acs";
        public static final String PROTOCOL_KEY_ACCOUNTS = "accounts";
        public static final String PROTOCOL_KEY_ACCOUNT_EVENTS = "acvs";
        public static final String PROTOCOL_KEY_ACCOUNT_EVENT_ACTION = "bh";
        public static final String PROTOCOL_KEY_AK = "ak";
        public static final String PROTOCOL_KEY_APP_ID = "app_id";
        public static final String PROTOCOL_KEY_APP_KEY = "app_secret";
        public static final String PROTOCOL_KEY_APP_NAME = "app_name";
        public static final String PROTOCOL_KEY_APP_WEBSITE = "app_website";
        public static String PROTOCOL_KEY_AUTHOR = null;
        public static final String PROTOCOL_KEY_BIRTHDAY = "birthday";
        public static final String PROTOCOL_KEY_COMMENTS = "cms";
        public static final String PROTOCOL_KEY_COMMENT_COUNT = "cm";
        public static String PROTOCOL_KEY_COMMENT_TEMPLATE = null;
        public static String PROTOCOL_KEY_COMMENT_TEXT = null;
        public static String PROTOCOL_KEY_CUSTOM_ID = null;
        public static final String PROTOCOL_KEY_DATA = "data";
        public static final String PROTOCOL_KEY_DE = "de";
        public static final String PROTOCOL_KEY_DEFAULT = "default";
        public static final String PROTOCOL_KEY_DEFAULT_ACCOUNT = "cu";
        public static final String PROTOCOL_KEY_DESCRIPTOR = "dc";
        public static final String PROTOCOL_KEY_DT = "dt";
        public static final String PROTOCOL_KEY_EN = "en";
        public static final String PROTOCOL_KEY_ENTITY_KEY = "ek";
        public static String PROTOCOL_KEY_ENTITY_NAME = null;
        public static final String PROTOCOL_KEY_ERRC = "err_code";
        public static final String PROTOCOL_KEY_EXPIRE_IN = "expires_in";
        public static final String PROTOCOL_KEY_EXTEND = "ext";
        public static final String PROTOCOL_KEY_EXTEND_ARGS = "extend";
        public static final String PROTOCOL_KEY_FR = "fr";
        public static final String PROTOCOL_KEY_FRIENDS_ICON = "profile_image_url";
        public static final String PROTOCOL_KEY_FRIENDS_LINKNAME = "link_name";
        public static final String PROTOCOL_KEY_FRIENDS_NAME = "name";
        public static final String PROTOCOL_KEY_FRIENDS_PINYIN = "pinyin";
        public static final String PROTOCOL_KEY_FRIST_TIME = "ft";
        public static String PROTOCOL_KEY_FTYPE = null;
        public static String PROTOCOL_KEY_FURL = null;
        public static final String PROTOCOL_KEY_GENDER = "gender";
        public static String PROTOCOL_KEY_IMAGE = null;
        public static final String PROTOCOL_KEY_IMEI = "imei";
        public static final String PROTOCOL_KEY_LAST_COMMENT_TIME = "lct";
        public static final String PROTOCOL_KEY_LIKE_COUNT = "lk";
        public static String PROTOCOL_KEY_LOCATION = null;
        public static final String PROTOCOL_KEY_LOGINACC = "loginaccount";
        public static final String PROTOCOL_KEY_MAC = "mac";
        public static final String PROTOCOL_KEY_MD5IMEI = "md5imei";
        public static final String PROTOCOL_KEY_MSG = "msg";
        public static String PROTOCOL_KEY_NEW_INSTALL = null;
        public static String PROTOCOL_KEY_NICK_NAME = null;
        public static final String PROTOCOL_KEY_OPENID = "openid";
        public static final String PROTOCOL_KEY_OPENUDID = "openudid";
        public static final String PROTOCOL_KEY_OPID = "opid";
        public static final String PROTOCOL_KEY_OS = "os";
        public static final String PROTOCOL_KEY_PLATFORM = "platforms";
        public static final String PROTOCOL_KEY_PLATFORM_ERROR = "platform_error";
        public static final String PROTOCOL_KEY_PROFILE_URL = "profile_url";
        public static final String PROTOCOL_KEY_PV = "pv";
        public static final String PROTOCOL_KEY_QQZONE_UID = "usid";
        public static final String PROTOCOL_KEY_QZONE_ID = "qzone_key";
        public static final String PROTOCOL_KEY_QZONE_SECRET = "qzone_secret";
        public static final String PROTOCOL_KEY_REFRESH_TOKEN = "refresh_token";
        public static final String PROTOCOL_KEY_REQUEST_TYPE = "tp";
        public static final String PROTOCOL_KEY_SCOPE = "scope";
        public static final String PROTOCOL_KEY_SHARE_FOLLOWS = "fusid";
        public static final String PROTOCOL_KEY_SHARE_NUM = "sn";
        public static final String PROTOCOL_KEY_SHARE_SNS = "sns";
        public static final String PROTOCOL_KEY_SHARE_TO = "to";
        public static final String PROTOCOL_KEY_SHARE_USID = "usid";
        public static final String PROTOCOL_KEY_SID = "sid";
        public static final String PROTOCOL_KEY_SNS = "sns";
        public static final String PROTOCOL_KEY_SNSACCOUNT_ICON = "pt";
        public static final String PROTOCOL_KEY_ST = "st";
        public static String PROTOCOL_KEY_STATUS = null;
        public static final String PROTOCOL_KEY_TENCENT = "tencent";
        public static String PROTOCOL_KEY_THUMB = null;
        public static String PROTOCOL_KEY_TITLE = null;
        public static final String PROTOCOL_KEY_UDID = "udid";
        public static final String PROTOCOL_KEY_UID = "uid";
        public static final String PROTOCOL_KEY_UMENG_SECRET = "umeng_secret";
        public static final String PROTOCOL_KEY_USER_ICON = "ic";
        public static final String PROTOCOL_KEY_USER_ICON2 = "icon";
        public static final String PROTOCOL_KEY_USER_NAME = "uname";
        public static final String PROTOCOL_KEY_USER_NAME2 = "username";
        public static final String PROTOCOL_KEY_VERIFY_MEDIA = "via";
        public static final String PROTOCOL_KEY_VERSION = "sdkv";
        public static final String PROTOCOL_KEY_WEIBOID = "wid";
        public static final String PROTOCOL_KEY_WX_APPID = "wxsession_key";
        public static final String PROTOCOL_KEY_WX_SECRET = "wxsession_secret";
        public static final String PROTOCOL_NORMAL_SHARE = "normal";
        public static final String PROTOCOL_SHAKE_SHARE = "shake";
        public static final String PROTOCOL_SHARE_TYPE = "type";
        public static String PROTOCOL_VERSION;

        static {
            PROTOCOL_KEY_COMMENT_TEXT = "ct";
            PROTOCOL_KEY_LOCATION = "lc";
            PROTOCOL_KEY_STATUS = Params.STATUS;
            PROTOCOL_KEY_IMAGE = "pic";
            PROTOCOL_KEY_NICK_NAME = "nname";
            PROTOCOL_KEY_COMMENT_TEMPLATE = "tpt";
            PROTOCOL_KEY_FURL = "furl";
            PROTOCOL_KEY_FTYPE = "ftype";
            PROTOCOL_KEY_TITLE = "title";
            PROTOCOL_KEY_THUMB = "thumb";
            PROTOCOL_KEY_AUTHOR = "author";
            PROTOCOL_VERSION = "pcv";
            PROTOCOL_KEY_NEW_INSTALL = "ni";
            PROTOCOL_KEY_CUSTOM_ID = "cuid";
            PROTOCOL_KEY_ENTITY_NAME = PROTOCOL_KEY_FRIENDS_NAME;
        }
    }
}
