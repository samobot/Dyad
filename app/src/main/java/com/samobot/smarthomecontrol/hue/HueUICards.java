package com.samobot.smarthomecontrol.hue;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
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

import com.samobot.smarthomecontrol.R;

public class HueUICards {

    public static class HueMonochromeCard extends HueCardTop {

        public HueMonochromeCard(final HueManager hueManager, final Activity activity, Context context, final String lightID, String lightName) {
            super(hueManager, activity, context, lightID, lightName);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final ConstraintLayout layoutExpanded = (ConstraintLayout) inflater.inflate(R.layout.light_page_light_monochrome_expanded, this, false);
            final SeekBar lightPercentSeekBar = (SeekBar) layoutExpanded.getViewById(R.id.brightnessSeekBar);
            layoutExpanded.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
            hueManager.getLightBrightness(Integer.parseInt(lightID), new HueManager.RunWhenDoneBrightness() {
                @Override
                public void run(final int brightness) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lightPercentSeekBar.setProgress(brightness);
                        }
                    });
                }
            });
            lightPercentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    hueManager.setLightBrightness(Integer.parseInt(lightID), seekBar.getProgress());
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

    public static class HueTemperatureCard extends HueCardTop {

        public HueTemperatureCard(final HueManager hueManager, final Activity activity, @NonNull Context context, final String lightID, String lightName) {
            super(hueManager, activity, context, lightID, lightName);
            LayoutInflater inflater = LayoutInflater.from(getContext());
            final ConstraintLayout layoutExpanded = (ConstraintLayout) inflater.inflate(R.layout.light_page_light_temperature_expanded, this, false);
            final SeekBar lightPercentSeekBar = (SeekBar) layoutExpanded.getViewById(R.id.brightnessSeekBar);
            final SeekBar temperaturePercentSeekBar = (SeekBar) layoutExpanded.getViewById(R.id.temperatureSeekBar);
            layoutExpanded.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
            hueManager.getLightBrightness(Integer.parseInt(lightID), new HueManager.RunWhenDoneBrightness() {
                @Override
                public void run(final int brightness) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lightPercentSeekBar.setProgress(brightness);
                        }
                    });
                }
            });
            hueManager.getLightTemperature(Integer.parseInt(lightID), new HueManager.RunWhenDoneTemperature() {
                @Override
                public void run(final int temperature) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            temperaturePercentSeekBar.setProgress(temperature);
                        }
                    });
                }
            });
            lightPercentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    hueManager.setLightBrightness(Integer.parseInt(lightID), seekBar.getProgress());
                }
            });
            temperaturePercentSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    hueManager.setLightTemperature(Integer.parseInt(lightID), seekBar.getProgress());
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

    private static class HueCardTop extends CardView {

        public LinearLayout cardLayout;
        public ToggleButton lightMenuButton;

        public HueCardTop(final HueManager hueManager, final Activity activity, @NonNull Context context, final String lightID, String lightName) {
            super(context);
            setBackgroundTintList(ContextCompat.getColorStateList(activity, R.color.cardBackgroundColor));
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
            hueManager.getLightEnabled(Integer.parseInt(lightID), new HueManager.RunWhenDoneState() {
                @Override
                public void run(final boolean state) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lightStateSwitchView.setChecked(state);
                        }
                    });
                }
            });
            lightStateSwitchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        hueManager.setLightState(Integer.parseInt(lightID), true);
                    } else {
                        hueManager.setLightState(Integer.parseInt(lightID), false);
                    }
                }
            });
            cardLayout.addView(layoutTop);
            addView(cardLayout);
        }
    }

}
