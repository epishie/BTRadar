package com.epishie.btradar.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.epishie.btradar.Beacon;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class BeaconService extends Service {

    private final ServiceBinder mBinder = new ServiceBinder();
    private final HashMap<String, WeakReference<BeaconStateListener>> mListeners = new HashMap<>();

    private BeaconScanner mBeaconScanner;
    private int mMonitorcount = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        mBeaconScanner = new BeaconLollipopScanner(BluetoothAdapter.getDefaultAdapter(), mScannerListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ServiceBinder extends Binder {

        public void startMonitoring(String id, BeaconStateListener listener) {
            mListeners.put(id, new WeakReference<BeaconStateListener>(listener));
            if (mMonitorcount == 0) {
                mBeaconScanner.startScan();
            }
            mMonitorcount++;
        }

        public void stopMonitoring(String id) {
            mListeners.remove(id);
            mMonitorcount--;
            if (mMonitorcount == 0) {
                mBeaconScanner.stopScan();
            }
        }
    }

    private final BeaconScanner.BeaconScannerListener mScannerListener = new BeaconScanner.BeaconScannerListener() {
        @Override
        public void onScanUpdate(Beacon beacon) {
            for (WeakReference<BeaconStateListener> stateListener : mListeners.values()) {
                if (stateListener.get() != null) {
                    stateListener.get().onStatusUpdate(beacon);
                }
            }
        }
    };

    public interface BeaconStateListener {
        public void onStatusUpdate(Beacon beacon);
    }
}
