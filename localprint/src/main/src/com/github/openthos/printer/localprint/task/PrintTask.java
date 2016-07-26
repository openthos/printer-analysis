package com.github.openthos.printer.localprint.task;

import android.util.Log;

import com.github.openthos.printer.localprint.APP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Print C1
 * Created by bboxh on 2016/5/16.
 */
public class PrintTask<Progress> extends CommandTask<Map<String, String>, Progress, String> {

    public static final String LP_PRINTER = "printer";
    public static final String LP_FILE = "file";
    public static final String LP_MEDIA = "media";
    public static final String LP_RESOLUTION = "resolution";
    public static final String LP_LANDSCAPE = "landscape";
    public static final String LP_LABEL = "label";
    public static final String LP_COPIES = "cpoies";
    public static final String LP_COLOR = "color";
    public static final String LP_RANGES = "ranges";

    protected int JOB_ID = -1;

    @Override
    protected String[] setCmd(Map<String, String>... params) {
        Map<String, String> map = params[0];
        String printerName = map.get(LP_PRINTER);
        String fileName = map.get(LP_FILE);
        String media = map.get(LP_MEDIA);
        String resolution = map.get(LP_RESOLUTION);
        String landscape = map.get(LP_LANDSCAPE);
        String label = map.get(LP_LABEL);
        String copies = map.get(LP_COPIES);
        String ranges = map.get(LP_RANGES);

        // TODO: 2016/5/16 Printing parameters need to be improved C1

        List<String> list = new ArrayList<String>();
        list.add("sh");
        list.add("proot.sh");
        list.add("lp");

        if (printerName != null) {
            list.add("-d");
            list.add(printerName);
        }
        if (fileName != null) {
            list.add(fileName);
        }
        if (media != null) {
            list.add("-o");
            list.add("media=" + media);
        }
        if (resolution != null) {
            list.add("-o");
            list.add("Resolution=" + resolution);
        }
        if (landscape != null) {
            list.add("-o");
            list.add(landscape);
        }
        if (label != null) {
            list.add("-t");
            list.add(label);
        }
        if (copies != null) {
            list.add("-n");
            list.add(copies);
        }
        if (ranges != null) {
            list.add("-P");
            list.add(ranges);
        }
        list.add("-o");
        list.add("fit-to-page");

        return list.toArray(new String[0]);
    }

    @Override
    protected final String handleCommand(List<String> stdOut, List<String> stdErr) {

        String flag = null;

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

        for (String line : stdOut) {
            if (line.startsWith("request id is")) {

                String[] data = line.split("\\s+");
                flag = data[3];
                Log.d(TAG, "request id is -> " + data[3]);
            } else if (line.contains("scheduler not responding")) {
                if (startCups()) {
                    runCommandAgain();
                    return null;
                } else {
                    ERROR = "Cups start failed.";
                    return null;
                }
            } else if (line.contains("No such file or directory")) {
                //file needing to be printed does not exist

            }
        }

        APP.sendRefreshJobsIntent();

        return flag;
    }

    @Override
    protected String bindTAG() {
        return "PrintTask";
    }
}
