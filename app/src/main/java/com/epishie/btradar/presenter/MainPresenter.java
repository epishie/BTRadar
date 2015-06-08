package com.epishie.btradar.presenter;

import com.epishie.btradar.Beacon;
import com.epishie.btradar.view.BeaconView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainPresenter {

    private final Set<BeaconView> mViewSet;
    private List<Beacon> mBeacons;

    public MainPresenter() {
        mViewSet = new HashSet<>();
    }

    public void registerView(BeaconView view) {
        mViewSet.add(view);
    }

    public void unRegisterView(BeaconView view) {
        mViewSet.remove(view);
    }

    private void updateViews() {
        for (BeaconView view : mViewSet) {
            view.update(mBeacons);
        }
    }

    public void test() {
        mBeacons = new ArrayList<>();
        mBeacons.add(new Beacon("TESTESTEST"));
        updateViews();
    }
}
