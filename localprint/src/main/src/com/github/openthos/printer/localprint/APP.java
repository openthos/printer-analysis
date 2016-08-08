package com.github.openthos.printer.localprint;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;

import com.android.systemui.statusbar.phone.PrinterJobStatus;
import com.github.openthos.printer.localprint.service.LocalPrintService;
import com.github.openthos.printer.localprint.task.InitTask;
import com.github.openthos.printer.localprint.ui.WelcomeActivity;
import com.github.openthos.printer.localprint.util.LogUtils;

import java.util.LinkedList;
import java.util.List;

public class APP extends Application {

    private static final String TAG = "APP";

    public static final String BROADCAST_ALL_ACTIVITY
            = "com.github.openthos.printer.openthosprintservice.broadcast_all_activity";
    public static final String BROADCAST_REFRESH_JOBS
            = "com.github.openthos.printer.openthosprintservice.broadcast_refresh_jobs";

    public static final String GLOBAL = "global";
    public static final String FIRST_RUN = "frist_run";
    public static final String TASK = "task";
    public static final String DOCU_FILE = "docu.pdf";
    public static final String MESSAGE = "message";
    public static final String RESULT = "result";
    public static final String JOBID = "jobid";
    public static final String PRINTER_NAME = "printer_name";

    /**
     * The cups executable file folder name && The name of cups data packet.
     */
    public static final String COMPONENT_PATH = "/component_27";

    /**
     * The data packet position.
     */
    public static final String COMPONENT_SOURCE_PATH = "/system/component_printer.tar.gz";

    public static final int TASK_DEFAULT = 1000;
    public static final int TASK_INIT_FAIL = 1006;
    public static final int TASK_INIT_FINISH = 1007;
    public static final int TASK_DETECT_USB_PRINTER = 1008;
    public static final int TASK_ADD_NEW_PRINTER = 1009;
    public static final int TASK_REFRESH_ADDED_PRINTERS = 1010;
    public static final int TASK_JOB_RESULT = 1011;
    public static final int TASK_REFRESH_JOBS = 1012;
    public static final int TASK_ADD_NEW_NET_PRINTER = 1013;
    public static final int TASK_INIT = 1014;

    /**
     * The print job refresh interval (ms).
     */
    public static final long JOB_REFRESH_INTERVAL = 4000;

    /**
     * Print job refresh interval cause by some printers plugged in.
     * The scanning time in cups is 5s , so the value cannot less than 5s
     * Unit: ms.
     */
    public static final long JOB_REFRESH_WAITING_PRINTER_INTERVAL = 5100;

    /**
     * The port of cups is used for send commands or browse the web page.
     * The port value must be same with the cups configuration file.
     */
    public static int CUPS_PORT = 6310;

    public static boolean IS_LOGE = true;
    public static boolean IS_LOGD = true;
    public static boolean IS_LOGI = true;
    public static boolean IS_FIRST_RUN = true;
    public static boolean IS_INITIALIZING = false;
    public static boolean IS_MANAGEMENT_ACTIVITY_ON_TOP = false;

    /**
     * Whether there has jobs waiting for printers becoming available.
     */
    public static boolean IS_JOB_WAITING_FOR_PRINTER = false;
    public static boolean STATUS_READY = false;

    /**
     * Bind cupsd process to the variate.
     */
    public static Process cupsdProcess;

    private static List<PrinterJobStatus> jobList = new LinkedList<PrinterJobStatus>();
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d(TAG, "onCreate()");
        init();
    }

    private void init() {

        context = getApplicationContext();

        IS_LOGI = true;
        IS_LOGE = true;
        IS_LOGD = true;

        //Check the current data version, promote update when the version is old.
        SharedPreferences sp = this.getSharedPreferences(GLOBAL, ContextWrapper.MODE_PRIVATE);
        String first_run = sp.getString(FIRST_RUN, "");

        if (!first_run.equals(COMPONENT_PATH)) {
            IS_FIRST_RUN = true;
        } else {
            IS_FIRST_RUN = false;
        }

        //Refresh printer Jobs when start up ths app.
        sendRefreshJobsIntent();

    }

    /**
     * Send a intent with refresh jobs requirement to LocalPrintService.
     */
    public static void sendRefreshJobsIntent() {
        Intent intent = new Intent(getApplicatioContext(), LocalPrintService.class);
        intent.putExtra(APP.TASK, APP.TASK_REFRESH_JOBS);
        getApplicatioContext().startService(intent);
    }

    /**
     * Get PrinterJobStatus list.
     * Forbid invoking JobList not in the main thread.
     *
     * @return Job List
     */
    public static List<PrinterJobStatus> getJobList() {

        //Judge current execution whether in the main thread
        if (Looper.getMainLooper() != Looper.myLooper()) {
            throw new RuntimeException("Forbid invoking JobList not in the main thread. ");
        }

        return jobList;
    }

    public static Context getApplicatioContext() {
        return context;
    }

    public static void initSucceed(Context context) {
        Intent intent = new Intent(APP.BROADCAST_ALL_ACTIVITY);
        intent.putExtra(APP.TASK, APP.TASK_INIT_FINISH);
        context.sendBroadcast(intent);

        APP.IS_FIRST_RUN = false;
        SharedPreferences sp = context.getSharedPreferences(APP.GLOBAL,
                ContextWrapper.MODE_PRIVATE);
        SharedPreferences.Editor editer = sp.edit();
        editer.putString(APP.FIRST_RUN, APP.COMPONENT_PATH);
        editer.apply();

        APP.sendRefreshJobsIntent();
    }

    public static void initFailed(Context context) {
        Intent intent = new Intent(APP.BROADCAST_ALL_ACTIVITY);
        intent.putExtra(APP.TASK, APP.TASK_INIT_FAIL);
        context.sendBroadcast(intent);
    }
}