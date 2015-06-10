package com.epishie.btradar.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epishie.btradar.model.Beacon;
import com.epishie.btradar.R;
import com.epishie.btradar.presenter.MainPresenter;
import com.epishie.btradar.view.BeaconView;
import com.epishie.btradar.view.activity.MainActivity;

import java.util.List;

public class MainFragment extends Fragment implements BeaconView {

    private MainPresenter mPresenter;
    private Adapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new Adapter(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.data_recycler);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity activity = (MainActivity)getActivity();
        mPresenter = activity.getPresenter();
        mPresenter.registerView(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unRegisterView(this);
    }

    @Override
    public void update(List<Beacon> beacons) {
        mAdapter.setBeacons(beacons);
    }

    private static class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private final Context mContext;
        private List<Beacon> mBeacons;

        public Adapter(Context context) {
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_beacon, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Beacon beacon = mBeacons.get(i);
            viewHolder.mIdText.setText(beacon.getUuid());
            viewHolder.mMajorText.setText(String.valueOf(beacon.getMajor()));
            viewHolder.mMinorText.setText(String.valueOf(beacon.getMinor()));
            viewHolder.mRssiText.setText(String.valueOf(beacon.getRssi()));
            viewHolder.mTxPowerText.setText(String.valueOf(beacon.getTxPower()));
            if (beacon.getDistance() == Beacon.UNKNOWN_DISTANCE) {
                viewHolder.mDistanceText.setText("-");
            } else {
                viewHolder.mDistanceText.setText(String.format("%.4f", beacon.getDistance()) + "m");
            }
            viewHolder.mProximityText.setText(mContext.getResources().
                    getStringArray(R.array.proximities)[beacon.getProximity().ordinal()]);
        }

        @Override
        public int getItemCount() {
            if (mBeacons != null) {
                return mBeacons.size();
            }

            return 0;
        }

        public void setBeacons(List<Beacon> beacons) {
            mBeacons = beacons;
            notifyDataSetChanged();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mIdText;
        private final TextView mMajorText;
        private final TextView mMinorText;
        private final TextView mRssiText;
        private final TextView mTxPowerText;
        private final TextView mDistanceText;
        private final TextView mProximityText;

        public ViewHolder(View itemView) {
            super(itemView);
            mIdText = (TextView)itemView.findViewById(R.id.beacon_id);
            mMajorText = (TextView)itemView.findViewById(R.id.beacon_major);
            mMinorText = (TextView)itemView.findViewById(R.id.beacon_minor);
            mRssiText = (TextView)itemView.findViewById(R.id.beacon_rssi);
            mTxPowerText = (TextView)itemView.findViewById(R.id.beacon_tx_power);
            mDistanceText = (TextView)itemView.findViewById(R.id.beacon_distance);
            mProximityText = (TextView)itemView.findViewById(R.id.beacon_proximity);
        }
    }
}
