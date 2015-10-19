package com.thetubeteam.mytube;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    public static final String TAG = "MainActivity";

    private ViewPager mViewPager;

    private SearchFragment searchFragment;

    private PlaylistFragment playlistFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs;
        OAuth2Helper oAuth2Helper;

        SectionsPagerAdapter mSectionsPagerAdapter;

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        oAuth2Helper = new OAuth2Helper(prefs);

        try{
            PlaylistUpdates.init(oAuth2Helper.loadCredential());
        }catch(IOException e){
            e.printStackTrace();
        }

        getSupportActionBar().setCustomView(R.layout.searchbox);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        final EditText search = (EditText) getSupportActionBar().getCustomView();

        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                Log.d(TAG, search.getText().toString());
                mViewPager.setCurrentItem(0, true);
                searchFragment.search(search.getText().toString());
                return true;
            }
        });

        searchFragment = SearchFragment.newInstance();
        playlistFragment = PlaylistFragment.newInstance();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return searchFragment;
                case 1:
                    return playlistFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Search";
                case 1:
                    return "Favorites";
            }
            return null;
        }
    }

}
