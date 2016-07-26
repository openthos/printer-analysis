package com.github.openthos.printer.localprint.task;

import java.util.List;
import java.util.Map;

/**
 * Add a printer B2
 * Created by bboxh on 2016/5/16.
 */
public class AddPrinterTask<Progress> extends CommandTask<Map<String, String>, Progress, Boolean> {
    @Override
    protected String[] setCmd(Map<String, String>[] params) {
        String name = params[0].get("name");
        String url = params[0].get("url");
        String model = params[0].get("model");
        String isShare = params[0].get("isShare");
        String share = "";
        if (isShare.equals("false")) {
            share = "-o printer-is-shared=false";
        } else if (isShare.equals("true")) {
            share = "-o printer-is-shared=true";
        }
        return new String[]{"sh", "proot.sh", "lpadmin", "-p", name,
                            "-v", url, "-m", model, share, "-E"};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {
        boolean flag = true;

        for (String line : stdErr) {
            if (line.contains("Unable to connect to server")) {
                if (startCups()) {
                    runCommandAgain();
                    flag = false;
                    break;
                } else {
                    ERROR = "Cups start failed.";
                    flag = false;
                    break;
                }
            } else if (line.contains("Unable to copy PPD file")) {
                ERROR = "Unable to copy PPD file";
                flag = false;
                break;
            }
        }


        return flag;
    }

    @Override
    protected String bindTAG() {
        return "AddPrinterTask";
    }
}
