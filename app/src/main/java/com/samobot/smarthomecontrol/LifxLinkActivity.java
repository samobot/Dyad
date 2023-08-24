package com.samobot.smarthomecontrol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.samobot.smarthomecontrol.lifx.LifxManager;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LifxLinkActivity extends AppCompatActivity {

    LifxManager lifxManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_lifx);

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.main_gradient_horiz, null));

        final Set<String> deviceIDs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).getStringSet(getString(R.string.deviceIDs_set_key), new HashSet<String>());

        final TextView discoveredLights = findViewById(R.id.discoveredLightsTextView);
        final ProgressBar progressBar = findViewById(R.id.loadingProgessBar);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressBar.getProgress() < 160) {
                    if (Build.VERSION.SDK_INT > 24) {
                        progressBar.setProgress(progressBar.getProgress() + 1, true);
                    } else {
                        progressBar.setProgress(progressBar.getProgress() + 1);
                    }
                    handler.postDelayed(this, 250);
                } else {
                    getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit().putStringSet(getString(R.string.deviceIDs_set_key), deviceIDs).apply();
                    Intent i = new Intent(LifxLinkActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }
        }, 250);


        try {
            lifxManager = new LifxManager();
            lifxManager.discoverLights(this, new LifxManager.DiscoverLightRunnable() {
                @Override
                public void run(final Activity activity, List<DatagramPacket> recievedResponses) {
                    if (!recievedResponses.isEmpty()) {
                        getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit().putBoolean(getString(R.string.islifxenabled_key), true).commit();
                        final Set<InetAddress> foundIPs = new HashSet<>();
                        for (DatagramPacket packet : recievedResponses) {
                            foundIPs.add(packet.getAddress());
                        }
                        new Thread() {
                            @Override
                            public void run() {
                                for (final InetAddress ip : foundIPs) {
                                    System.out.println("Found: " + ip.getHostAddress());
                                    lifxManager.getLightName(activity, ip, true, new LifxManager.ReceivedStringRunnable() {
                                        @SuppressLint("ApplySharedPref")
                                        @Override
                                        public void run(String returnString) {
                                            int length = deviceIDs.size();
                                            String deviceID = null;
                                            while (deviceIDs.size() == length) {
                                                deviceID = Double.toHexString(Math.random());
                                                deviceIDs.add(deviceID);
                                            }
                                            getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).edit().putString(deviceID + "_brand", "lifx")
                                                    .putString(deviceID + "_lightIP", ip.getHostAddress())
                                                    .putString(deviceID + "_lightName", returnString).commit();

                                            System.out.println("Light: " + ip.getHostAddress() + " :Name: " + returnString);
                                        }
                                    });
                                }
                                final int foundLightsCount = foundIPs.size();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        discoveredLights.setText(getString(R.string.lightsFoundText, foundLightsCount));
                                    }
                                });
                            }
                        }.start();
                    } else {
                        System.out.println("No Lights Found");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                discoveredLights.setText(getString(R.string.noLightsFoundText));
                            }
                        });
                    }
                }
            });
        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(lifxManager != null) {
            lifxManager.close();
        }
    }

}
