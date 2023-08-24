package com.samobot.smarthomecontrol;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.samobot.smarthomecontrol.hue.HueManager;
import com.samobot.smarthomecontrol.lifx.LifxManager;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public HueManager hueManager;

    public LightsPageFragment lightsPageFragment = null;
    public List<Fragment> fragmentList = new ArrayList<>();

    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.main_gradient_horiz, null));

        System.out.println(this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getBoolean(getString(R.string.ishueenabled_key), false));

        boolean addLightPage = false;
        if(getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getBoolean(getString(R.string.ishueenabled_key), false)) {
            addLightPage = true;
        } else if(getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getBoolean(getString(R.string.islifxenabled_key), false)) {
           addLightPage = true;
        }

        lightsPageFragment = new LightsPageFragment();
        if(addLightPage) {
            fragmentList.add(lightsPageFragment);
        }

        fragmentList.add(new NewServiceFragment());


        viewPager = findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = findViewById(R.id.tab_layout);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

            }
        }).attach();

        final WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if((!manager.isWifiEnabled())) {
            AlertDialog dialog = new AlertDialog.Builder(this, R.style.DarkDialog).setMessage("Please Enable WIFI connection").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!manager.isWifiEnabled()) {
                        finish();
                        System.exit(0);
                    }
                }
            }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if(!manager.isWifiEnabled()) {
                        finish();
                        System.exit(0);
                    }
                }
            }).create();
            dialog.show();

        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        public ScreenSlidePagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getItemCount() {
            return fragmentList.size();
        }
    }

    public LightsPageFragment getLightsFrag() {
        return lightsPageFragment;
    }

    public void addLightPage() {
        fragmentList.add(0, lightsPageFragment);
    }

}