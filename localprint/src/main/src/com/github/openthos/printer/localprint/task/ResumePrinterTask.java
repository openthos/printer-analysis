package com.github.openthos.printer.localprint.task;

import java.util.List;

/**
 * C12
 * Created by bboxh on 2016/8/5.
 */
public class ResumePrinterTask extends CommandTask<String, Void, Void> {
    @Override
    protected String[] setCmd(String... params) {
        return new String[]{"sh", "proot.sh", "cupsenable", params[0]};
    }

    @Override
    protected Void handleCommand(List<String> stdOut, List<String> stdErr) {

        for (String line : stdErr) {
            if (line.startsWith("WARNING")) {
                continue;
            } else if (line.contains("Bad file descriptor")
                    || line.contains("server-error-service-unavailable")) {
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
        return "ResumePrinterTask";
    }
}
