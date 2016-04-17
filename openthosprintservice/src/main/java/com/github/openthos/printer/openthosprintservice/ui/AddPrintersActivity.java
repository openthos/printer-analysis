package com.github.openthos.printer.openthosprintservice.ui;

import android.os.Bundle;

import com.github.openthos.printer.openthosprintservice.R;

public class AddPrintersActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_printers);
    }

    @Override
    protected String bindTAG() {
        return "AddPrintersActivity";
    }
}
