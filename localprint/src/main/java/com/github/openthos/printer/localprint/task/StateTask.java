package com.github.openthos.printer.localprint.task;

import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;

import java.util.List;

/**
 * Created by bboxh on 2016/5/17.
 */
public class StateTask<Progress> extends CommandTask<PrinterId , Progress, PrinterInfo> {

    private PrinterId printerId;

    @Override
    protected String[] setCmd(PrinterId... params) {
        this.printerId = params[0];
        return new String[]{};
    }

    @Override
    protected PrinterInfo handleCommand(List<String> stdOut, List<String> stdErr) {

        // TODO: 2016/5/10 追踪打印机的功能和状态 B6
        //参考
        PrinterCapabilitiesInfo capabilities =
                new PrinterCapabilitiesInfo.Builder(printerId)
                        .setMinMargins(new PrintAttributes.Margins(200, 200, 200, 200))
                        .addMediaSize(PrintAttributes.MediaSize.ISO_A4, true)
                        .addResolution(new PrintAttributes.Resolution("R1", "600x600", 600, 600), true)
                        .setColorModes(PrintAttributes.COLOR_MODE_MONOCHROME,
                                PrintAttributes.COLOR_MODE_MONOCHROME)
                        .build();

        String printerName = "";

        PrinterInfo.Builder builder = new PrinterInfo.Builder(printerId, printerName, PrinterInfo.STATUS_IDLE);


        PrinterInfo printer = builder.setCapabilities(capabilities)
                //.setDescription(item.getManufacturerName())
                .build();

        return printer;
    }

    @Override
    protected String bindTAG() {
        return "StateTask";
    }
}
