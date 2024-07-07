package com.symbol.kepzetclient;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPageAdapter extends FragmentStateAdapter {
    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new tabSearch();
            case 1:
                return new tabDbAccess();
            case 2:
                return new tabPosition();
            case 3:
                return new tabAge();
            default:
                return new tabSearch();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
