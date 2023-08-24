package com.samobot.smarthomecontrol.lifx;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.samobot.smarthomecontrol.R;
import com.samobot.smarthomecontrol.hue.HueManager;
import com.samobot.smarthomecontrol.hue.HueUICards;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

public class LifxUICards {

    public static class LifxColorCard extends LifxCardTop {

        public LifxColorCard(final LifxManager lifxManager, final Activity activity, Context context, final String lightIP, String lightName) throws UnknownHostException {
            super(lifxManager, activity, context, lightIP, lightName);
            final InetAddress lightAddr = InetAddress.getByName(lightIP);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final ConstraintLayout layoutExpanded = (ConstraintLayout) inflater.inflate(R.layout.light_page_light_color_expanded, this, false);
            final SeekBar colorSeekBar = (SeekBar) layoutExpanded.getViewById(R.id.colorSeekBar);
            final SeekBar saturationSeekBar = (SeekBar) layoutExpanded.getViewById(R.id.saturationSeekBar);
            final SeekBar brightnessSeekBar = (SeekBar) layoutExpanded.getViewById(R.id.brightnessSeekBar);
            final SeekBar temperatureSeekBar = (SeekBar) layoutExpanded.getViewById(R.id.temperatureSeekBar);
            layoutExpanded.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
            /*lifxManager.getLightBrightness(Integer.parseInt(lightID), new HueManager.RunWhenDoneBrightness() {
                @Override
                public void run(final int brightness) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lightPercentSeekBar.setProgress(brightness);
                        }
                    });
                }
            });*/
            colorSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    //int wrapAround = ((progress - 10) >= 0) ? progress - 10 : 255 - (progress - 10);
                    seekBar.setThumbTintList(ColorStateList.valueOf(Color.HSVToColor(255, new float[]{scaleColor(progress), 255, 255})));
                    seekBar.setProgressTintList(ColorStateList.valueOf(Color.HSVToColor(255, new float[]{scaleColor(progress), 255, 255})));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    lifxManager.setLightHSBK(lightAddr, scale255to65535(seekBar.getProgress()), scale255to65535(saturationSeekBar.getProgress()), scale255to65535(brightnessSeekBar.getProgress()), scaleKelvin(temperatureSeekBar.getProgress()));
                }
            });
            saturationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    lifxManager.setLightHSBK(lightAddr, scale255to65535(colorSeekBar.getProgress()), scale255to65535(seekBar.getProgress()), scale255to65535(brightnessSeekBar.getProgress()), scaleKelvin(temperatureSeekBar.getProgress()));
                }
            });
            brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    lifxManager.setLightHSBK(lightAddr, scale255to65535(colorSeekBar.getProgress()), scale255to65535(saturationSeekBar.getProgress()), scale255to65535(seekBar.getProgress()), scaleKelvin(temperatureSeekBar.getProgress()));
                }
            });
            temperatureSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    lifxManager.setLightHSBK(lightAddr, scale255to65535(colorSeekBar.getProgress()), scale255to65535(saturationSeekBar.getProgress()), scale255to65535(brightnessSeekBar.getProgress()), scaleKelvin(seekBar.getProgress()));
                }
            });
            layoutExpanded.setVisibility(View.GONE);
            lightMenuButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        layoutExpanded.setVisibility(View.VISIBLE);
                    } else {
                        layoutExpanded.setVisibility(View.GONE);
                    }
                }
            });
            cardLayout.addView(layoutExpanded);
        }
    }

    private static class LifxCardTop extends CardView {

        public LinearLayout cardLayout;
        public ToggleButton lightMenuButton;

        public LifxCardTop(final LifxManager lifxManager, final Activity activity, @NonNull Context context, final String lightIP, String lightName) throws UnknownHostException {
            super(context);
            setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.cardBackgroundColor));
            final InetAddress lightAddr = InetAddress.getByName(lightIP);
            System.out.println(lightAddr.getHostAddress());
            cardLayout = new LinearLayout(getContext());
            LayoutTransition layoutTransition = new LayoutTransition();
            layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING); layoutTransition.disableTransitionType(LayoutTransition.APPEARING);
            cardLayout.setLayoutTransition(layoutTransition);
            cardLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams layoutParamsCard = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParamsCard.setMargins(25, 10, 25, 10);
            setLayoutParams(layoutParamsCard);
            setRadius(20); setUseCompatPadding(true);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            ConstraintLayout layoutTop = (ConstraintLayout) inflater.inflate(R.layout.light_page_light_entry, this, false);
            TextView lightNameView = (TextView) layoutTop.getViewById(R.id.lightNameView);
            final SwitchCompat lightStateSwitchView = (SwitchCompat) layoutTop.getViewById(R.id.lightStateSwitchView);
            lightMenuButton = (ToggleButton) layoutTop.getViewById(R.id.lightMenuButton);
            lightNameView.setTextSize(18);
            lightNameView.setText(lightName);
            /*lifxManager.getLightEnabled(Integer.parseInt(lightID), new HueManager.RunWhenDoneState() {
                @Override
                public void run(final boolean state) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lightStateSwitchView.setChecked(state);
                        }
                    });
                }
            });*/
            lightStateSwitchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        lifxManager.setLightState(lightAddr, true);
                    } else {
                        lifxManager.setLightState(lightAddr, false);
                    }
                }
            });
            cardLayout.addView(layoutTop);
            addView(cardLayout);
        }
    }

    private static int scale255to65535(int num) {
        return (int) ((num/255f)*65535f);
    }

    private static int scaleKelvin(int num) {
        return (int) ((num/255f)*6500f)+2500;
    }

    private static int scaleColor(int num) {
        return (int) ((num/255f)*360f);
    }

}
