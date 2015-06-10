package com.epishie.btradar.presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.epishie.btradar.Beacon;
import com.epishie.btradar.bluetooth.BeaconService;
import com.epishie.btradar.view.BeaconView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainPresenter implements BeaconService.BeaconStateListener {

    private static final String ID = "BT-RADAR";

    private final Context mContext;
    private final Set<BeaconView> mViewSet;
    private BeaconService.ServiceBinder mServiceBinder;
    private boolean mServiceBound;
    private boolean mMonitoring;
    private final Handler mHandler;

    private final List<Beacon> mBeacons;
    private static final List<String> mUuids = new ArrayList<>(Arrays.asList(
            "B9407F30-F5F8-466E-AFF9-25556B57FE6D",
            "8492E75F-4FD6-469D-B132-043FE94921D8"
    ));

    public MainPresenter(Context context, List<Beacon> savedBeacons) {
        mContext = context;
        mViewSet = new HashSet<>();
        mHandler = new Handler(Looper.getMainLooper());

        if (savedBeacons != null) {
            mBeacons = savedBeacons;
        } else {
            mBeacons = new ArrayList<>();
            Beacon.Builder builder = new Beacon.Builder();
            for (String uuid : mUuids) {
                builder.setUuid(uuid);
                mBeacons.add(builder.create());
            }
        }
    }

    public List<Beacon> getBeacons() {
        return mBeacons;
    }

    public void onActivityStart() {
        Intent intent = new Intent(mContext, BeaconService.class);
        mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        synchronized (this) {
            mMonitoring = true;
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!mMonitoring) {
                        return;
                    }
                    for (int i = 0; i < mBeacons.size(); i++) {
                        Beacon beacon = mBeacons.get(i);
                        if (beacon.getLastUpdateTime() == 0) {
                            continue;
                        }
                        long elapsedTime = System.currentTimeMillis() - beacon.getLastUpdateTime();
                        if (elapsedTime > 10000L) {
                            mBeacons.set(i, new Beacon.Builder(beacon).create());
                        }
                    }
                    mHandler.postDelayed(this, 1000);
                }
            }, 1000);
        }
    }

    public void onActivityStop() {
        if (mServiceBound) {
            mServiceBinder.stopMonitoring(ID);
            mContext.unbindService(mServiceConnection);
            mServiceBound = false;
        }
        synchronized (this) {
            mMonitoring = false;
        }
    }

    public void registerView(BeaconView view) {
        mViewSet.add(view);
        view.update(mBeacons);
    }

    public void unRegisterView(BeaconView view) {
        mViewSet.remove(view);
    }

    private void updateViews() {
        for (BeaconView view : mViewSet) {
            view.update(mBeacons);
        }
    }

    @Override
    public void onStatusUpdate(Beacon beacon) {
        synchronized (this) {
            int i = 0;
            for (Beacon b : mBeacons) {
                if (b.equals(beacon)) {
                    mBeacons.set(i, beacon);
                    updateViews();
                }
                i++;
            }
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceBinder = (BeaconService.ServiceBinder)service;
            mServiceBinder.startMonitoring(ID, MainPresenter.this);
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
}
