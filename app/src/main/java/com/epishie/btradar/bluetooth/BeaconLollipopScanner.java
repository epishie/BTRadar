package com.epishie.btradar.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.epishie.btradar.model.Beacon;

import java.util.List;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BeaconLollipopScanner extends BeaconScanner {

    private BluetoothLeScanner mScanner;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public BeaconLollipopScanner(BluetoothAdapter adapter, BeaconScannerListener listener) {
        super(adapter, listener);
    }

    @Override
    public void startScan() {
        mScanning = true;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAdapter.isEnabled()) {
                    getScanner().stopScan(mScanCallback);
                }
                if (mScanning) {
                    startScan();
                }
            }
        }, SCAN_TIME);
        if (mAdapter.isEnabled()) {
            getScanner().startScan(mScanCallback);
        }
    }

    @Override
    public void stopScan() {
        mScanning = false;
        getScanner().stopScan(mScanCallback);
    }

    private BluetoothLeScanner getScanner() {
        if (mScanner == null) {
            mScanner = mAdapter.getBluetoothLeScanner();
        }
        return mScanner;
    }

    private final ScanCallback mScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            processResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results) {
                processResult(result);
            }
        }

        private void processResult(ScanResult result) {
            Log.d("SCAN", "FOUND: " + result.getDevice().getAddress());
            ScanRecord record = result.getScanRecord();
            if (record != null) {
                Beacon beacon = getBeacon(record.getBytes(), result.getRssi());
                if (beacon != null) {
                    mListener.onScanUpdate(beacon);
                }
            }
        }
    };
}
