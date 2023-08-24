package com.samobot.smarthomecontrol;

import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.samobot.smarthomecontrol.hue.HueManager;
import com.samobot.smarthomecontrol.hue.HueUICards;
import com.samobot.smarthomecontrol.lifx.LifxManager;
import com.samobot.smarthomecontrol.lifx.LifxUICards;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Set;

public class LightsPageFragment extends Fragment {

    String preferenceKey = null;

    HueManager hueManager;
    LifxManager lifxManager;
    LinearLayout linearLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lights_page, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        preferenceKey = getString(R.string.preference_file_key);
        MainActivity activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(getString(R.string.lightsPageSupportBarText));
        linearLayout = view.findViewById(R.id.cardHolder);
        drawScreen();
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        activity.getSupportActionBar().setTitle(getString(R.string.lightsPageSupportBarText));
        if(linearLayout.getChildCount() == 0) {
            drawScreen();
        }
    }

    private void drawScreen() {
        Set<String> deviceIDS = getActivity().getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).getStringSet(getString(R.string.deviceIDs_set_key), null);
        if (getActivity().getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).getBoolean(getString(R.string.ishueenabled_key), false)) {
            hueManager = new HueManager(getActivity().getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).getString(getString(R.string.device_ip_key), null), getActivity().getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).getString(getString(R.string.apikey_key), null));
        }
        if(getActivity().getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).getBoolean(getString(R.string.islifxenabled_key), false)) {
            try {
                lifxManager = new LifxManager();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        if (deviceIDS != null && !deviceIDS.isEmpty()) {
            for (String deviceID : deviceIDS) {
                switch (getActivity().getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).getString(deviceID + "_brand", null)) {
                    case "hue": {
                        String lightName = getActivity().getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).getString(deviceID + "_lightName", null);
                        String lightID = getActivity().getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).getString(deviceID + "_lightID", null);
                        switch (getActivity().getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).getString(deviceID + "_lightType", null)) {
                            case "Dimmable light":
                                linearLayout.addView(new HueUICards.HueMonochromeCard(hueManager, getActivity(), getContext(), lightID, lightName));
                                break;
                            case "Color temperature light":
                                linearLayout.addView(new HueUICards.HueTemperatureCard(hueManager, getActivity(), getContext(), lightID, lightName));
                                break;
                        }
                        break;
                    }
                    case "lifx": {
                        String lightName = getActivity().getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).getString(deviceID + "_lightName", null);
                        String lightIP = getActivity().getSharedPreferences(preferenceKey, Context.MODE_PRIVATE).getString(deviceID + "_lightIP", null);
                        try {
                            linearLayout.addView(new LifxUICards.LifxColorCard(lifxManager, getActivity(), getContext(), lightIP, lightName));
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }

}
