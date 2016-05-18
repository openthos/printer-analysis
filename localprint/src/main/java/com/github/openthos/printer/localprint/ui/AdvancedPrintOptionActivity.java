package com.github.openthos.printer.localprint.ui;

import android.os.Bundle;
import com.github.openthos.printer.localprint.R;

public class AdvancedPrintOptionActivity extends BaseActivity {

    private static final String TAG = "AdvancedPrintOptionActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 2016/5/10 AdvancedPrintOptionActivity 打印机高级设置

        setContentView(R.layout.activity_advanced_print_option);
    }

    @Override
    protected String bindTAG() {
        return TAG;
    }
}
