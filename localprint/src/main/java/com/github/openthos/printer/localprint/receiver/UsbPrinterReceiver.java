package com.github.openthos.printer.localprint.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Handler;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.service.LocalPrintService;
import com.github.openthos.printer.localprint.service.OpenthosPrintService;
import com.github.openthos.printer.localprint.ui.ManagementActivity;
import com.github.openthos.printer.localprint.util.LogUtils;

/**
 * 接收USB插入和拔出的Braodcaster
 */
public class UsbPrinterReceiver extends BroadcastReceiver {
    private static final String TAG = "UsbPrinterReceiver";

    public UsbPrinterReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d(TAG, "intent.getAction() ->" + action);

        //监听USB设备的插入和拔出
        if( action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED) || action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)){

            //管理界面在最前面则直接通知更新界面，否则给OpenthosPrintService处理
            if(APP.IS_MANAGEMENT_ACTIVITY_ON_TOP){
                Intent new_intent = new Intent(APP.getApplicatioContext(), ManagementActivity.class);
                new_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                new_intent.putExtra(APP.TASK, APP.TASK_ADD_NEW_PRINTER);
                APP.getApplicatioContext().startActivity(new_intent);
            }else{
                Intent new_intent = new Intent(APP.getApplicatioContext(), OpenthosPrintService.class);
                new_intent.putExtra(APP.TASK, APP.TASK_DETECT_USB_PRINTER);
                APP.getApplicatioContext().startService(new_intent);
            }

            //通知更新打印任务信息
            if(APP.IS_JOB_WAITING_FOR_PRINTER){

                //延迟一会看打印任务有没有更新
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent new_intent = new Intent(APP.getApplicatioContext(), LocalPrintService.class);
                        new_intent.putExtra(APP.TASK, APP.TASK_REFRESH_JOBS);
                        APP.getApplicatioContext().startService(new_intent);
                    }
                }, APP.JOB_REFRESH_WAITING_PRINTER_INTERVAL);
            }

        }

    }

}
