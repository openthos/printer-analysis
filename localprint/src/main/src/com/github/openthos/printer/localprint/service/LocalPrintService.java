package com.github.openthos.printer.localprint.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.systemui.statusbar.phone.PrinterJobStatus;
import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.task.JobQueryTask;
import com.github.openthos.printer.localprint.ui.ManagementActivity;

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
            case APP.TASK_DETECT_USB_PRINTER:
                detectPrinter();
                break;
            case APP.TASK_ADD_NEW_PRINTER:
                showAddPrinterDialog();
                break;
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

        boolean STATUS_READY_THIS_TIME = false;

        APP.IS_JOB_WAITING_FOR_PRINTER = false;

        for (PrinterJobStatus item : list) {
            if (item.getStatus() == PrinterJobStatus.STATUS_PRINTING) {

                if (IS_REFRESHING_JOBS) {
                    break;
                }

                setDelayRefresh();
                break;
            } else if (item.getStatus() == PrinterJobStatus.STATUS_WAITING_FOR_PRINTER) {
                APP.IS_JOB_WAITING_FOR_PRINTER = true;
            } else if (!STATUS_READY_THIS_TIME
                    && item.getStatus() == PrinterJobStatus.STATUS_READY) {
                STATUS_READY_THIS_TIME = true;
                if (!APP.STATUS_READY) {
                    setDelayRefresh();
                    APP.STATUS_READY = true;
                }
            }
        }

        if (APP.STATUS_READY ){
            APP.STATUS_READY = false;
        };

        ArrayList<Parcelable> remoteList = new ArrayList<>();

        Map<String, ArrayList<PrinterJobStatus>> sortMap = new HashMap<>();

        for (PrinterJobStatus item : list) {
            ArrayList<PrinterJobStatus> nameSortedList = sortMap.get(item.getPrinter());
            if (nameSortedList == null) {
                nameSortedList = new ArrayList<>();
                sortMap.put(item.getPrinter(), nameSortedList);
            }
            nameSortedList.add(item);
        }

        for (ArrayList<PrinterJobStatus> nameSortedList : sortMap.values()) {
            remoteList.addAll(nameSortedList);
        }

        //send broadcast to inform others that jobs info has been refreshed.
        Intent intent = new Intent(APP.BROADCAST_REFRESH_JOBS);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("jobs", remoteList);
        intent.putExtra("jobs", bundle);
        sendBroadcast(intent);

    }

    private void setDelayRefresh() {
        //Refresh later when there has printing jobs
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshJobs();
                IS_REFRESHING_JOBS = false;
            }
        }, APP.JOB_REFRESH_INTERVAL);
        IS_REFRESHING_JOBS = true;
    }

    // TODO: 2016/5/10 send task to check whether there has new printer pulgged in with Android API.
    private void detectPrinter() {
        //TaskUtils.execute(new DetectPrinterTask(TAG));
    }

    private void showAddPrinterDialog() {

        AlertDialog.Builder builder
                = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog)
                       .setTitle(R.string.new_printer__notification)
                       .setMessage(R.string.whether_add_new_printer)
                       .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent
                                = new Intent(LocalPrintService.this, ManagementActivity.class);
                        //Context is not in the activity stack need the flag
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(APP.TASK, APP.TASK_ADD_NEW_PRINTER);
                        APP.getApplicatioContext().startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}