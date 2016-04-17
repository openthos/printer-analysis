package com.github.openthos.printer.openthosprintservice.ui;

import android.os.Bundle;

import com.github.openthos.printer.openthosprintservice.R;

public class AdvancedPrintOptionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_print_option);
    }

    @Override
    protected String bindTAG() {
        return "AdvancedPrintOptionActivity";
    }
}
