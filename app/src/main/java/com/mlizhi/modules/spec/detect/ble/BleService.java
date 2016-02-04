package com.mlizhi.modules.spec.detect.ble;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothAdapter.LeScanCallback;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.philips.skincare.skincareprototype.R;
import com.tencent.connect.common.Constants;


import java.util.List;
import java.util.UUID;

@SuppressLint({"NewApi"})
public class BleService extends Service {
    public static final String ACTION_DATA_AVAILABLE = "com.mlzfg.bluetooth.le.ACTION_DATA_AVAILABLE";
    public static final String ACTION_GATT_CONNECTED = "com.mlzfg.bluetooth.le.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.mlzfg.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_GATT_SERVICES_DISCOVERED = "com.mlzfg.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String EXTRA_DATA = "com.mlzfg.bluetooth.le.EXTRA_DATA";
    public static final String HIDE_SCAN_BTN = "com.mlzfg.bluetooth.le.HIDE_SCAN_BTN";
    private static final String PERIPHERAL_DEVICE_SERVICE_NAME = "MyService";
    public static final String POWER_DATA = "com.mlzfg.bluetooth.le.POWER_DATA";
    public static final int SEARCH_DEVICE = 100;
    public static final String SHOW_SCAN_BTN = "com.mlzfg.bluetooth.le.SHOW_SCAN_BTN";
    long backgroundRunID;
    private Handler controllerHandler;
    private String historyAddress;
    private boolean isFirstSend;
    private BackgroundRunnable mBackgroundRunnable;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private boolean mConnected;
    private final BluetoothGattCallback mGattCallback;
    private Handler mHandler;
    private LeScanCallback mLeScanCallback;
    private boolean mScanning;
    private Handler serviceHandler;
    boolean supportBle;

    /* renamed from: com.mlizhi.modules.spec.detect.ble.BleService.1 */
    class C01441 extends Handler {
        C01441() {
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                BleService.this.mConnected = BleService.this.connectDevice(((BluetoothDevice)(msg.obj)).getAddress());
            } else if (msg.what == 2) {
                BleService.this.disGattconnect();
                BleService.this.closeGatt();
            }
        }
    }

    /* renamed from: com.mlizhi.modules.spec.detect.ble.BleService.2 */
    class C01452 implements LeScanCallback {
        C01452() {
        }

        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if (device != null) {
                try {
                    if (BleService.PERIPHERAL_DEVICE_SERVICE_NAME.equals(device.getName())) {
                        if (BleService.this.mScanning) {
                            BleService.this.mBluetoothAdapter.stopLeScan(BleService.this.mLeScanCallback);
                            BleService.this.mScanning = false;
                        }
                        Message msg = BleService.this.controllerHandler.obtainMessage();
                        msg.what = 0;
                        msg.obj = device;
                        BleService.this.controllerHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* renamed from: com.mlizhi.modules.spec.detect.ble.BleService.3 */
    class C01463 extends BluetoothGattCallback {
        C01463() {
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == 2) {
                BleService.this.broadcastRealMsg(BleService.ACTION_GATT_CONNECTED);
                BleService.this.mBluetoothAdapter.startDiscovery();
                BleService.this.mBluetoothGatt.discoverServices();
                BleService.this.setmConnected(true);
            } else if (newState == 0) {
                BleService.this.broadcastRealMsg(BleService.ACTION_GATT_DISCONNECTED);
                BleService.this.setmConnected(false);
                BleService.this.mBluetoothAdapter.cancelDiscovery();
                try {
                    if (BleService.this.serviceHandler != null) {
                        ((HandlerThread) BleService.this.serviceHandler.getLooper().getThread()).quit();
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == 0) {
                BleService.this.broadcastRealMsg(BleService.ACTION_GATT_SERVICES_DISCOVERED);
            }
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == 0) {
                BleService.this.broadcastDataMsg(BleService.ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            BleService.this.mBluetoothGatt.writeDescriptor(descriptor);
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            BleService.this.broadcastDataMsg(BleService.ACTION_DATA_AVAILABLE, characteristic);
        }

        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        }
    }

    /* renamed from: com.mlizhi.modules.spec.detect.ble.BleService.4 */
    class C01474 implements Runnable {
        C01474() {
        }

        public void run() {
            if (BleService.this.mScanning) {
                BleService.this.broadcastRealMsg(BleService.SHOW_SCAN_BTN);
                BleService.this.mScanning = false;
                BleService.this.getBluetoothAdapter().stopLeScan(BleService.this.mLeScanCallback);
                try {
                    if (BleService.this.serviceHandler != null) {
                        ((HandlerThread) BleService.this.serviceHandler.getLooper().getThread()).quit();
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        }
    }

    private class BackgroundRunnable implements Runnable {
        private BackgroundRunnable() {
        }

        public void run() {
            BleService.this.backgroundRunID = Thread.currentThread().getId();
            while (BleService.this.ismConnected()) {
                List<BluetoothGattService> btGattServiceList = BleService.this.getSupportedGattServices();
                if (btGattServiceList != null && btGattServiceList.size() > 0) {
                    BluetoothGattCharacteristic characteristic = getCharacteristic(btGattServiceList);
                    if (characteristic != null && (characteristic.getProperties() & 2) > 0) {
                        BleService.this.readCharacteristic(characteristic);
                    }
                }
            }
        }

        private BluetoothGattCharacteristic getCharacteristic(List<BluetoothGattService> gattServices) {
            if (gattServices == null) {
                return null;
            }
            for (BluetoothGattService gattService : gattServices) {
                if (gattService.getUuid().toString().substring(4, 8).endsWith("1600")) {
                    return gattService.getCharacteristic(UUID.fromString("00001601-0000-1000-8000-00805f9b34fb"));
                }
            }
            return null;
        }
    }

    public BleService() {
        this.mConnected = false;
        this.mScanning = false;
        this.mHandler = null;
        this.serviceHandler = null;
        this.isFirstSend = true;
        this.mBackgroundRunnable = null;
        this.supportBle = false;
        this.controllerHandler = new C01441();
        this.mLeScanCallback = new C01452();
        this.mGattCallback = new C01463();
        this.backgroundRunID = 0;
    }

    public void onCreate() {
        super.onCreate();
        this.mHandler = new Handler();
        if (VERSION.SDK_INT < 18) {
            this.supportBle = false;
            Toast.makeText(this, R.string.ble_os_version_low, Toast.LENGTH_LONG).show();
        } else if (getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            this.mBluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(BLUETOOTH_SERVICE);
            this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
            if (this.mBluetoothAdapter == null) {
                Toast.makeText(this, R.string.ble_device_not_support, Toast.LENGTH_LONG).show();
                this.supportBle = false;
                return;
            }
            this.supportBle = true;
        } else {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
            this.supportBle = false;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        setmConnected(false);
        try {
            if (this.serviceHandler != null) {
                ((HandlerThread) this.serviceHandler.getLooper().getThread()).quit();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (this.supportBle) {
            HandlerThread thread = new HandlerThread("serviceHandlerThread4Ble");
            thread.start();
            this.serviceHandler = new Handler(thread.getLooper());
            scanLeDevice(true);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public BluetoothAdapter getBluetoothAdapter() {
        if (this.mBluetoothAdapter == null) {
            this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        }
        return this.mBluetoothAdapter;
    }

    public void scanLeDevice(boolean enable) {
        broadcastRealMsg(HIDE_SCAN_BTN);
        if (enable) {
            this.mHandler.postDelayed(new C01474(), 10000);
            this.mScanning = true;
            this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
            return;
        }
        this.mScanning = false;
        this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
    }

    public synchronized boolean connectDevice(String address) {
        boolean z = true;
        boolean z2 = false;
        synchronized (this) {
            if (address != null) {
                if (this.historyAddress == null || !address.equals(this.historyAddress) || this.mBluetoothGatt == null) {
                    BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(address);
                    if (device != null) {
                        this.mBluetoothGatt = device.connectGatt(getApplicationContext(), false, this.mGattCallback);
                        this.historyAddress = address;
                        if (this.mBluetoothGatt != null && this.mBluetoothGatt.connect()) {
                            z2 = true;
                        }
                    }
                } else {
                    if (!this.mBluetoothGatt.connect()) {
                        z = false;
                    }
                    z2 = z;
                }
            }
        }
        return z2;
    }

    public synchronized void disGattconnect() {
        if (!(this.mBluetoothAdapter == null || this.mBluetoothGatt == null)) {
            this.mBluetoothGatt.disconnect();
            this.mConnected = false;
        }
    }

    public synchronized void closeGatt() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            this.mBluetoothGatt.readCharacteristic(characteristic);
        }
    }

    public void wirteCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (this.mBluetoothAdapter != null && this.mBluetoothGatt != null) {
            this.mBluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    private void broadcastRealMsg(String action) {
        getApplication().sendBroadcast(new Intent(action));
    }

    private void broadcastDataMsg(String action, BluetoothGattCharacteristic characteristic) {
        Intent intent = new Intent(action);
        byte[] data = characteristic.getValue();
        if (data != null && data.length > 0) {
            StringBuilder stringBuilder = new StringBuilder(data.length);
            int length = data.length;
            for (int i = 0; i < length; i++) {
                stringBuilder.append(String.format("%02X ", new Object[]{Byte.valueOf(data[i])}));
            }
            String s = Constants.VIA_RESULT_SUCCESS;
            String s1 = Constants.VIA_RESULT_SUCCESS;
            int num = 0;
            int num1 = 0;
            if (stringBuilder.toString().length() >= 20) {
                int c = Integer.parseInt(stringBuilder.toString().substring(0, 2).trim(), 16) ^ Integer.parseInt(stringBuilder.toString().substring(3, 5).trim(), 16);
                int p = Integer.parseInt(stringBuilder.toString().substring(6, 8).trim(), 16);
                int q = p & 7;
                p = ((p << q) % 256) | (p >> (8 - q));
                if (p < 0) {
                    p += 256;
                }
                if (c == 202) {
                    c = p ^ Integer.parseInt(stringBuilder.toString().substring(9, 11).trim(), 16);
                    if (c == 85) {
                        num1 = 2;
                    } else if (c == 225) {
                        num1 = 3;
                    }
                } else if (c == 197) {
                    int dd1 = Integer.parseInt(stringBuilder.toString().substring(9, 11).trim(), 16);
                    int dd2 = Integer.parseInt(stringBuilder.toString().substring(12, 14).trim(), 16);
                    int dd3 = Integer.parseInt(stringBuilder.toString().substring(15, 17).trim(), 16);
                    int d3 = dd2 ^ dd3;
                    int d4 = dd3 ^ Integer.parseInt(stringBuilder.toString().substring(18, 20).trim(), 16);
                    if (this.isFirstSend) {
                        num = (p ^ dd1) + ((dd1 ^ dd2) * 256);
                    }
                    num1 = (int) (((double) ((d3 + (d4 * 256)) + 295)) * 53.125d);
                }
            } else {
                if (this.isFirstSend && stringBuilder.toString().length() >= 11) {
                    num = Integer.parseInt(new StringBuilder(String.valueOf(stringBuilder.toString().substring(9, 11))).append(stringBuilder.toString().substring(6, 8)).toString().trim(), 16);
                }
                if (stringBuilder.toString().length() >= 11) {
                    s1 = new StringBuilder(String.valueOf(stringBuilder.toString().substring(3, 5))).append(stringBuilder.toString().substring(0, 2)).toString();
                }
                num1 = Integer.parseInt(s1.trim(), 16);
            }
            if (!this.isFirstSend || num <= 0 || num1 == 0) {
                intent.putExtra(EXTRA_DATA, num1);
            } else {
                this.isFirstSend = false;
                intent.putExtra(POWER_DATA, num);
                intent.putExtra(EXTRA_DATA, num1);
            }
        }
        getApplicationContext().sendBroadcast(intent);
    }

    public List<BluetoothGattService> getSupportedGattServices() {
        if (this.mBluetoothGatt == null) {
            return null;
        }
        List<BluetoothGattService> bgServiceList = null;
        try {
            return this.mBluetoothGatt.getServices();
        } catch (Exception e) {
            return bgServiceList;
        }
    }

    public boolean ismConnected() {
        return this.mConnected;
    }

    public void setmConnected(boolean mConnected) {
        this.mConnected = mConnected;
        if (!mConnected) {
            Message msg = this.controllerHandler.obtainMessage();
            msg.what = 2;
            this.controllerHandler.sendMessage(msg);
            try {
                if (this.mBackgroundRunnable != null) {
                    this.serviceHandler.removeCallbacks(this.mBackgroundRunnable);
                    this.mBackgroundRunnable = null;
                    finalize();
                    System.gc();
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else if (mConnected) {
            this.mBackgroundRunnable = new BackgroundRunnable();
            this.serviceHandler.post(this.mBackgroundRunnable);
        }
    }
}
