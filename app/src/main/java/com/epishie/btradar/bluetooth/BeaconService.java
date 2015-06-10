package com.epishie.btradar.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.epishie.btradar.model.Beacon;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class BeaconService extends Service {

    private final ServiceBinder mBinder = new ServiceBinder();
    private final HashMap<String, WeakReference<BeaconStateListener>> mListeners = new HashMap<>();

    private BeaconScanner mBeaconScanner;
    private int mMonitorCount = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        mBeaconScanner = BeaconScanner.createScanner(adapter, mScannerListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ServiceBinder extends Binder {

        public void startMonitoring(String id, BeaconStateListener listener) {
            mListeners.put(id, new WeakReference<>(listener));
            if (mMonitorCount == 0) {
                mBeaconScanner.startScan();
            }
            mMonitorCount++;
        }

        public void stopMonitoring(String id) {
            mListeners.remove(id);
            mMonitorCount--;
            if (mMonitorCount == 0) {
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
       void onStatusUpdate(Beacon beacon);
    }
}
