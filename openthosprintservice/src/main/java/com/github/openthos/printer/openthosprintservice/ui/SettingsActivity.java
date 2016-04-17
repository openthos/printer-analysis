package com.github.openthos.printer.openthosprintservice.ui;

import android.os.Bundle;

import com.github.openthos.printer.openthosprintservice.R;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected String bindTAG() {
        return "SettingsActivity";
    }
}
