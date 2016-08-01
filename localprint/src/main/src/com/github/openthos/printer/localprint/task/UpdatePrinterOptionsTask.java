package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.PrinterOptionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Update a printer's options for the need of Android printing B11
 * Created by bboxh on 2016/6/1.
 */
public abstract class UpdatePrinterOptionsTask<Progress>
        extends CommandTask<PrinterOptionItem, Progress, Boolean> {
    @Override
    protected String[] setCmd(PrinterOptionItem... params) {
        PrinterOptionItem item = params[0];

        List<String> list = new ArrayList<String>();
        list.add("sh");
        list.add("proot.sh");
        list.add("lpoptions");
        list.add("-p");
        list.add(getPrinter());
        list.add("-o");
        list.add(item.getMediaSizeName() + "=" + item.getMediaSizeCupsSelectedItem());
        if (item.getColorModeName() != null) {
            list.add("-o");
            list.add(item.getColorModeName() + "=" + item.getColorModeCupsSelectedItem());
        }

        return list.toArray(new String[0]);
    }

    /**
     * Need to be overwritten.
     *
     * @return the printer name
     */
    protected abstract String getPrinter();

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {
        boolean flag = false;

        for (String line : stdErr) {

            if (line.startsWith("WARNING")) {
                continue;
            } else if (line.contains("Bad file descriptor")) {
                if (startCups()) {
                    runCommandAgain();
                    return null;
                } else {
                    ERROR = "Cups start failed.";
                    return null;
                }
            }
        }

        if (stdOut.isEmpty()) {
            flag = true;
        }
        return flag;
    }

    @Override
    protected String bindTAG() {
        return "UpdatePrinterOptionsTask";
    }
}
