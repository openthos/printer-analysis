package com.github.openthos.printer.localprint.service;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.print.PageRange;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.util.Log;
import android.view.WindowManager;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.android.systemui.statusbar.phone.PrinterJobStatus;
import com.github.openthos.printer.localprint.model.PrinterOptionItem;
import com.github.openthos.printer.localprint.task.JobCancelTask;
import com.github.openthos.printer.localprint.task.PrintTask;
import com.github.openthos.printer.localprint.ui.ManagementActivity;
import com.github.openthos.printer.localprint.util.FileUtils;
import com.github.openthos.printer.localprint.util.LogUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenthosPrintService extends PrintService {

    private static final String TAG = "OpenthosPrintService";

    /**
     * System ask for searching printers
     *
     * @return PrinterDiscoverySession
     */
    @Override
    protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {
        return new PrintDiscoverySession(this);
    }

    /**
     * System ask for canceling a printJob
     *
     * @param printJob PrintJob
     */
    @Override
    protected void onRequestCancelPrintJob(final PrintJob printJob) {

        JobCancelTask<Void> task = new JobCancelTask<Void>() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                printJob.cancel();
            }
        };

        String jobId = printJob.getTag();

        if (jobId == null) {
            printJob.cancel();
        } else {
            PrinterJobStatus item = new PrinterJobStatus();
            item.setJobId(Integer.parseInt(jobId));
            task.start(item);
        }

    }

    /**
     * System send out a new printJob.
     *
     * @param printJob PrintJob
     */
    @Override
    protected void onPrintJobQueued(final PrintJob printJob) {
        LogUtils.d(TAG, "onPrintJobQueued()");

        final String docu_file_path = FileUtils.getDocuFilePath(printJob.getId().toString());

        Map<String, String> map = new HashMap<>();
        map.put(PrintTask.LP_PRINTER, printJob.getInfo().getPrinterId().getLocalId());
        map.put(PrintTask.LP_FILE, FileUtils.getDocuFileName(printJob.getId().toString()));
        map.put(PrintTask.LP_MEDIA,
                PrinterOptionItem.media2cups(printJob.getInfo().getAttributes().getMediaSize()));
        //map.put(PrintTask.LP_RESOLUTION,
        //  PrinterOptionItem.resulution2cups(printJob.getInfo().getAttributes().getResolution()));
        //map.put(PrintTask.LP_COLOR, "");
        //map.put(PrintTask.LP_LANDSCAPE,"");     //System may has handled
        map.put(PrintTask.LP_COPIES, String.valueOf(printJob.getInfo().getCopies()));
        map.put(PrintTask.LP_LABEL, printJob.getDocument().getInfo().getName());

        PageRange[] ranges = printJob.getInfo().getPages();
        StringBuilder rangeStr = new StringBuilder();
        if (ranges != null && ranges.length > 0) {
            for (PageRange range : ranges) {

                if (range.getStart() == 0 && range.getEnd() == Integer.MAX_VALUE) {
                    break;
                }

                if (rangeStr.length() > 0) {
                    rangeStr.append(",");
                }

                rangeStr.append(range.getStart() + 1);

                if (range.getStart() == range.getEnd()) {
                    continue;
                }
                rangeStr.append("-").append(range.getEnd() + 1);

            }
            if (rangeStr.length() > 0) {
                map.put(PrintTask.LP_RANGES, rangeStr.toString());
            }
        }

        //Send a printing job.

        boolean flag = FileUtils.copyFile(docu_file_path, printJob.getDocument().getData());
        if (!flag) {
            printJob.fail(getResources().getString(R.string.print_copy_file_failed));
            return;
        }

        PrintTask<Void> task = new PrintTask<Void>() {

            @Override
            protected void onPreExecute() {

                //Must operate printJob in the main thread.
                printJob.start();
            }

            @Override
            protected void onPostExecute(String jobId) {

                new File(docu_file_path).delete();

                if (jobId == null) {
                    printJob.fail(ERROR);
                } else {
                    //Fill the job id got from CUPS.
                    printJob.setTag(String.valueOf(jobId));
                    printJob.complete();
                }
            }
        };

        task.start(map);

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Receive a task in the intent.
     *
     * @param intent  Intent
     * @param flags   int
     * @param startId int
     * @return handle killed event
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return START_STICKY;
        }

        int task = intent.getIntExtra(APP.TASK, APP.TASK_DEFAULT);

        LogUtils.d(TAG, "task -> " + task);

        switch (task) {
            case APP.TASK_DETECT_USB_PRINTER:
                detectPrinter();
                break;
            case APP.TASK_ADD_NEW_PRINTER:
                showAddPrinterDialog();
                break;
            case APP.TASK_JOB_RESULT:
                handleJobResult(intent.getBooleanExtra(APP.RESULT, false)
                        , intent.getStringExtra(APP.JOBID), intent.getStringExtra(APP.MESSAGE));
                break;
            case APP.TASK_DEFAULT:
                break;
        }

        /**
         * START_STICKY
         * when the service is killed , it will start automatically,not keep the Intent
         */
        return START_STICKY;
    }

    private void handleJobResult(boolean result, String jobId, String message) {

        PrintJob jobitem = null;

        List<PrintJob> jobs = getActivePrintJobs();
        for (PrintJob job : jobs) {
            if (job.getId().toString().equals(jobId)) {
                jobitem = job;
            }
        }

        if (jobitem == null) {
            LogUtils.d(TAG, "empty jobitem");
            return;
        }

        if (result) {
            jobitem.complete();
        } else {
            jobitem.fail(message);
        }

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
                                = new Intent(OpenthosPrintService.this, ManagementActivity.class);
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

}
