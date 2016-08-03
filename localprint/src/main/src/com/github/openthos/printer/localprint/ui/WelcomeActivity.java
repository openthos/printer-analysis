package com.github.openthos.printer.localprint.ui;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.task.InitTask;

/**
 * Welcome page is responsible for initializing.
 */
public class WelcomeActivity extends Activity {

    private static final String TAG = "WelcomeActivity";
    private TextView mTextView;
    private Button mButtonCancel;
    private Button mButtonOk;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        initUI();

        if (APP.IS_FIRST_RUN) {

        } else {
            Toast.makeText(WelcomeActivity.this, R.string.no_need_for_initialization, Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    private void initUI() {
        mTextView = (TextView) findViewById(R.id.textView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mButtonCancel = (Button) findViewById(R.id.button_cancel);
        mButtonOk = (Button) findViewById(R.id.button_ok);
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextView.setText(R.string.initializing_print_service);
                mProgressBar.setVisibility(ProgressBar.VISIBLE);
                new InitTask<Void>() {
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        start_init(aBoolean, ERROR);
                    }
                }.start();
                mButtonOk.setClickable(false);
            }
        });
    }

    private void exit() {
        APP.initFailed(this);
        finish();
    }

    private void start_init(boolean flag, String ERROR) {
        if (flag) {

            APP.initSucceed(this);

            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            mTextView.setText(R.string.initialization_suceess);
            Toast.makeText(WelcomeActivity.this
                    , R.string.initialization_suceess, Toast.LENGTH_SHORT).show();
            finish();

        } else {
            mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            mTextView.setText(getString(R.string.initialization_failure) + "\n" + ERROR);
            mButtonOk.setClickable(true);
        }

    }


}
