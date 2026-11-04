package com.aditya.stereo3d;

import android.os.Bundle;

import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(StereoGalleryPlugin.class);
        super.onCreate(savedInstanceState);
    }
}
