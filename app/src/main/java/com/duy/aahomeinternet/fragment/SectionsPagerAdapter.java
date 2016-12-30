package com.duy.aahomeinternet.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.duy.aahomeinternet.R;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private final int NUM_PAGE = 4;
    private Context context;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new DeviceFragment();
            case 2:
                return new FragmentMode();
            case 3:
                return new FragmentChart(context);
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.tong_quan);
            case 1:
                return context.getString(R.string.device);
            case 2:
                return context.getString(R.string.mode);
            case 3:
                return context.getString(R.string.thong_ke);
            default:
                return null;
        }
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        // setUpTabLayout(tabLayout);
    }
}
