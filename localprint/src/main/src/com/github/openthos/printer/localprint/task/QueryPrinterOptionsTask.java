package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.PrinterOptionItem;

import java.util.List;

/**
 * Query a printer's options for the need of Android printing B10
 * Created by bboxh on 2016/5/31.
 */
public class QueryPrinterOptionsTask<Progress>
                 extends CommandTask<String, Progress, PrinterOptionItem> {
    @Override
    protected String[] setCmd(String... params) {

        String printerName = params[0];

        return new String[]{"sh", "proot.sh", "sh", "printerquery.sh", params[0]};
    }

    @Override
    protected PrinterOptionItem handleCommand(List<String> stdOut, List<String> stdErr) {

        PrinterOptionItem item = new PrinterOptionItem();
        for (String line : stdOut) {
            String[] preSplit = line.split(" ");
            boolean isPrinterOptionsLine = false;
            for (int x = 0; x < preSplit.length; x++) {
                if (preSplit[x].startsWith("device-uri")) {
                    isPrinterOptionsLine = true;
                    break;
                }
            }
            if (isPrinterOptionsLine) {
                for (int i = 0; i < preSplit.length; i++) {
                    if (preSplit[i].startsWith("printer-is-shared")) {
                        String[] secondSplit = preSplit[i].split("=");
                        if (secondSplit[1].equals("true")) {
                            item.setmSharePrinter(true);
                        } else if (secondSplit[1].equals("false")) {
                            item.setmSharePrinter(false);
                        }
                        break;
                    }
                }
            } else {
                String[] firstSplit = line.split("/");

                if (firstSplit[0].equals("ColorModel")
                        || firstSplit[0].equals("Color") || firstSplit[0].equals("ColorMode")) {
                    item.setColorModeName(firstSplit[0]);

                    String[] secondSplit = firstSplit[1].split(": ");
                    String[] thirdSplit = secondSplit[1].split(" ");
                    for (int i = 0; i < thirdSplit.length; i++) {
                        if (thirdSplit[i].startsWith("*")) {
                            thirdSplit[i] = thirdSplit[i].replace("*", "");
                            item.addColorModeItem(thirdSplit[i], true);
                        } else
                            item.addColorModeItem(thirdSplit[i], false);
                    }
                }

                if (firstSplit[0].equals("PageSize")) {
                    item.setMediaSizeName(firstSplit[0]);

                    String[] secondSplit = firstSplit[1].split(": ");
                    String[] thirdSplit = secondSplit[1].split(" ");
                    for (int i = 0; i < thirdSplit.length; i++) {
                        if (thirdSplit[i].startsWith("*")) {
                            thirdSplit[i] = thirdSplit[i].replace("*", "");
                            item.addMediaSizeItem(thirdSplit[i], true);
                        } else {
                            item.addMediaSizeItem(thirdSplit[i], false);
                        }
                    }
                }
            }
        }

        //simulated data
        /*
        item.setColorModeName("ColorMode");
        item.setMediaSizeName("PageSize");
        item.addColorModeItem("Color", true);
        item.addColorModeItem("Grayscale", true);
        item.addMediaSizeItem("Letter", false);
        item.addMediaSizeItem("A4", true);
        */
        return item;
    }

    @Override
    protected String bindTAG() {
        return "QueryPrinterOptionsTask";
    }
}
