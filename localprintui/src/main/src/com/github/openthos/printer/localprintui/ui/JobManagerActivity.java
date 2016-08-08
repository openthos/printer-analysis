package com.github.openthos.printer.localprintui.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.systemui.statusbar.phone.PrinterJobStatus;
import com.github.openthos.printer.localprint.aidl.IJobCancelAllTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobPauseAllTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobResumeAllTaskCallBack;
import com.github.openthos.printer.localprintui.APP;
import com.github.openthos.printer.localprintui.R;
import com.github.openthos.printer.localprintui.ui.adapter.JobAdapter;
import com.github.openthos.printer.localprintui.util.LogUtils;

import java.util.List;

public class JobManagerActivity extends BaseActivity {

    private static final String TAG = "JobManagerActivity";
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

        ListView mListviewJob = (ListView) findViewById(R.id.listview_job);
        List<PrinterJobStatus> mList = APP.getJobList();
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

        boolean flag = false;
        try {
            final Handler handler = new Handler();
            flag = APP.remoteExec(new IJobCancelAllTaskCallBack.Stub() {

                @Override
                public void onPostExecute(final boolean aBoolean) throws RemoteException {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (aBoolean) {
                                Toast.makeText(JobManagerActivity.this,
                                        R.string.canceled, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(JobManagerActivity.this,
                                        R.string.cancel_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (!flag) {
            LogUtils.d(TAG, "IJobCancelAllTaskCallBack connect_service_error");
            Toast.makeText(JobManagerActivity.this,
                    R.string.connect_service_error, Toast.LENGTH_SHORT).show();
        }

    }

    private void startAll() {

        boolean flag = false;
        try {
            final Handler handler = new Handler();
            flag = APP.remoteExec(new IJobResumeAllTaskCallBack.Stub() {

                @Override
                public void onPostExecute(final boolean aBoolean) throws RemoteException {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (aBoolean) {
                                Toast.makeText(JobManagerActivity.this,
                                        R.string.started, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(JobManagerActivity.this,
                                        R.string.start_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (!flag) {
            LogUtils.d(TAG, "IJobResumeAllTaskCallBack connect_service_error");
            Toast.makeText(JobManagerActivity.this,
                    R.string.connect_service_error, Toast.LENGTH_SHORT).show();
        }

    }

    private void pauseAll() {

        boolean flag = false;
        try {
            final Handler handler = new Handler();
            flag = APP.remoteExec(new IJobPauseAllTaskCallBack.Stub() {

                @Override
                public void onPostExecute(final boolean aBoolean) throws RemoteException {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (aBoolean) {
                                Toast.makeText(JobManagerActivity.this,
                                        R.string.paused, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(JobManagerActivity.this,
                                        R.string.pause_error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (!flag) {
            LogUtils.d(TAG, "IJobPauseAllTaskCallBack connect_service_error");
            Toast.makeText(JobManagerActivity.this,
                    R.string.connect_service_error, Toast.LENGTH_SHORT).show();
        }

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
