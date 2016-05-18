package com.github.openthos.printer.localprint.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.util.LogUtils;


public abstract class BaseActivity extends AppCompatActivity {

    private String TAG = "BaseActivity";
    BroadcastReceiver baseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int task = intent.getIntExtra(APP.TASK, APP.TASK_DEFAULT);

            switch (task){
                case APP.TASK_INIT_FINISH:
                    break;
                case APP.TASK_INIT_FAIL:
                    Toast.makeText(BaseActivity.this, R.string.initialization_failure, Toast.LENGTH_SHORT).show();
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

        if(bindTAG() != null){
            TAG = bindTAG();
        }

        initialize();
        if_first_run();

    }

    private void initialize() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(APP.BROADCAST_ALL_ACTIVITY);

        registerReceiver(baseReceiver, filter);


        LogUtils.d(TAG, "initialize()");
    }

    private void if_first_run() {
        if(APP.IS_FIRST_RUN){
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
        unregisterReceiver(baseReceiver);
    }

    /**
     * 必须设置一个TAG
     * @return TAG
     */
    protected abstract String bindTAG();
}
