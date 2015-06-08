package com.epishie.btradar.view.fragment;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.epishie.btradar.R;
import com.epishie.btradar.presenter.MainPresenter;

public class MainFragment extends Fragment {

    private Adapter mAdapter;
    private MainPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new Adapter(getChildFragmentManager(), getActivity());
        mPresenter = new MainPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager viewPager = (ViewPager)view.findViewById(R.id.main_pager);
        TabLayout tabLayout = (TabLayout)view.findViewById(R.id.main_tab);

        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public MainPresenter getPresenter() {
        return mPresenter;
    }

    private static class Adapter extends FragmentPagerAdapter {

        private static final int COUNT = 2;
        private static final String[] FRAGMENTS = new String[]{ DataFragment.class.getName(), RadarFragment.class.getName() };

        private final Context mContext;
        private final String[] mTitles;

        public Adapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            mTitles = mContext.getResources().getStringArray(R.array.titles);
        }

        @Override
        public Fragment getItem(int position) {
            return Fragment.instantiate(mContext, FRAGMENTS[position]);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public int getCount() {
            return COUNT;
        }
    }
}
