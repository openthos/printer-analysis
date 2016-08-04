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
 * Receive the event of USB devices' insert and pull out.
 */
public class UsbPrinterReceiver extends BroadcastReceiver {
    private static final String TAG = "UsbPrinterReceiver";

    public UsbPrinterReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d(TAG, "intent.getAction() ->" + action);

        //Monitor the USB device insert and pull out.
        if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)
                || action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {

            //Update UI when management activity is on the top,or handled by OpenthosPrintService
            if (APP.IS_MANAGEMENT_ACTIVITY_ON_TOP) {
                Intent newIntent = new Intent(APP.getApplicatioContext(),
                                               ManagementActivity.class);
                newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                newIntent.putExtra(APP.TASK, APP.TASK_ADD_NEW_PRINTER);
                APP.getApplicatioContext().startActivity(newIntent);
            } else {
                Intent newIntent
                        = new Intent(APP.getApplicatioContext(), LocalPrintService.class);
                newIntent.putExtra(APP.TASK, APP.TASK_DETECT_USB_PRINTER);
                APP.getApplicatioContext().startService(newIntent);
            }

            //Send a message when there has jobs waiting for printer.
            if (APP.IS_JOB_WAITING_FOR_PRINTER) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        APP.sendRefreshJobsIntent();
                    }
                }, APP.JOB_REFRESH_WAITING_PRINTER_INTERVAL);
            }

        } else if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent newIntent = new Intent(APP.getApplicatioContext(), LocalPrintService.class);
            newIntent.putExtra(APP.TASK, APP.TASK_INIT);
            APP.getApplicatioContext().startService(newIntent);
        }

    }

}
