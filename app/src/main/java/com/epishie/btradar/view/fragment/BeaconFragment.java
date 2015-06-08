package com.epishie.btradar.view.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.epishie.btradar.presenter.MainPresenter;
import com.epishie.btradar.view.BeaconView;
import com.epishie.btradar.view.activity.MainActivity;

public abstract class BeaconFragment extends Fragment implements BeaconView {

    private MainPresenter mPresenter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity activity = (MainActivity)getActivity();
        mPresenter = activity.getPresenter();
        mPresenter.registerView(this);
    }

    @Override
    public void onDestroyView() {
        if (mPresenter != null) {
            mPresenter.unRegisterView(this);
        }
        super.onDestroyView();
    }
}
