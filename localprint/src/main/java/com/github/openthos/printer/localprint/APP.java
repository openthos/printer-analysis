package com.github.openthos.printer.localprint;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;

import com.github.openthos.printer.localprint.model.JobItem;
import com.github.openthos.printer.localprint.service.LocalPrintService;
import com.github.openthos.printer.localprint.util.LogUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by bboxh on 2016/5/10.
 */
public class APP extends Application{

    private static final String TAG = "APP";

    public static final String BROADCAST_ALL_ACTIVITY = "com.github.openthos.printer.openthosprintservice.broadcast_all_activity";
    public static final String BROADCAST_REFRESH_JOBS = "com.github.openthos.printer.openthosprintservice.broadcast_refresh_jobs";

    public static final String GLOBAL = "global";
    public static final String FIRST_RUN = "is_frist_run";
    public static final String COMPONENT_FILE_NAME_x86 = "component_x86.zip";
    public static final String TASK = "task";

    public static final int TASK_DEFAULT = 1000;
    public static final int TASK_INIT_FAIL = 1006;
    public static final int TASK_INIT_FINISH = 1007;
    public static final int TASK_DETECT_USB_PRINTER = 1008;
    public static final int TASK_ADD_NEW_PRINTER = 1009;
    public static final int TASK_REFRESH_ADDED_PRINTERS = 1010;
    public static final int TASK_JOB_RESULT = 1011;
    public static final int TASK_REFRESH_JOBS = 1012;

    public static final String DOCU_FILE = "docu.pdf";
    public static final String MESSAGE = "message";
    public static final String RESULT = "result";
    public static final String JOBID = "jobid";
    public static final String COMPONENT_PATH = "/component_22";        //文件所在文件夹
    public static final String COMPONENT_SOURCE_PATH = "/system";       //打印数据包位置

    public static final long JOB_REFRESH_INTERVAL = 4000;                       //打印任务刷新间隔 单位：毫秒
    public static final long JOB_REFRESH_WAITING_PRINTER_INTERVAL = 5100;       //更新由打印机可用导致的任务刷新时间，CUPS中扫描间隔是5秒，所以不能少于5秒 单位：毫秒

    public static final int NOTIFY_JOBS_ID = 1000;          //打印任务 通知栏 编号
    public static int CUPS_PORT = 6310;                     //CUPS端口号

    public static boolean IS_LOGE = true;
    public static boolean IS_LOGD = true;
    public static boolean IS_LOGI = true;
    public static boolean IS_FIRST_RUN = true;                      //是否第一次运行
    public static boolean IS_MANAGEMENT_ACTIVITY_ON_TOP = false;       //管理界面是否在最前端
    public static boolean IS_NOTIFICATION = true;                   //是否开启通知栏
    public static boolean IS_JOB_WAITING_FOR_PRINTER = false;       //是否存在 等待打印打印机可用的任务

    public static Process cupsdProcess;                             //绑定到CUPS运行的进程
    private static List<JobItem> jobList = new LinkedList<JobItem>();       //打印任务列表数据，必须在主线程里操作


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

        //读取SharedPreferences，检测是否是第一次运行

        SharedPreferences sp = this.getSharedPreferences(GLOBAL, ContextWrapper.MODE_PRIVATE);
        String first_run = sp.getString(FIRST_RUN, "");

        // 屏蔽首次运行检测
        //first_run = false;

        if (!first_run.equals(COMPONENT_PATH)) {
            IS_FIRST_RUN = true;
            initData();
        } else {
            IS_FIRST_RUN = false;
        }


        //启动时就通知更新打印任务信息
        sendRefreshJobsIntent();

    }

    private void initData() {

    }

    /**
     *发送更新打印任务信息Intent给LocalPrintService
     */
    public static void sendRefreshJobsIntent(){
        Intent intent = new Intent(getApplicatioContext(), LocalPrintService.class);
        intent.putExtra(APP.TASK, APP.TASK_REFRESH_JOBS);
        getApplicatioContext().startService(intent);
    }

    /**
     * 获取任务列表数据
     * 不允许非主线程使用
     * @return
     */
    public static List<JobItem> getJobList(){

        //判断是否是主线程
        if(Looper.getMainLooper() != Looper.myLooper()){
            throw new RuntimeException("can not invoke JobList not in the main thread. ");
        }

        return jobList;
    }

    /**
     * 获取应用context
     * @return
     */
    public static Context getApplicatioContext(){
        return context;
    }

}
