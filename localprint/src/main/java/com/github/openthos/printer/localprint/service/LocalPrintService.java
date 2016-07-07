package com.github.openthos.printer.localprint.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.model.JobItem;
import com.github.openthos.printer.localprint.task.JobQueryTask;

import java.util.List;

public class LocalPrintService extends Service {

    /**
     * Whether is refreshing jobs.
     */
    public static boolean IS_REFRESHING_JOBS = false;

    public LocalPrintService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int task = intent.getIntExtra(APP.TASK, APP.TASK_DEFAULT);
        switch (task) {
            case APP.TASK_REFRESH_JOBS:
                refreshJobs();
                break;
            default:
                break;
        }

        return START_NOT_STICKY;
    }

    /**
     * refresh jobs info.
     */
    private void refreshJobs() {
        final List<JobItem> list = APP.getJobList();
        new JobQueryTask<Void, Void>(list) {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    updateJobs(list);
                } else {
                    Toast.makeText(LocalPrintService.this, getString(R.string.query_error)
                            + " -refreshJobs- " + ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
    }

    private void updateJobs(List<JobItem> list) {

        APP.IS_JOB_WAITING_FOR_PRINTER = false;

        for (JobItem item : list) {
            if (item.getStatus() == JobItem.STATUS_PRINTING) {

                if (IS_REFRESHING_JOBS) {
                    break;
                }

                //Refresh later when there has printing jobs
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshJobs();
                        IS_REFRESHING_JOBS = false;
                    }
                }, APP.JOB_REFRESH_INTERVAL);
                IS_REFRESHING_JOBS = true;
                break;
            } else if (item.getStatus() == JobItem.STATUS_WAITING_FOR_PRINTER) {
                APP.IS_JOB_WAITING_FOR_PRINTER = true;
                continue;
            }
        }

        //send broadcast to inform others that jobs info refreshed.
        sendBroadcast(new Intent(APP.BROADCAST_REFRESH_JOBS));

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
