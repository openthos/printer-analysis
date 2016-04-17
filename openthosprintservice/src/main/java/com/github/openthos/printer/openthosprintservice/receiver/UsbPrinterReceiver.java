package com.github.openthos.printer.openthosprintservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;

import com.github.openthos.printer.openthosprintservice.APP;
import com.github.openthos.printer.openthosprintservice.service.OpenthosPrintService;
import com.github.openthos.printer.openthosprintservice.ui.ManagementActivity;
import com.github.openthos.printer.openthosprintservice.util.LogUtils;

public class UsbPrinterReceiver extends BroadcastReceiver {
    private static final String TAG = "UsbPrinterReceiver";

    public UsbPrinterReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d(TAG, "intent.getAction() ->" + action);

        if( action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED) || action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)){

            if(APP.MANAGEMENT_ACTIVITY_ON_TOP){
                Intent intent1 = new Intent(APP.getApplicatioContext(), ManagementActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra(APP.TASK, APP.TASK_ADD_NEW_PRINTER);
                APP.getApplicatioContext().startActivity(intent1);
            }else{
                Intent new_intent = new Intent(APP.getApplicatioContext(), OpenthosPrintService.class);
                new_intent.putExtra(APP.TASK, APP.TASK_DETECT_USB_PRINTER);
                APP.getApplicatioContext().startService(new_intent);
            }
        }

    }

}
