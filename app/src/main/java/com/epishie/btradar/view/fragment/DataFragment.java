package com.epishie.btradar.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epishie.btradar.Beacon;
import com.epishie.btradar.R;

import java.util.List;

public class DataFragment extends BeaconFragment {

    private Adapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new Adapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.data_recycler);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(lm);
        recyclerView.setAdapter(mAdapter);

        MainFragment parent = (MainFragment)getParentFragment();
        parent.getPresenter().test();
    }

    @Override
    public void update(List<Beacon> beacons) {
        mAdapter.setBeacons(beacons);
    }

    private static class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Beacon> mBeacons;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_beacon, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            Beacon beacon = mBeacons.get(i);
            viewHolder.mIdText.setText(beacon.getId());
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

        public ViewHolder(View itemView) {
            super(itemView);
            mIdText = (TextView)itemView.findViewById(R.id.beacon_id);
        }
    }
}
