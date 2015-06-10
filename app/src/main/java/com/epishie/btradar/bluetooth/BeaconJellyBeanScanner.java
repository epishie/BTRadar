package com.epishie.btradar.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.epishie.btradar.model.Beacon;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BeaconJellyBeanScanner extends BeaconScanner {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public BeaconJellyBeanScanner(BluetoothAdapter adapter, BeaconScannerListener listener) {
        super(adapter, listener);
    }

    @Override
    public void startScan() {
        mScanning = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAdapter.isEnabled()) {
                    mAdapter.stopLeScan(mLeScanCallback);
                    mAdapter.startLeScan(mLeScanCallback);
                }
            }
        }, SCAN_TIME);
        if (mAdapter.isEnabled()) {
            mAdapter.startLeScan(mLeScanCallback);
        }
    }

    @Override
    public void stopScan() {
        mScanning = false;
        mAdapter.stopLeScan(mLeScanCallback);
    }

    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d("SCAN", "FOUND: " + device.getAddress());
            Beacon beacon = getBeacon(scanRecord, rssi);
            if (beacon != null) {
                mListener.onScanUpdate(beacon);
            }
        }
    };
}
