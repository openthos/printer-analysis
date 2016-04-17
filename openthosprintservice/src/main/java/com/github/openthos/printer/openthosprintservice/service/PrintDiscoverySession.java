package com.github.openthos.printer.openthosprintservice.service;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrinterDiscoverySession;

import com.github.openthos.printer.openthosprintservice.model.PrinterItem;
import com.github.openthos.printer.openthosprintservice.model.PrinterItemHelper;
import com.github.openthos.printer.openthosprintservice.util.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bboxh on 2016/4/12.
 */
public class PrintDiscoverySession extends PrinterDiscoverySession {

    private static final String TAG = "PrintDiscoverySession";
    private final OpenthosPrintService openthosPrintService;

    public PrintDiscoverySession(OpenthosPrintService openthosPrintService) {
        this.openthosPrintService = openthosPrintService;
    }

    /**
     *开始寻找
     * @param priorityList
     */
    @Override
    public void onStartPrinterDiscovery(List<PrinterId> priorityList) {
        LogUtils.d(TAG, "onStartPrinterDiscovery()");
        List<PrinterInfo> printers = this.getPrinters();

        PrinterItemHelper helper = new PrinterItemHelper();
        List<PrinterItem> list = helper.queryAll();
        helper.close();

        if (list != null) {
            for (PrinterItem printerItem : list) {

                PrinterInfo.Builder builder =
                        new PrinterInfo.Builder(openthosPrintService.generatePrinterId(String.valueOf(printerItem.getPrinterId()))
                                , printerItem.getNickName(), PrinterInfo.STATUS_UNAVAILABLE);


                getStatus(printerItem, builder);
                PrinterInfo myprinter = builder.build();
                printers.add(myprinter);
                addPrinters(printers);
            }


        }
    }

    /**
     * device.getVendorId() == printerItem.getVendorId() && device.getProductId() == printerItem.getProductId()
     * @param printerItem
     * @param builder
     */
    private void getStatus(PrinterItem printerItem, PrinterInfo.Builder builder) {
        UsbManager manager = (UsbManager) openthosPrintService.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if(device.getVendorId() == printerItem.getVendorId() && device.getProductId() == printerItem.getProductId()
                    && device.getSerialNumber().equals(printerItem.getSerialNumber())){
                builder.setStatus(PrinterInfo.STATUS_IDLE);
                LogUtils.d(TAG, printerItem.getNickName() + " STATUS_IDLE" + " device -> " + device.getDeviceName());
            }
        }
    }

    /**
     * 停止寻找
     */
    @Override
    public void onStopPrinterDiscovery() {

    }

    /**
     * ？
     * @param printerIds
     */
    @Override
    public void onValidatePrinters(List<PrinterId> printerIds) {

    }

    /**
     * 选择打印机时调用该方法更新打印机的状态，功能
     * @param printerId
     */
    @Override
    public void onStartPrinterStateTracking(PrinterId printerId) {
        LogUtils.d(TAG, "onStartPrinterStateTracking()");
        PrinterInfo printer = findPrinterInfo(printerId);
        if (printer != null) {
            PrinterCapabilitiesInfo capabilities =
                    new PrinterCapabilitiesInfo.Builder(printerId)
                            .setMinMargins(new PrintAttributes.Margins(200, 200, 200, 200))
                            .addMediaSize(PrintAttributes.MediaSize.ISO_A4, true)
                            .addResolution(new PrintAttributes.Resolution("R1", "600x600", 600, 600), true)
                            .setColorModes(PrintAttributes.COLOR_MODE_MONOCHROME,
                                    PrintAttributes.COLOR_MODE_MONOCHROME)
                            .build();


            PrinterItemHelper helper = new PrinterItemHelper();
            PrinterItem item = helper.query(Integer.parseInt(printerId.getLocalId()));

            PrinterInfo.Builder builder = new PrinterInfo.Builder(printer);
            getStatus(item, builder);

            printer = builder.setCapabilities(capabilities)
                    //.setDescription(item.getManufacturerName())
                    .build();
            List<PrinterInfo> printers = new ArrayList<PrinterInfo>();

            printers.add(printer);
            addPrinters(printers);
        }
    }

    /**
     * 选择结束，停止更新
     * @param printerId
     */
    @Override
    public void onStopPrinterStateTracking(PrinterId printerId) {

    }

    @Override
    public void onDestroy() {

    }

    private PrinterInfo findPrinterInfo(PrinterId printerId) {
        List<PrinterInfo> printers = getPrinters();
        final int printerCount = getPrinters().size();
        for (int i = 0; i < printerCount; i++) {
            PrinterInfo printer = printers.get(i);
            if (printer.getId().equals(printerId)) {
                return printer;
            }
        }
        return null;
    }


}
