package com.github.openthos.printer.openthosprintservice.task;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.WindowManager;

import com.github.openthos.printer.openthosprintservice.APP;
import com.github.openthos.printer.openthosprintservice.R;
import com.github.openthos.printer.openthosprintservice.model.DriveItemHelper;
import com.github.openthos.printer.openthosprintservice.model.PrinterItem;
import com.github.openthos.printer.openthosprintservice.model.PrinterItemHelper;
import com.github.openthos.printer.openthosprintservice.service.OpenthosPrintService;
import com.github.openthos.printer.openthosprintservice.ui.ManagementActivity;
import com.github.openthos.printer.openthosprintservice.util.DialogUtils;
import com.github.openthos.printer.openthosprintservice.util.LogUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 检测USB打印插入和拔出
 * Created by bboxh on 2016/4/14.
 */
public class DetectPrinterTask extends BaseTask {

    private Context context;
    private UsbManager usbmanager;
    //private List<UsbDevice> usbItem;

    public DetectPrinterTask(String TAG) {
        super(TAG + "-> DetectPrinterTask");
        context = APP.getApplicatioContext();
    }



    @Override
    public void run() {


         usbmanager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = usbmanager.getDeviceList();

        Log.d(TAG, "get device list  = " + deviceList.size());
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        PrinterItemHelper helper_printer = new PrinterItemHelper();

        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            LogUtils.d(TAG, "detect device -> " + device.toString());

            for(int i=0; i < device.getInterfaceCount(); i++ ){
                // InterfaceClass 7 代表打印机
                if(device.getInterface(i).getInterfaceClass() == 7){
                    PrinterItem item = new PrinterItem();
                    item.setVendorId(device.getVendorId());
                    item.setProductId(device.getProductId());
                    item.setSerialNumber(device.getSerialNumber());
                    if(!helper_printer.isExist(item)){
                        Intent new_intent = new Intent(APP.getApplicatioContext(), OpenthosPrintService.class);
                        new_intent.putExtra(APP.TASK, APP.TASK_ADD_NEW_PRINTER);
                        APP.getApplicatioContext().startService(new_intent);
                        helper_printer.close();
                        return;
                    }
                }
            }

        }

        helper_printer.close();

    }

}
