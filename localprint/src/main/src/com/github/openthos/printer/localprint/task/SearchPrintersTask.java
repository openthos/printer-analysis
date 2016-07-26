package com.github.openthos.printer.localprint.task;

import android.os.AsyncTask;

import com.github.openthos.printer.localprint.model.PrinterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Search availabe printers which can be added in CUPS B8
 * Created by bboxh on 2016/5/14.
 */
public class SearchPrintersTask<Params, Progress> extends CommandTask<Params, Progress, List<PrinterItem>> {

    @Override
    protected String[] setCmd(Params[] params) {
        return new String[]{"sh", "proot.sh", "lpinfo", "-v", "-l"};
    }

    /**
     * called in doInBackground() method
     *
     * @param stdOut standard output
     * @param stdErr error output
     * @return null represent error occured
     */
    @Override
    protected List<PrinterItem> handleCommand(List<String> stdOut, List<String> stdErr) {

        for (String line : stdErr) {

            if (line.startsWith("WARNING"))
                continue;
            else if (line.contains("Bad file descriptor")) {
                if (startCups()) {
                    runCommandAgain();
                    return null;
                } else {
                    ERROR = "Cups start failed.";
                    return null;
                }
            }


        }

        List<PrinterItem> list = new ArrayList<>();

        for (String line : stdOut) {
            if (line.startsWith("direct usb")) {
                String[] splitLine = line.split(" ");
                //String deviceURL = splitLine[1];
                String deviceName = splitLine[1]
                        .substring(splitLine[1].indexOf("//") + 2, splitLine[1].indexOf("?"));
                deviceName = deviceName.replace("/", "_");
                deviceName = deviceName.replace("%20", "_");
                list.add(new PrinterItem(deviceName, splitLine[1], splitLine[0]));
            }
        }

        //example
        //list.add(new PrinterItem("HP LaserJet Professional P1108"
        // , "usb://HP/LaserJet%20Professional%20P1108?serial=000000000Q8D9XVKPR1a","direct"));
        return list;
    }

    @Override
    protected String bindTAG() {
        return "SearchPrintersTask";
    }

    @Override
    public AsyncTask<Params, Progress, List<PrinterItem>> start(Params... params) {
        return super.start(params);
    }
}
