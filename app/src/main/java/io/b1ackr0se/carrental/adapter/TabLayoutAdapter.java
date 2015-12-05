package io.b1ackr0se.carrental.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import io.b1ackr0se.carrental.fragment.LoginFragment;
import io.b1ackr0se.carrental.fragment.RegisterFragment;

public class TabLayoutAdapter extends FragmentStatePagerAdapter {

    final int PAGE_COUNT = 2;
    private String titles[];
    private Context context;

    public TabLayoutAdapter(FragmentManager fm, Context c, String[] titles) {
        super(fm);
        this.context = c;
        this.titles = titles;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0) {
            return new LoginFragment();
        } else {
            return new RegisterFragment();
        }
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
