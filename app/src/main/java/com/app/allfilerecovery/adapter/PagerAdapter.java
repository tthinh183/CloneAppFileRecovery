package com.app.allfilerecovery.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.app.allfilerecovery.view.history.HistoryFragment;
import com.app.allfilerecovery.view.home.HomeFragment;
import com.app.allfilerecovery.view.recycle_bin.RecycleBinFragment;

public class PagerAdapter extends FragmentStateAdapter {
    public PagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new HomeFragment();
            case 1:
                return new RecycleBinFragment();
            case 2:
                return new HistoryFragment();
        }
        notifyDataSetChanged();
        return new HomeFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
