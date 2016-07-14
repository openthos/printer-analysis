package com.github.openthos.printer.localprint.service;

import android.print.PrinterId;
import android.print.PrinterInfo;
import android.printservice.PrinterDiscoverySession;
import android.widget.Toast;

import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.model.PrinterItem;
import com.github.openthos.printer.localprint.task.ListAddedTask;
import com.github.openthos.printer.localprint.task.StateTask;
import com.github.openthos.printer.localprint.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * System will call PrinterDiscoverySession.
 * Created by bboxh on 2016/4/12.
 */
public class PrintDiscoverySession extends PrinterDiscoverySession {

    private static final String TAG = "PrintDiscoverySession";

    private final OpenthosPrintService mOpenthosPrintService;

    public PrintDiscoverySession(OpenthosPrintService openthosPrintService) {
        mOpenthosPrintService = openthosPrintService;
    }

    /**
     * begin to search printers.
     *
     * @param priorityList List<PrinterId>
     */
    @Override
    public void onStartPrinterDiscovery(final List<PrinterId> priorityList) {
        LogUtils.d(TAG, "onStartPrinterDiscovery()");
        final List<PrinterInfo> printers = this.getPrinters();

        ListAddedTask<Void, Void> task = new ListAddedTask<Void, Void>() {
            @Override
            protected void onPostExecute(List<PrinterItem> list) {
                List<PrinterId> old_list = new ArrayList<>();
                old_list.addAll(priorityList);

                if (list != null) {
                    for (PrinterItem printerItem : list) {

                        PrinterId id = mOpenthosPrintService
                                .generatePrinterId(String.valueOf(printerItem.getNickName()));

                        if (priorityList.contains(id)) {
                            old_list.remove(id);
                            continue;
                        }

                        PrinterInfo.Builder builder = new PrinterInfo.Builder(id
                                , printerItem.getNickName()
                                , PrinterInfo.STATUS_IDLE);
                        PrinterInfo myprinter = builder.build();
                        printers.add(myprinter);
                    }
                    addPrinters(printers);
                } else {
                    Toast.makeText(mOpenthosPrintService,
                                   mOpenthosPrintService.getResources()
                                       .getString(R.string.query_error) + " " + ERROR,
                                   Toast.LENGTH_SHORT).show();
                }

                removePrinters(old_list);
            }
        };

        task.start();

    }

    /**
     * Stop searching.
     * In CUPS, whether the printer is connected or not, the printer's status is idle.
     * So do not need to scan the whole time, Once is enough and do not need the stop.
     */
    @Override
    public void onStopPrinterDiscovery() {
    }

    /**
     * ？Unknown.
     *
     * @param printerIds List<PrinterId>
     */
    @Override
    public void onValidatePrinters(List<PrinterId> printerIds) {
        // TODO: 2016/5/10  onValidatePrinters ?
    }

    /**
     * Called by the system when a user select a printer to update the printer info.
     *
     * @param printerId PrinterId
     */
    @Override
    public void onStartPrinterStateTracking(final PrinterId printerId) {
        LogUtils.d(TAG, "onStartPrinterStateTracking()");

        StateTask<Void> task = new StateTask<Void>() {
            @Override
            protected void onPostExecute(PrinterInfo printerInfo) {

                if (printerInfo == null) {
                    Toast.makeText(mOpenthosPrintService,
                                   mOpenthosPrintService.getResources()
                                       .getString(R.string.query_error) + " " + ERROR,
                                   Toast.LENGTH_LONG).show();
                    PrinterInfo.Builder builder =
                            new PrinterInfo.Builder(printerId, printerId.getLocalId()
                                    , PrinterInfo.STATUS_UNAVAILABLE);
                    printerInfo = builder.build();
                }

                List<PrinterInfo> printers = new ArrayList<PrinterInfo>();
                printers.add(printerInfo);
                addPrinters(printers);
            }
        };
        task.start(printerId);

    }

    /**
     * Called by the system When a user finish the event of selecting a printer.
     *
     * @param printerId PrinterId
     */
    @Override
    public void onStopPrinterStateTracking(PrinterId printerId) {
        // For CUPS, we only track once, so we do not need the stop.
    }

    @Override
    public void onDestroy() {

    }

}
