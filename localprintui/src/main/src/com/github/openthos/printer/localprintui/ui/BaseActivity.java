package com.github.openthos.printer.localprintui.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.github.openthos.printer.localprintui.APP;
import com.github.openthos.printer.localprintui.R;
import com.github.openthos.printer.localprintui.util.LogUtils;


/**
 * Base activity
 */
public abstract class BaseActivity extends ActionBarActivity {

    private String TAG = "BaseActivity";
    private BroadcastReceiver mBaseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int task = intent.getIntExtra(APP.TASK, APP.TASK_DEFAULT);
            LogUtils.d(TAG, "intent -> task=" + task);
            switch (task) {
                case APP.TASK_INIT_FINISH:
                    break;
                case APP.TASK_INIT_FAIL:
                    Toast.makeText(BaseActivity.this
                            , R.string.initialization_failure
                            , Toast.LENGTH_SHORT)
                            .show();
                    BaseActivity.this.finish();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (bindTAG() != null) {
            TAG = bindTAG();
        }

        initialize();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirstRun();
            }
        }, APP.FIRTST_CONNECT_SERVICE_DELAY_TIME);

    }

    private void initialize() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(APP.BROADCAST_ALL_ACTIVITY);

        registerReceiver(mBaseReceiver, filter);

        LogUtils.d(TAG, "initialize()");
    }

    private void isFirstRun() {

        if(APP.getIRemoteService() == null){
            LogUtils.d(TAG, "isFirstRun connect_service_error");
            Toast.makeText(this, R.string.connect_service_error, Toast.LENGTH_SHORT).show();
        }

        if (APP.IS_INITIALIZING) {
            Toast.makeText(this, getString(R.string.initializing_print_service),
                           Toast.LENGTH_SHORT).show();
            finish();
        }

        if (APP.IS_FIRST_RUN) {
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy()");
        unregisterReceiver(mBaseReceiver);
    }

    /**
     * Must to set a TAG
     *
     * @return TAG
     */
    protected abstract String bindTAG();
}