package com.epishie.btradar.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.epishie.btradar.presenter.MainPresenter;
import com.epishie.btradar.view.BeaconView;

public abstract class BeaconFragment extends Fragment implements BeaconView {

    private MainPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainFragment parent = (MainFragment)getParentFragment();
        mPresenter = parent.getPresenter();
        mPresenter.registerView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unRegisterView(this);
    }
}
