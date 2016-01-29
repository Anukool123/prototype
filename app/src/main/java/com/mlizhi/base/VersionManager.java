package com.mlizhi.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.Html;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mlizhi.modules.spec.util.JsonUtil;
import com.philips.skincare.skincareprototype.R;
import com.tencent.connect.common.Constants;
import com.umeng.socialize.net.utils.SocializeProtocolConstants;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import p016u.aly.bq;

public class VersionManager {
    private static final int DOWN_OVER = 2;
    private static final int DOWN_UPDATE = 1;
    private Activity activity;
    private String apkUrl;
    private Thread downLoadThread;
    private Dialog downloadDialog;
    ErrorListener errorListener;
    private boolean implicit;
    private boolean interceptFlag;
    private Handler mHandler;
    private ProgressBar mProgress;
    private RequestQueue mRequestQueue;
    private Runnable mdownApkRunnable;
    private Dialog noticeDialog;
    private int progress;
    private String saveFileName;
    @SuppressLint({"SdCardPath"})
    private String savePath;
    Listener<String> successListener;
    private ProgressDialog versionProgressDialog;

    /* renamed from: com.mlizhi.base.VersionManager.1 */
    class C01141 extends Handler {
        C01141() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case VersionManager.DOWN_UPDATE /*1*/:
                    VersionManager.this.mProgress.setProgress(VersionManager.this.progress);
                case VersionManager.DOWN_OVER /*2*/:
                    VersionManager.this.installApk();
                default:
            }
        }
    }

    /* renamed from: com.mlizhi.base.VersionManager.2 */
    class C01152 implements Runnable {
        C01152() {
        }

        public void run() {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(VersionManager.this.apkUrl).openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                File file = new File(VersionManager.this.savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                FileOutputStream fos = new FileOutputStream(new File(VersionManager.this.saveFileName));
                int count = 0;
                byte[] buf = new byte[AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT];
                do {
                    int numread = is.read(buf);
                    count += numread;
                    VersionManager.this.progress = (int) ((((float) count) / ((float) length)) * 100.0f);
                    VersionManager.this.mHandler.sendEmptyMessage(VersionManager.DOWN_UPDATE);
                    if (numread <= 0) {
                        VersionManager.this.mHandler.sendEmptyMessage(VersionManager.DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!VersionManager.this.interceptFlag);
                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    /* renamed from: com.mlizhi.base.VersionManager.5 */
    class C01165 implements OnClickListener {
        C01165() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            VersionManager.this.showDownloadDialog();
        }
    }

    /* renamed from: com.mlizhi.base.VersionManager.6 */
    class C01176 implements OnClickListener {
        C01176() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    /* renamed from: com.mlizhi.base.VersionManager.7 */
    class C01187 implements OnClickListener {
        C01187() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            VersionManager.this.interceptFlag = true;
        }
    }

    /* renamed from: com.mlizhi.base.VersionManager.3 */
    class C03803 implements Listener<String> {
        C03803() {
        }

        public void onResponse(String response) {
            if (Constants.VIA_RESULT_SUCCESS.equals(JsonUtil.getHeaderCode(response))) {
                if (!VersionManager.this.implicit) {
                    VersionManager.this.versionProgressDialog.dismiss();
                }
                try {
                    JSONObject versionObj = JsonUtil.getBodyJsonObject(response).getJSONObject("version");
                    String versionNo = versionObj.getString("versionNo");
                    String downloadUrl = versionObj.getString("download");
                    String descn = versionObj.getString("descn");
                    if (((int) Float.parseFloat(versionNo)) > Session.get(VersionManager.this.activity).getVersionCode()) {
                        VersionManager.this.showNoticeDialog(downloadUrl, descn);
                        return;
                    } else if (!VersionManager.this.implicit) {
                        Toast.makeText(VersionManager.this.activity, "\u6ca1\u6709\u53d1\u73b0\u65b0\u7248\u672c\uff01\uff01\uff01", 0).show();
                        return;
                    } else {
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            Toast.makeText(VersionManager.this.activity, JsonUtil.getHeaderErrorInfo(response), 0).show();
        }
    }

    /* renamed from: com.mlizhi.base.VersionManager.4 */
    class C03814 implements ErrorListener {
        C03814() {
        }

        public void onErrorResponse(VolleyError error) {
            Toast.makeText(VersionManager.this.activity, error.getMessage(), 0).show();
            if (!VersionManager.this.implicit) {
                VersionManager.this.versionProgressDialog.dismiss();
            }
        }
    }

    /* renamed from: com.mlizhi.base.VersionManager.8 */
    class C05318 extends StringRequest {
        C05318(int $anonymous0, String $anonymous1, Listener $anonymous2, ErrorListener $anonymous3) {
            super($anonymous0, $anonymous1, $anonymous2, $anonymous3);
        }

        protected Map<String, String> getParams() throws AuthFailureError {
            String timestamp = SecurityUtil.getTimestamp();
            Map<String, String> params = new HashMap();
            params.put("companyId", Constants.VIA_TO_TYPE_QQ_GROUP);
            params.put(SocializeProtocolConstants.PROTOCOL_SHARE_TYPE, Constants.VIA_TO_TYPE_QQ_GROUP);
            params.put(com.mlizhi.utils.Constants.URL_TIMESTAMP, timestamp);
            params.put(com.mlizhi.utils.Constants.URL_KEY, SecurityUtil.getMd5String(timestamp));
            return params;
        }
    }

    public VersionManager(Activity activity) {
        this.apkUrl = bq.f888b;
        this.savePath = bq.f888b;
        this.saveFileName = bq.f888b;
        this.interceptFlag = false;
        this.mHandler = new C01141();
        this.mdownApkRunnable = new C01152();
        this.implicit = false;
        this.successListener = new C03803();
        this.errorListener = new C03814();
        this.activity = activity;
    }

    public boolean isExternalStorageWritable() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if ("mounted".equals(state) || "mounted_ro".equals(state)) {
            return true;
        }
        return false;
    }

    public void showNoticeDialog(String downloadUrl, String descn) {
        this.apkUrl = downloadUrl;
        if (isExternalStorageWritable()) {
            this.savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        } else {
            this.savePath = this.activity.getFilesDir().getPath();
        }
        this.saveFileName = this.savePath + downloadUrl.substring(downloadUrl.lastIndexOf("/"), downloadUrl.length());
        Builder builder = new Builder(this.activity);
        builder.setTitle(this.activity.getString(R.string.download_info_title));
        builder.setMessage(Html.fromHtml(descn));
        builder.setPositiveButton(this.activity.getString(R.string.download_info_yes), new C01165());
        builder.setNegativeButton(this.activity.getString(R.string.download_info_no), new C01176());
        this.noticeDialog = builder.create();
        this.noticeDialog.show();
    }

    private void showDownloadDialog() {
        Builder builder = new Builder(new ContextThemeWrapper(this.activity, R.style.MlzThemeDialog));
        builder.setTitle(this.activity.getResources().getString(R.string.download_info_title));
        View v = LayoutInflater.from(this.activity).inflate(R.layout.progress, null);
        this.mProgress = (ProgressBar) v.findViewById(R.id.progress);
        builder.setView(v);
        builder.setNegativeButton(this.activity.getResources().getString(R.string.download_info_concel), new C01187());
        this.downloadDialog = builder.create();
        this.downloadDialog.show();
        downloadApk();
    }

    private void downloadApk() {
        this.downLoadThread = new Thread(this.mdownApkRunnable);
        this.downLoadThread.start();
    }

    private void installApk() {
        File apkfile = new File(this.saveFileName);
        if (apkfile.exists()) {
            Intent i = new Intent("android.intent.action.VIEW");
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            this.activity.startActivity(i);
        }
    }

    public void showVersionProcessDialog(boolean implicit) {
        this.implicit = implicit;
        if (!implicit) {
            this.versionProgressDialog = ProgressDialog.show(this.activity, bq.f888b, "\u6b63\u5728\u83b7\u53d6\u65b0\u7248\u672c\u2026\u2026", true, false);
        }
        if (NetWorkManager.getNewInstance().isNetworkConnected(this.activity)) {
            this.mRequestQueue = Volley.newRequestQueue(this.activity);
            this.mRequestQueue.add(new C05318(DOWN_UPDATE, com.mlizhi.utils.Constants.URL_POST_GET_NEW_VERSION, this.successListener, this.errorListener));
            this.mRequestQueue.start();
            return;
        }
        Toast.makeText(this.activity, R.string.net_connected_failure, 0).show();
    }
}
