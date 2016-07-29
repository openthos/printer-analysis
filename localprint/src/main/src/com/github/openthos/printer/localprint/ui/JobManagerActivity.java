package com.github.openthos.printer.localprint.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.android.systemui.statusbar.phone.PrinterJobStatus;
import com.github.openthos.printer.localprint.task.JobCancelAllTask;
import com.github.openthos.printer.localprint.task.JobPauseAllTask;
import com.github.openthos.printer.localprint.task.JobResumeAllTask;
import com.github.openthos.printer.localprint.ui.adapter.JobAdapter;

import java.util.List;

public class JobManagerActivity extends BaseActivity {

    private ListView mListviewJob;
    private List<PrinterJobStatus> mList;
    private JobAdapter mJobAdapter;

    private BroadcastReceiver jobReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mJobAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_job_manager);

        mListviewJob = (ListView) findViewById(R.id.listview_job);
        mList = APP.getJobList();
        mJobAdapter = new JobAdapter(this, mList);
        mListviewJob.setAdapter(mJobAdapter);

        Button button_pause_all = (Button) findViewById(R.id.button_pause_all);
        Button button_start_all = (Button) findViewById(R.id.button_start_all);
        Button button_cancel_all = (Button) findViewById(R.id.button_cancel_all);
        Button button_ok = (Button) findViewById(R.id.button_ok);

        button_pause_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseAll();
            }
        });
        button_start_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAll();
            }
        });
        button_cancel_all.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelAll();
            }
        });
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok();
            }
        });

        registerReceiver(jobReceiver, new IntentFilter(APP.BROADCAST_REFRESH_JOBS));

    }

    private void ok() {
        this.finish();
    }

    private void cancelAll() {

        JobCancelAllTask<Void, Void> task = new JobCancelAllTask<Void, Void>(mList) {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(JobManagerActivity.this,
                                   R.string.canceled, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(JobManagerActivity.this,
                                   R.string.cancel_error, Toast.LENGTH_SHORT).show();
                }
            }
        };

        task.start();

    }

    private void startAll() {
        JobResumeAllTask<Void, Void> task = new JobResumeAllTask<Void, Void>(mList) {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(JobManagerActivity.this,
                                   R.string.started, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(JobManagerActivity.this,
                                   R.string.start_error, Toast.LENGTH_SHORT).show();
                }
            }
        };

        task.start();
    }

    private void pauseAll() {

        JobPauseAllTask<Void, Void> task = new JobPauseAllTask<Void, Void>(mList) {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(JobManagerActivity.this,
                                   R.string.paused, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(JobManagerActivity.this,
                                   R.string.pause_error, Toast.LENGTH_SHORT).show();
                }
            }
        };

        task.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        APP.sendRefreshJobsIntent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(jobReceiver);
    }

    @Override
    protected String bindTAG() {
        return "JobManagerActivity";
    }
}
