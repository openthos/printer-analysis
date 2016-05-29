package com.github.openthos.printer.localprint.ui;


import android.app.Activity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.task.BaseTask;
import com.github.openthos.printer.localprint.task.InitTask;
import com.github.openthos.printer.localprint.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * 欢迎界面，实为检测是否初始化
 */
public class WelcomeActivity extends Activity {

    // TODO: 首次检测页面待改为Dialog显示，可写在BaseActivity中

    private static final String TAG = "WelcomeActivity";
    private TextView textView;
    private Button button_cancel;
    private Button button_ok;
    private ProgressBar progressbar;

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

        if(APP.IS_FIRST_RUN){


        }else{
            Toast.makeText(WelcomeActivity.this, R.string.no_need_for_initialization, Toast.LENGTH_SHORT).show();
            finish();
        }


    }

    private void initUI() {
        textView = (TextView)findViewById(R.id.textView);
        progressbar = (ProgressBar)findViewById(R.id.progressBar);
        button_cancel = (Button)findViewById(R.id.button_cancel);
        button_ok = (Button)findViewById(R.id.button_ok);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
            }
        });

        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(R.string.initializing_print_service);
                progressbar.setVisibility(ProgressBar.VISIBLE);
                new InitTask<Void>(){
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        start_init(aBoolean, ERROR);
                    }
                }.start();
                button_ok.setClickable(false);
            }
        });
    }

    private void exit() {
        Intent intent = new Intent(APP.BROADCAST_ALL_ACTIVITY);
        intent.putExtra(APP.TASK, APP.TASK_INIT_FAIL);
        sendBroadcast(intent);
        finish();
    }

    private void start_init(boolean flag, String ERROR) {
        if(flag){

            Intent intent = new Intent(APP.BROADCAST_ALL_ACTIVITY);
            intent.putExtra(APP.TASK, APP.TASK_INIT_FINISH);
            sendBroadcast(intent);

            APP.IS_FIRST_RUN = false;
            SharedPreferences sp = WelcomeActivity.this.getSharedPreferences(APP.GLOBAL, ContextWrapper.MODE_PRIVATE);
            SharedPreferences.Editor editer = sp.edit();
            editer.putBoolean(APP.FIRST_RUN, false);
            editer.apply();

            progressbar.setVisibility(ProgressBar.INVISIBLE);
            textView.setText(R.string.initialization_suceess);
            Toast.makeText(WelcomeActivity.this, R.string.initialization_suceess, Toast.LENGTH_SHORT).show();
            finish();

        }else{
            progressbar.setVisibility(ProgressBar.INVISIBLE);
            textView.setText(R.string.initialization_failure + "\n" + ERROR);
            button_ok.setClickable(true);
        }

    }


}
