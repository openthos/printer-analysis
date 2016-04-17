package com.github.openthos.printer.openthosprintservice.ui;


import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.openthos.printer.openthosprintservice.APP;
import com.github.openthos.printer.openthosprintservice.R;
import com.github.openthos.printer.openthosprintservice.task.BaseTask;
import com.github.openthos.printer.openthosprintservice.util.FileUtils;
import com.github.openthos.printer.openthosprintservice.util.TaskUtils;

import java.io.File;
import java.io.IOException;

public class WelcomeActivity extends Activity {

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
                TaskUtils.execute(new BaseTask(TAG){
                    @Override
                    public void run() {
                        start_init();
                    }
                });
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

    private void start_init() {
        if(unzip()){

            Intent intent = new Intent(APP.BROADCAST_ALL_ACTIVITY);
            intent.putExtra(APP.TASK, APP.TASK_INIT_FINISH);
            sendBroadcast(intent);

            APP.IS_FIRST_RUN = false;
            SharedPreferences sp = WelcomeActivity.this.getSharedPreferences(APP.GLOBAL, ContextWrapper.MODE_PRIVATE);
            SharedPreferences.Editor editer = sp.edit();
            editer.putBoolean(APP.FIRST_RUN, false);
            editer.apply();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressbar.setVisibility(ProgressBar.INVISIBLE);
                    textView.setText(R.string.initialization_suceess);
                    Toast.makeText(WelcomeActivity.this, R.string.initialization_suceess, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        }else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressbar.setVisibility(ProgressBar.INVISIBLE);
                    textView.setText(R.string.initialization_failure);
                    button_ok.setClickable(true);
                }
            });
        }

    }

    /**
     *
     * @return  成功返回true 失败返回 false
     */
    private boolean unzip() {
        boolean flag = false;

        try {
            FileUtils.UnZipInputStream(this.getAssets().open(getCompFileName()), this.getFilesDir().getAbsolutePath());
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file_gs = new File(FileUtils.getComponentPath() + "/gs");
        file_gs.setExecutable(true);
        File file_foo2zjs = new File(FileUtils.getComponentPath() + "/foo2zjs");
        file_foo2zjs.setExecutable(true);
        return flag;
    }

    private String getCompFileName() {
        return APP.COMPONENT_FILE_NAME_x86;
    }


}
