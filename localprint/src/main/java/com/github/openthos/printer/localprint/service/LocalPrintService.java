package com.github.openthos.printer.localprint.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.model.JobItem;
import com.github.openthos.printer.localprint.task.JobQueryTask;
import com.github.openthos.printer.localprint.ui.JobManagerActivity;

import java.util.List;

public class LocalPrintService extends Service {

    public static boolean IS_REFRESHING_JOBS = false;     //正在计时刷新任务

    public LocalPrintService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int task = intent.getIntExtra(APP.TASK, APP.TASK_DEFAULT);
        switch (task){
            case APP.TASK_REFRESH_JOBS:
                refreshJobs();
                break;
            default:
                break;
        }

        return START_NOT_STICKY;
    }

    /**
     * 刷新打印任务信息
     */
    private void refreshJobs() {
        final List<JobItem> list = APP.getJobList();
        new JobQueryTask<Void, Void>(list){
            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if(aBoolean){
                    updateJobs(list);
                }else{
                    Toast.makeText(LocalPrintService.this, getString(R.string.query_error) + " -refreshJobs- " + ERROR, Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
    }

    /**
     * 通知其他模块更新打印任务信息
     * @param list
     */
    private void updateJobs(List<JobItem> list) {

        APP.IS_JOB_WAITING_FOR_PRINTER = false;         //重置存在 等待打印打印机可用的任务 的标记

        for(JobItem item: list){
            if(item.getStatus() == JobItem.STATUS_PRINTING){

                if(IS_REFRESHING_JOBS){         //代表已经有定时刷新任务
                    break;
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshJobs();
                        IS_REFRESHING_JOBS = false;
                    }
                }, APP.JOB_REFRESH_INTERVAL);       //有打印任务，则一段时间后刷新任务信息
                IS_REFRESHING_JOBS = true;
                break;
            }else if(item.getStatus() == JobItem.STATUS_WAITING_FOR_PRINTER){
                APP.IS_JOB_WAITING_FOR_PRINTER = true;          //有任务在等待打印机
                continue;
            }
        }

        sendBroadcast(new Intent(APP.BROADCAST_REFRESH_JOBS));      //通知其他模块更新打印任务列表

        if(!APP.IS_NOTIFICATION) return;        //是否开启通知栏

        // TODO: 2016/6/7 显示通知栏信息

        showNotification();

    }

    /**
     *显示通知栏
     */
    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification_jobs);

        Intent intent = new Intent(this, JobManagerActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, PendingIntent.FLAG_UPDATE_CURRENT, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.button, pIntent);

        //添加打印任务项

        RemoteViews item1 = new RemoteViews(getPackageName(), R.layout.notification_item_jobs);
        item1.setTextViewText(R.id.textview_name, "HP P1108");
        views.addView(R.id.linearlayout_items, item1);

        RemoteViews item2 = new RemoteViews(getPackageName(), R.layout.notification_item_jobs);
        item2.setTextViewText(R.id.textview_name, "docu.pdf");
        item2.setTextViewText(R.id.textview_status, "printing");
        views.addView(R.id.linearlayout_items, item2);

        //添加完毕

        builder.setContent(views);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(getString(R.string.print_info));
        builder.setOngoing(true);

        android.support.v4.app.NotificationCompat.InboxStyle inboxBuilder = new NotificationCompat.InboxStyle();
        inboxBuilder.addLine("Test1");

        builder.setStyle(inboxBuilder);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(APP.NOTIFY_JOBS_ID, builder.build());


        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        Notification noti = new NotificationCompat.Builder(
                this)
                .setNumber(13)
                .setContentIntent(pendingIntent)
                .setStyle(
                        new NotificationCompat.InboxStyle()
                                .addLine(
                                        "M.Twain (Google+) Haiku is more than a cert...")
                                .addLine("M.Twain Reminder")
                                .addLine("M.Twain Lunch?")
                                .addLine("M.Twain Revised Specs")
                                .addLine("M.Twain ")
                                .addLine(
                                        "Google Play Celebrate 25 billion apps with Goo..")
                                .addLine(
                                        "Stack Exchange StackOverflow weekly Newsl...")
                                .setBigContentTitle("6 new message")
                                .setSummaryText("mtwain@android.com"))
                .build();
        NotificationManager m1NotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        m1NotificationManager.notify(0, noti);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
