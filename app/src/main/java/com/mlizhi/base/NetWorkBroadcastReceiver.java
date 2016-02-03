package com.mlizhi.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetWorkBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (!NetWorkManager.getNewInstance().isNetworkConnected(context)) {
            Toast.makeText(context, R.string.net_connected_failure, 1).show();
        }
    }
}
