package com.samobot.smarthomecontrol;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.samobot.smarthomecontrol.hue.HueManager;

import java.util.HashSet;
import java.util.Set;

public class HueLinkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_hue);

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.main_gradient_horiz, null));

        TextView bridgeResultText = findViewById(R.id.textViewBridgeResult);
        final TextView apiResultText = findViewById(R.id.textViewAPIResult);
        final TextView discoveryResultText = findViewById(R.id.textViewDiscoveryResult);
        Button genAPIKeyButton = findViewById(R.id.buttonGenAPIKey);
        final String preferenceKey = getString(R.string.preference_file_key);

        genAPIKeyButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {
                HueManager.ApiKeyGenThread apiKeyGenThread = new HueManager.ApiKeyGenThread(getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).getString(getString(R.string.device_ip_key), null));
                apiKeyGenThread.start();
                try {
                    apiKeyGenThread.join();
                    String apiKey = apiKeyGenThread.apiKey;
                    if(apiKey != null) {
                        getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).edit().putString(getString(R.string.apikey_key), apiKey).commit();
                        apiResultText.setText("Success!");
                        getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).edit().putBoolean(getString(R.string.ishueenabled_key), true).commit();
                        discoveryResultText.setText("Discovering...");
                        HueManager.LightDiscoveryThread lightDiscoveryThread = new HueManager.LightDiscoveryThread(getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).getString(getString(R.string.device_ip_key), null), apiKey);
                        lightDiscoveryThread.start();
                        lightDiscoveryThread.join();
                        if(!lightDiscoveryThread.lightNames.isEmpty()) {
                            discoveryResultText.setText("Found " + lightDiscoveryThread.lightNames.size() + " Lights");
                            Set<String> keys = lightDiscoveryThread.lightNames.keySet();
                            Set<String> deviceIDs = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).getStringSet(getString(R.string.deviceIDs_set_key), new HashSet<String>());
                            for(String key : keys) {
                                int length = deviceIDs.size();
                                String deviceID = null;
                                while(deviceIDs.size() == length) {
                                    deviceID = Double.toHexString(Math.random());
                                    deviceIDs.add(deviceID);
                                }
                                getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).edit().putString(deviceID + "_brand", "hue")
                                        .putString(deviceID + "_lightID", key)
                                        .putString(deviceID + "_lightName", lightDiscoveryThread.lightNames.get(key))
                                        .putString(deviceID + "_lightType", lightDiscoveryThread.lightTypes.get(key)).commit();
                            }
                            getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).edit().putStringSet(getString(R.string.deviceIDs_set_key), deviceIDs).commit();
                            Intent i = new Intent(HueLinkActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        } else {
                            discoveryResultText.setText("Failed");
                        }
                    } else {
                        apiResultText.setText("Failed");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    apiResultText.setText("Failed");
                }

            }
        });

        HueManager.BridgeDiscoveryThread bridgeDiscoveryThread = new HueManager.BridgeDiscoveryThread();
        bridgeDiscoveryThread.start();
        try {
            bridgeDiscoveryThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final String device_ip = bridgeDiscoveryThread.device_ip;

        if(device_ip != null) {
            String apiKey = null;
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).edit().putString(getString(R.string.device_ip_key), device_ip).apply();
            System.out.println(device_ip);
            bridgeResultText.setText("Success!");
            bridgeResultText.setTextColor(Color.parseColor("#00FF00"));
            apiResultText.setTextColor(Color.parseColor("#FFFF00"));
            apiResultText.setText("Press the button on the hue bridge device, then press the button below");
            genAPIKeyButton.setEnabled(true);
        } else {
            System.out.println((String) null);
            bridgeResultText.setText("Failed");
            bridgeResultText.setTextColor(Color.parseColor("#FF0000"));
        }
    }
}
