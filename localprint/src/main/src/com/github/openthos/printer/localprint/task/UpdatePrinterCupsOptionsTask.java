package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.PrinterCupsOptionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Update a printer's advanced options in CUPS B5
 * Created by bboxh on 2016/5/31.
 */
public abstract class UpdatePrinterCupsOptionsTask<Progress>
        extends CommandTask<List<PrinterCupsOptionItem>, Progress, Boolean> {
    @Override
    protected String[] setCmd(List<PrinterCupsOptionItem>... params) {

        List<String> list = new ArrayList<String>();
        list.add("sh");
        list.add("proot.sh");
        list.add("lpoptions");
        list.add("-p");
        list.add(getPrinter());

        for (PrinterCupsOptionItem item : params[0]) {
            if (item.getDef() != item.getDef2()) {
                list.add("-o");
                list.add(item.getOption_id() + "=" + item.getOption().get(item.getDef2()));
            }
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
        return null;
    }


    @Override
    protected String bindTAG() {
        return "UpdatePrinterCupsOptionsTask";
    }
}
