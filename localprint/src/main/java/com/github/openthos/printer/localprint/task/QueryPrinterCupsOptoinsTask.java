package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.PrinterCupsOptionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Query a printer's advanced options in CUPS B9
 * Created by bboxh on 2016/5/27.
 */
public class QueryPrinterCupsOptoinsTask<Progress>
        extends CommandTask<String, Progress, List<PrinterCupsOptionItem>> {
    @Override
    protected String[] setCmd(String... params) {
        if (params == null) {
            return null;
        }
        return new String[]{"sh", "proot.sh", "lpoptions", "-p", params[0], "-l"};
    }

    @Override
    protected List<PrinterCupsOptionItem> handleCommand(List<String> stdOut, List<String> stdErr) {

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
            } else if (line.contains("The printer or class does not exist")) {
                ERROR = "The printer or class does not exist.";
                return null;
            }

        }

        List<PrinterCupsOptionItem> options = new ArrayList<>();
        for (String line : stdOut) {
            String[] firstSplit = line.split("/");
            PrinterCupsOptionItem item1 = new PrinterCupsOptionItem();
            item1.setOption_id(firstSplit[0]);

            String[] secondSplit = firstSplit[1].split(": ");
            item1.setName(secondSplit[0]);

            String[] thirdSplit = secondSplit[1].split("\\s+");
            for (int i = 0; i < thirdSplit.length; i++) {
                if (thirdSplit[i].startsWith("*")) {
                    thirdSplit[i] = thirdSplit[i].replace("*", "");
                    item1.add(thirdSplit[i], true);
                } else
                    item1.add(thirdSplit[i], false);
            }
            options.add(item1);
        }

        //simulated data
        /*List<PrinterCupsOptionItem> options = new ArrayList<>();
        PrinterCupsOptionItem item1 = new PrinterCupsOptionItem();
        item1.setName("Printing Quality");
        item1.setOption_id("Quality");
        item1.add("draft", true);
        item1.add("normal", false);
        options.add(item1);
        PrinterCupsOptionItem item2 = new PrinterCupsOptionItem();
        item2.setOption_id("ColorMode");
        item2.setName("Color Mode");
        item2.add("ICM", false);
        item2.add("Monochrome", true);
        options.add(item2);*/

        return options;
    }

    @Override
    protected String bindTAG() {
        return "QueryPrinterCupsOptoinsTask";
    }
}
