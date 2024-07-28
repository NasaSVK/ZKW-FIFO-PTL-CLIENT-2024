package com.symbol.kepzetclient;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;


public class DbActivity extends AppCompatActivity {
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPageAdapter viewPageAdapter;

    //How to Implement TabLayout in your Android App
    //https://www.youtube.com/watch?v=LXl7D57fgOQ
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);
        viewPageAdapter = new ViewPageAdapter(this);
        viewPager2.setAdapter(viewPageAdapter);

        //https://stackoverflow.com/questions/768969/passing-a-bundle-on-startactivity
        int OpenTabIndex = getIntent().getExtras() != null && getIntent().getExtras().containsKey("OpenTabIndex")?getIntent().getExtras().getInt("OpenTabIndex"):0;

        //otvorenie prislusnej karty tabLayoutu //nutne OBA prikazy :0(
        tabLayout.getTabAt(OpenTabIndex).select();
        viewPager2.setCurrentItem(OpenTabIndex);
        //============================================

//        if (savedInstanceState != null) {
//            int OpenTabIndex = savedInstanceState.containsKey("OpenTabIndex") ? (savedInstanceState.getInt("OpenTabIndex")) : 0;
//            tabLayout.getTabAt(OpenTabIndex).select();
//        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });
    }

    public void Close1(View view) {
        this.finish();
    }
}