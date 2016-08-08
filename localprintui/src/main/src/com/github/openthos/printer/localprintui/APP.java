package com.github.openthos.printer.localprintui;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.widget.Toast;

import com.android.systemui.statusbar.phone.PrinterJobStatus;
import com.github.openthos.printer.localprint.aidl.IAddPrinterTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IAppCallBack;
import com.github.openthos.printer.localprint.aidl.IDeletePrinterTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IInitTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobCancelAllTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobCancelTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobPauseAllTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobPauseTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobResumeAllTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IJobResumeTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IListAddedTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IPrintTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IQueryPrinterCupsOptionsTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IQueryPrinterOptionsTaskCallBack;
import com.github.openthos.printer.localprint.aidl.ISearchModelsTaskCallBack;
import com.github.openthos.printer.localprint.aidl.ISearchPrintersTaskCallBack;
import com.github.openthos.printer.localprint.aidl.ITaskAidlInterface;
import com.github.openthos.printer.localprint.aidl.IUpdatePrinterCupsOptionsTaskCallBack;
import com.github.openthos.printer.localprint.aidl.IUpdatePrinterOptionsTaskCallBack;
import com.github.openthos.printer.localprintui.util.LogUtils;

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
    public static final String COMPONENT_PATH = "/component_26";

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

    public static final String LP_PRINTER = "printer";
    public static final String LP_FILE = "file";

    /**
     * The port of cups is used for send commands or browse the web page.
     * The port value must be same with the cups configuration file.
     */
    public static int CUPS_PORT = 6310;

    public static final long FIRTST_CONNECT_SERVICE_DELAY_TIME = 1000;

    public static boolean IS_LOGE = true;
    public static boolean IS_LOGD = true;
    public static boolean IS_LOGI = true;

    /**
     * Need sync with server app.
     */
    public static boolean IS_FIRST_RUN = false;
    public static boolean IS_INITIALIZING = false;
    public static boolean IS_MANAGEMENT_ACTIVITY_ON_TOP = false;

    private static APP mAPP;

    private static List<PrinterJobStatus> mJobList = new LinkedList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mAPP = this;
        LogUtils.d(TAG, "onCreate()");
        init();
    }

    private void init() {
        connect();
    }

    public static List<PrinterJobStatus> getJobList() {
        return mJobList;
    }

    static ITaskAidlInterface mIRemoteService;
    private static ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {

            mIRemoteService = ITaskAidlInterface.Stub.asInterface(service);

            try {
                APP.remoteExec(new IAppCallBack.Stub() {
                    @Override
                    public void IS_FIRST_RUN(boolean aBoolean) throws RemoteException {
                        APP.IS_FIRST_RUN = aBoolean;
                    }

                    @Override
                    public void IS_INITIALIZING(boolean aBoolean) throws RemoteException {
                        APP.IS_INITIALIZING = aBoolean;
                    }

                    @Override
                    public void setJobList(List jobList) throws RemoteException {
                        mJobList.clear();
                        mJobList.addAll(jobList);
                    }

                    @Override
                    public boolean IS_MANAGEMENT_ACTIVITY_ON_TOP() throws RemoteException {
                        return IS_MANAGEMENT_ACTIVITY_ON_TOP;
                    }

                });
            } catch (RemoteException e) {
                e.printStackTrace();
                LogUtils.d(TAG, "IAppCallBack connect_service_error");
                Toast.makeText(mAPP, R.string.connect_service_error, Toast.LENGTH_SHORT).show();
            }

        }

        public void onServiceDisconnected(ComponentName className) {
            LogUtils.e(TAG, "Service has unexpectedly disconnected");
            mIRemoteService = null;
        }
    };

    public static ITaskAidlInterface getIRemoteService() {
        return mIRemoteService;
    }

    public static boolean remoteExec(Object callBack) throws RemoteException {

        if (mIRemoteService == null) {
            connect();
            return false;
        }

        exec(callBack);

        return true;
    }

    private static void connect() {
        Intent intent = new Intent();
        intent.setAction("com.github.openthos.printer.localprint.IAppAidlInterface");
        intent.setPackage("com.github.openthos.printer.localprint");
        mAPP.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private static void exec(Object callBack) throws RemoteException {
        if (callBack instanceof IInitTaskCallBack) {
            mIRemoteService.IInitTaskCallBack((IInitTaskCallBack) callBack);
        } else if (callBack instanceof IAddPrinterTaskCallBack) {
            mIRemoteService.IAddPrinterTaskCallBack((IAddPrinterTaskCallBack) callBack);
        } else if (callBack instanceof IDeletePrinterTaskCallBack) {
            mIRemoteService.IDeletePrinterTaskCallBack((IDeletePrinterTaskCallBack) callBack);
        } else if (callBack instanceof IJobCancelAllTaskCallBack) {
            mIRemoteService.IJobCancelAllTaskCallBack((IJobCancelAllTaskCallBack) callBack);
        } else if (callBack instanceof IJobCancelTaskCallBack) {
            mIRemoteService.IJobCancelTaskCallBack((IJobCancelTaskCallBack) callBack);
        } else if (callBack instanceof IJobPauseAllTaskCallBack) {
            mIRemoteService.IJobPauseAllTaskCallBack((IJobPauseAllTaskCallBack) callBack);
        } else if (callBack instanceof IJobPauseTaskCallBack) {
            mIRemoteService.IJobPauseTaskCallBack((IJobPauseTaskCallBack) callBack);
        } else if (callBack instanceof IJobResumeAllTaskCallBack) {
            mIRemoteService.IJobResumeAllTaskCallBack((IJobResumeAllTaskCallBack) callBack);
        } else if (callBack instanceof IJobResumeTaskCallBack) {
            mIRemoteService.IJobResumeTaskCallBack((IJobResumeTaskCallBack) callBack);
        } else if (callBack instanceof IListAddedTaskCallBack) {
            mIRemoteService.IListAddedTaskCallBack((IListAddedTaskCallBack) callBack);
        } else if (callBack instanceof IPrintTaskCallBack) {
            mIRemoteService.IPrintTaskCallBack((IPrintTaskCallBack) callBack);
        } else if (callBack instanceof IQueryPrinterCupsOptionsTaskCallBack) {
            mIRemoteService.IQueryPrinterCupsOptionsTaskCallBack((IQueryPrinterCupsOptionsTaskCallBack) callBack);
        } else if (callBack instanceof IQueryPrinterOptionsTaskCallBack) {
            mIRemoteService.IQueryPrinterOptionsTaskCallBack((IQueryPrinterOptionsTaskCallBack) callBack);
        } else if (callBack instanceof ISearchModelsTaskCallBack) {
            mIRemoteService.ISearchModelsTaskCallBack((ISearchModelsTaskCallBack) callBack);
        } else if (callBack instanceof ISearchPrintersTaskCallBack) {
            mIRemoteService.ISearchPrintersTaskCallBack((ISearchPrintersTaskCallBack) callBack);
        } else if (callBack instanceof IUpdatePrinterCupsOptionsTaskCallBack) {
            mIRemoteService.IUpdatePrinterCupsOptionsTaskCallBack((IUpdatePrinterCupsOptionsTaskCallBack) callBack);
        } else if (callBack instanceof IUpdatePrinterOptionsTaskCallBack) {
            mIRemoteService.IUpdatePrinterOptionsTaskCallBack((IUpdatePrinterOptionsTaskCallBack) callBack);
        } else if (callBack instanceof IAppCallBack) {
            mIRemoteService.IAppCallBack((IAppCallBack) callBack);
        }
    }

    public static void initFailed() {
        if (mIRemoteService == null) {
            return;
        }
        try {
            mIRemoteService.initFailed();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void initSucceed() {
        if (mIRemoteService == null) {
            return;
        }
        try {
            mIRemoteService.initSucceed();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static void sendRefreshJobsIntent() {
        if (mIRemoteService == null) {
            return;
        }
        try {
            mIRemoteService.sendRefreshJobsIntent();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
