package com.github.openthos.printer.openthosprintservice.service;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.print.PrintJobInfo;
import android.printservice.PrintDocument;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.view.WindowManager;

import com.github.openthos.printer.openthosprintservice.APP;
import com.github.openthos.printer.openthosprintservice.R;
import com.github.openthos.printer.openthosprintservice.task.DetectPrinterTask;
import com.github.openthos.printer.openthosprintservice.task.PrintGsFoo2zjsTask;
import com.github.openthos.printer.openthosprintservice.ui.ManagementActivity;
import com.github.openthos.printer.openthosprintservice.util.LogUtils;
import com.github.openthos.printer.openthosprintservice.util.TaskUtils;

import java.util.List;

public class OpenthosPrintService extends PrintService {

    private static final String TAG = "OpenthosPrintService";

    /**
     * 系统请求寻找打印机
     * @return
     */
    @Override
    protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {
        return new PrintDiscoverySession(this);
    }

    /**
     * 系统请求取消打印任务
     * @param printJob
     */
    @Override
    protected void onRequestCancelPrintJob(PrintJob printJob) {

    }

    /**
     * 系统发出新的打印任务
     * @param printJob
     */
    @Override
    protected void onPrintJobQueued(PrintJob printJob) {
        LogUtils.d(TAG, "onPrintJobQueued()");

        PrintJobInfo printjobinfo = printJob.getInfo();
        PrintDocument printdocument = printJob.getDocument();

        TaskUtils.execute(new PrintGsFoo2zjsTask(TAG, printJob.getId().toString(), printdocument.getData(), printJob.getInfo().getPrinterId().getLocalId()));
        printJob.start();

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent == null){
            return START_STICKY;
        }

        int task = intent.getIntExtra(APP.TASK, APP.TASK_DEFAULT);

        LogUtils.d(TAG, "task -> " + task);

        switch(task){
            case APP.TASK_DETECT_USB_PRINTER:
                detectPrinter();
                break;
            case APP.TASK_ADD_NEW_PRINTER:
                showAddPrinterDialog();
                break;
            case APP.TASK_JOB_RESULT:
                handleJobResult(intent.getBooleanExtra(APP.RESULT, false), intent.getStringExtra(APP.JOBID), intent.getStringExtra(APP.MESSAGE));
                break;
            case APP.TASK_DEFAULT:
                break;
        }

        return START_STICKY;
    }

    private void handleJobResult(boolean result, String jobId, String message) {

        PrintJob jobitem = null;

        List<PrintJob> jobs = getActivePrintJobs();
        for(PrintJob job: jobs){
            if(job.getId().toString().equals(jobId)){
                jobitem = job;
            }
        }

        if(jobitem == null){
            LogUtils.d(TAG, "empty jobitem");
            return;
        }

        if(result){
            jobitem.complete();
        }else{
            jobitem.fail(message);
        }

    }

    private void detectPrinter(){
        TaskUtils.execute(new DetectPrinterTask(TAG));
    }

    /**
     * 显示有新打印机插入的dialog提示
     * remix等部分系统下不能自动弹出
     */
    private void showAddPrinterDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog)
                .setTitle(R.string.new_printer__notification)
                .setMessage(R.string.whether_add_new_printer)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(OpenthosPrintService.this, ManagementActivity.class);
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

}
