package com.github.openthos.printer.localprint.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.widget.Toast;

import com.android.systemui.statusbar.phone.PrinterJobStatus;
import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.task.JobQueryTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final List<PrinterJobStatus> list = APP.getJobList();
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

    private void updateJobs(List<PrinterJobStatus> list) {

        APP.IS_JOB_WAITING_FOR_PRINTER = false;

        for (PrinterJobStatus item : list) {
            if (item.getStatus() == PrinterJobStatus.STATUS_PRINTING) {

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
            } else if (item.getStatus() == PrinterJobStatus.STATUS_WAITING_FOR_PRINTER) {
                APP.IS_JOB_WAITING_FOR_PRINTER = true;
                continue;
            }
        }

        ArrayList<Parcelable> remoteList = new ArrayList<>();

        Map<String, ArrayList<PrinterJobStatus>> sortMap = new HashMap<>();

        for (PrinterJobStatus item : list) {
            ArrayList<PrinterJobStatus> nameSortedList = sortMap.get(item.getPrinter());
            if (nameSortedList == null) {
                nameSortedList = new ArrayList<>();
                sortMap.put(item.getPrinter(),nameSortedList);
            }
            nameSortedList.add(item);
        }

        for (ArrayList<PrinterJobStatus> nameSortedList: sortMap.values()) {
            remoteList.addAll(nameSortedList);
        }

        //send broadcast to inform others that jobs info has been refreshed.
        Intent intent = new Intent(APP.BROADCAST_REFRESH_JOBS);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("jobs", remoteList);
        intent.putExtra("jobs", bundle);
        sendBroadcast(intent);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
