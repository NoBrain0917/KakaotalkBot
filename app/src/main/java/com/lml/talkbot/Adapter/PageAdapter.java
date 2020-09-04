package com.lml.talkbot.Adapter;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PageAdapter extends FragmentStatePagerAdapter {
    ArrayList<Fragment> items = new ArrayList<Fragment>();
    public PageAdapter(FragmentManager fm) {
        super(fm);
    }
    public void addItem(Fragment item) {
        items.add(item);
    }
    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }
    @Override
    public int getCount(){
        return items.size();
    }
    @NonNull
    @Override
    public CharSequence getPageTitle(int position) {
        return String.valueOf(position);
    }
}
