package com.symbol.ptlclient2024;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPageAdapter extends FragmentStateAdapter {
    private Activity _FragmentActivity;

    public ViewPageAdapter(@NonNull FragmentActivity fragmentActivity) {

        super(fragmentActivity);
        this._FragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new tabSearch(this._FragmentActivity);
            case 1:
                return new tabDbAccess(this._FragmentActivity);
            case 2:
                return new tabPosition(this._FragmentActivity);
            case 3:
                return new tabAge(this._FragmentActivity);
            default:
                return new tabSearch(this._FragmentActivity);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
