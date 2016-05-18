package com.github.openthos.printer.localprint.ui;

import android.os.Bundle;

import com.github.openthos.printer.localprint.R;

public class SettingsActivity extends BaseActivity {

    private static final String TAG = "SettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 2016/5/10 SettingsActivity 打印插件设置

        setContentView(R.layout.activity_settings);
    }

    @Override
    protected String bindTAG() {
        return TAG;
    }
}
