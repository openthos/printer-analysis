package com.github.openthos.printer.localprint.task;

import java.util.List;

/**
 * Delete a printer B4
 * Created by bboxh on 2016/5/16.
 */
public class DetectNewPrinterTask<Progress> extends CommandTask<String, Progress, Boolean> {

    private String[] mUsbList;

    @Override
    protected String[] setCmd(String[] usbList) {
        mUsbList = usbList;
        return new String[]{"sh", "proot.sh", "lpstat", "-v"};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {

        for (String line : stdErr) {
            if (line.startsWith("WARNING"))
                continue;
            else if (line.contains("Bad file descriptor")) {
                if (startCups()) {
                    runCommandAgain();
                    return false;
                } else {
                    ERROR = "Cups start failed.";
                    return false;
                }
            }
        }

        boolean flag = true;
        for (String item : mUsbList) {
            for (String line : stdOut) {
                if (line.contains("serial=" + item)) {
                    flag = false;
                    break;
                }
            }
        }

        return flag;
    }

    @Override
    protected String bindTAG() {
        return "DeletePrinterTask";
    }
}
