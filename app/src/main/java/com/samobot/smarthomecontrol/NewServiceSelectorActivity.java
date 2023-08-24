package com.samobot.smarthomecontrol;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.Set;

public class NewServiceSelectorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_service_selector);

        getSupportActionBar().setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.main_gradient_horiz, null));

        Button hueLinkButton = findViewById(R.id.hueLinkButton);
        Button lifxLinkButton = findViewById(R.id.lifxLinkButton);

        hueLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Button Clicked");
                linkHue();
            }
        });

        lifxLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linkLifx();
            }
        });

        boolean isHueEnabled = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getBoolean(getString(R.string.ishueenabled_key), false);
        if (isHueEnabled) {
            hueLinkButton.setText(getText(R.string.connectedServiceButtonText));
            hueLinkButton.setEnabled(false);
        }

        boolean isLifxEnabled = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getBoolean(getString(R.string.islifxenabled_key), false);
        if(isLifxEnabled) {
            lifxLinkButton.setText(getString(R.string.connectedServiceButtonText));
            lifxLinkButton.setEnabled(false);
        }
    }

    private void linkHue() {
        Intent intent = new Intent(this, HueLinkActivity.class);
        startActivity(intent);
    }

    private void linkLifx() {
        Intent intent = new Intent(this, LifxLinkActivity.class);
        startActivity(intent);
    }

}
