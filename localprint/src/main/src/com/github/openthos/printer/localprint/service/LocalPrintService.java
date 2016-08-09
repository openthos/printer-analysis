package com.github.openthos.printer.localprint.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.systemui.statusbar.phone.PrinterJobStatus;
import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
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
import com.github.openthos.printer.localprint.model.ModelsItem;
import com.github.openthos.printer.localprint.model.PrinterCupsOptionItem;
import com.github.openthos.printer.localprint.model.PrinterItem;
import com.github.openthos.printer.localprint.model.PrinterOptionItem;
import com.github.openthos.printer.localprint.task.AddPrinterTask;
import com.github.openthos.printer.localprint.task.CommandTask;
import com.github.openthos.printer.localprint.task.DeletePrinterTask;
import com.github.openthos.printer.localprint.task.DetectNewPrinterTask;
import com.github.openthos.printer.localprint.task.InitTask;
import com.github.openthos.printer.localprint.task.JobCancelAllTask;
import com.github.openthos.printer.localprint.task.JobCancelTask;
import com.github.openthos.printer.localprint.task.JobPauseAllTask;
import com.github.openthos.printer.localprint.task.JobPauseTask;
import com.github.openthos.printer.localprint.task.JobQueryTask;
import com.github.openthos.printer.localprint.task.JobResumeAllTask;
import com.github.openthos.printer.localprint.task.JobResumeTask;
import com.github.openthos.printer.localprint.task.ListAddedTask;
import com.github.openthos.printer.localprint.task.PrintTask;
import com.github.openthos.printer.localprint.task.QueryPrinterCupsOptionsTask;
import com.github.openthos.printer.localprint.task.QueryPrinterOptionsTask;
import com.github.openthos.printer.localprint.task.SearchModelsTask;
import com.github.openthos.printer.localprint.task.SearchPrintersTask;
import com.github.openthos.printer.localprint.task.UpdatePrinterCupsOptionsTask;
import com.github.openthos.printer.localprint.task.UpdatePrinterOptionsTask;
import com.github.openthos.printer.localprint.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LocalPrintService extends Service {

    private static final String TAG = "LocalPrintService";
    /**
     * Whether is refreshing jobs.
     */
    public static boolean IS_REFRESHING_JOBS = false;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy()");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        LogUtils.d(TAG, "onTaskRemoved()");
        reStartService();
    }

    private void reStartService() {
        CommandTask.killCups();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            return START_STICKY;
        }

        int task = intent.getIntExtra(APP.TASK, APP.TASK_DEFAULT);
        LogUtils.d(TAG, "APP.TASK ->" + task);
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
            case APP.TASK_INIT:
                bootCheck();
            default:
                break;
        }

        return START_STICKY;
    }

    private void bootCheck() {

        if (!APP.IS_FIRST_RUN) {
            return;
        }

        APP.IS_INITIALIZING = true;

        new InitTask<Void>() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                LogUtils.d(TAG, "init -> " + aBoolean);
                APP.IS_INITIALIZING = false;
                if (aBoolean) {
                    APP.initSucceed(LocalPrintService.this);
                } else {
                    APP.initFailed(LocalPrintService.this);
                }
            }
        }.start();

    }

    /**
     * refresh jobs info.
     */
    private void refreshJobs() {
        final List<PrinterJobStatus> list = APP.getJobList();
        new JobQueryTask<Void, Void>(list) {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean != null && aBoolean) {
                    updateJobs(list);
                    try {
                        if(APP.iAppCallBack != null){
                            APP.iAppCallBack.setJobList(list);
                        }

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
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

        if (APP.STATUS_READY) {
            APP.STATUS_READY = false;
        }
        ;

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

    private void detectPrinter() {

        UsbManager usbmanager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();
        LogUtils.d(TAG, "get device list  = " + deviceList.size());
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        ArrayList<String> usbList = new ArrayList<>();

        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            LogUtils.d(TAG, "detect device -> " + device.toString());
            for (int i = 0; i < device.getInterfaceCount(); i++) {
                // InterfaceClass 7
                if (device.getInterface(i).getInterfaceClass() == 7) {
                    usbList.add(device.getSerialNumber());
                }
            }
        }

        if (usbList.size() == 0) {
            return;
        }

        DetectNewPrinterTask<Void> task = new DetectNewPrinterTask<Void>() {
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    showAddPrinterDialog();
                }
            }
        };

        task.start(usbList.toArray(new String[0]));

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
                                = new Intent("com.github.openthos.printer.localprint.Management");
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
        return new ITaskAidlInterfaceImpl();
    }

    public class ITaskAidlInterfaceImpl extends ITaskAidlInterface.Stub {

        @Override
        public void IInitTaskCallBack(final IInitTaskCallBack callBack) throws RemoteException {
            new InitTask<Void>() {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    try {
                        callBack.onPostExecute(aBoolean, ERROR);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        @Override
        public void IAddPrinterTaskCallBack(final IAddPrinterTaskCallBack callBack) throws RemoteException {
            AddPrinterTask<Void> task = new AddPrinterTask<Void>() {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    try {
                        callBack.onPostExecute(aBoolean);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };

            task.start(callBack.bindStart());
        }

        @Override
        public void IDeletePrinterTaskCallBack(final IDeletePrinterTaskCallBack callBack) throws RemoteException {
            DeletePrinterTask<Void> task = new DeletePrinterTask<Void>() {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    try {
                        callBack.onPostExecute(aBoolean);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };
            task.start(callBack.bindStart());
        }

        @Override
        public void IJobCancelAllTaskCallBack(final IJobCancelAllTaskCallBack callBack) throws RemoteException {
            JobCancelAllTask<Void, Void> task = new JobCancelAllTask<Void, Void>(APP.getJobList()) {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    try {
                        callBack.onPostExecute(aBoolean);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };

            task.start();
        }

        @Override
        public void IJobCancelTaskCallBack(final IJobCancelTaskCallBack callBack) throws RemoteException {
            JobCancelTask<Void> task = new JobCancelTask<Void>() {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    try {
                        callBack.onPostExecute(aBoolean);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };
            task.start(callBack.bindStart());
        }

        @Override
        public void IJobPauseAllTaskCallBack(final IJobPauseAllTaskCallBack callBack) throws RemoteException {
            JobPauseAllTask<Void, Void> task = new JobPauseAllTask<Void, Void>(APP.getJobList()) {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    try {
                        callBack.onPostExecute(aBoolean);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };

            task.start();
        }

        @Override
        public void IJobPauseTaskCallBack(final IJobPauseTaskCallBack callBack) throws RemoteException {
            JobPauseTask<Void> task = new JobPauseTask<Void>() {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    try {
                        callBack.onPostExecute(aBoolean);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };
            task.start(callBack.bindStart());
        }

        @Override
        public void IJobResumeAllTaskCallBack(final IJobResumeAllTaskCallBack callBack) throws RemoteException {
            JobResumeAllTask<Void, Void> task = new JobResumeAllTask<Void, Void>(APP.getJobList()) {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    try {
                        callBack.onPostExecute(aBoolean);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };

            task.start();
        }

        @Override
        public void IJobResumeTaskCallBack(final IJobResumeTaskCallBack callBack) throws RemoteException {
            JobResumeTask<Void> task = new JobResumeTask<Void>() {
                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    try {
                        callBack.onPostExecute(aBoolean);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };
            task.start(callBack.bindStart());
        }

        @Override
        public void IListAddedTaskCallBack(final IListAddedTaskCallBack callBack) throws RemoteException {
            new ListAddedTask<Void, Void>() {
                @Override
                protected void onPostExecute(List<PrinterItem> printerItems) {
                    try {
                        callBack.onPostExecute(printerItems);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        @Override
        public void IPrintTaskCallBack(final IPrintTaskCallBack callBack) throws RemoteException {
            PrintTask<Void> task = new PrintTask<Void>() {
                @Override
                protected String bindFileName() {
                    return null;
                }

                @Override
                protected String bindPrinterName() {
                    try {
                        return callBack.bindPrinterName();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return "";
                }

                @Override
                protected void onPostExecute(String jobId) {
                    try {
                        callBack.onPostExecute(jobId, ERROR);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };
            task.start(callBack.bindStart());
        }

        @Override
        public void IQueryPrinterCupsOptionsTaskCallBack(final IQueryPrinterCupsOptionsTaskCallBack callBack) throws RemoteException {
            QueryPrinterCupsOptionsTask<Void> task = new QueryPrinterCupsOptionsTask<Void>() {

                @Override
                protected void onPostExecute(List<PrinterCupsOptionItem> printerOptionItems) {
                    try {
                        callBack.onPostExecute(printerOptionItems, ERROR);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

            };
            task.start(callBack.bindStart());
        }

        @Override
        public void IQueryPrinterOptionsTaskCallBack(final IQueryPrinterOptionsTaskCallBack callBack) throws RemoteException {
            QueryPrinterOptionsTask<Void> task = new QueryPrinterOptionsTask<Void>() {
                @Override
                protected void onPostExecute(PrinterOptionItem printerOptionItem) {
                    try {
                        callBack.onPostExecute(printerOptionItem);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };
            task.start(callBack.bindStart());
        }

        @Override
        public void ISearchModelsTaskCallBack(final ISearchModelsTaskCallBack callBack) throws RemoteException {
            new SearchModelsTask<Void, Void>() {
                @Override
                protected String bindPrinter() {
                    try {
                        return callBack.bindPrinter();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return "";
                }

                @Override
                protected void onPostExecute(ModelsItem modelsItem) {
                    try {
                        callBack.onPostExecute(modelsItem, ERROR);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        @Override
        public void ISearchPrintersTaskCallBack(final ISearchPrintersTaskCallBack callBack) throws RemoteException {
            SearchPrintersTask<Void, Void> task = new SearchPrintersTask<Void, Void>() {
                @Override
                protected void onPostExecute(List<PrinterItem> printerItems) {
                    try {
                        callBack.onPostExecute(printerItems);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };

            task.start();
        }

        @Override
        public void IUpdatePrinterCupsOptionsTaskCallBack(final IUpdatePrinterCupsOptionsTaskCallBack callBack) throws RemoteException {
            UpdatePrinterCupsOptionsTask<Void> task = new UpdatePrinterCupsOptionsTask<Void>() {

                @Override
                protected String getPrinter() {
                    try {
                        return callBack.getPrinter();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return "";
                }

                @Override
                protected void onPostExecute(Boolean flag) {
                    try {
                        callBack.onPostExecute(flag);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            };

            task.start(callBack.bindStart());
        }

        @Override
        public void IUpdatePrinterOptionsTaskCallBack(final IUpdatePrinterOptionsTaskCallBack callBack) throws RemoteException {
            UpdatePrinterOptionsTask<Void> task = new UpdatePrinterOptionsTask<Void>() {

                @Override
                protected void onPostExecute(Boolean aBoolean) {
                    try {
                        callBack.onPostExecute(aBoolean, ERROR);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                protected String getPrinter() {
                    try {
                        return callBack.getPrinter();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    return "";
                }
            };
            task.start(callBack.bindStart());
        }

        @Override
        public void IAppCallBack(IAppCallBack callBack) throws RemoteException {
            APP.iAppCallBack = callBack;
        }

        @Override
        public void initFailed() throws RemoteException {
            APP.initFailed(getApplicationContext());
        }

        @Override
        public void initSucceed() throws RemoteException {
            APP.initSucceed(getApplicationContext());
        }

        @Override
        public void sendRefreshJobsIntent() throws RemoteException {
            APP.sendRefreshJobsIntent();
        }
    }

}