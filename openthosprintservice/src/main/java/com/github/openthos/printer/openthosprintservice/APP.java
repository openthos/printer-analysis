package com.github.openthos.printer.openthosprintservice;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;

import com.github.openthos.printer.openthosprintservice.model.DriveItem;
import com.github.openthos.printer.openthosprintservice.model.DriveItemHelper;
import com.github.openthos.printer.openthosprintservice.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bboxh on 2016/4/12.
 */
public class APP extends Application {

    private static final String TAG = "APP";

    public static final String GLOBAL = "global";
    public static final String FIRST_RUN = "frist_run";
    public static final String BROADCAST_ALL_ACTIVITY = "com.github.openthos.printer.openthosprintservice.broadcast_all_activity";
    public static final int TASK_DEFAULT = 1000;
    public static final int TASK_INIT_FAIL = 1006;
    public static final int TASK_INIT_FINISH = 1007;
    public static final String COMPONENT_FILE_NAME_x86 = "component_x86.zip";
    public static final String TASK = "task";
    public static final int TASK_DETECT_USB_PRINTER = 1008;
    public static final int TASK_ADD_NEW_PRINTER = 1009;
    public static final int TASK_REFRESH_ADDED_PRINTERS = 1010;

    public static final String DOCU_FILE = "docu.pdf";
    public static final int TASK_JOB_RESULT = 1011;
    public static final String MESSAGE = "message";
    public static final String RESULT = "result";
    public static final String JOBID = "jobid";

    public static boolean MANAGEMENT_ACTIVITY_ON_TOP = false;

    public static boolean IS_LOGE = true;
    public static boolean IS_LOGD = true;
    public static boolean IS_FIRST_RUN = true;
    public static boolean IS_LOGI = true;

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

        SharedPreferences sp = this.getSharedPreferences(GLOBAL, ContextWrapper.MODE_PRIVATE);
        boolean first_run = sp.getBoolean(FIRST_RUN, true);
        if (first_run) {
            IS_FIRST_RUN = true;
            initData();
        } else {
            IS_FIRST_RUN = false;
        }

    }

    private void initData() {
        DriveItemHelper helper = new DriveItemHelper();
        DriveItem item = new DriveItem(1, "gs&foo2zjs", 1008, 42);
        helper.insert(item);
        helper.close();
    }

    public static Context getApplicatioContext(){
        if(context == null){
            LogUtils.e(TAG, "getApplicatioContext() == null");
        }
        return context;
    }

}
