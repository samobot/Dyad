package com.samobot.smarthomecontrol;

import android.content.res.AssetManager;
import android.graphics.Typeface;

public class CustomTypefaces {
    public Typeface openSans;
    public CustomTypefaces(AssetManager assets) {
        openSans = Typeface.createFromAsset(assets, "fonts/");
    }
}
