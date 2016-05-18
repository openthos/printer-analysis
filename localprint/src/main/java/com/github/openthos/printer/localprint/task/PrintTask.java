package com.github.openthos.printer.localprint.task;

import java.util.List;
import java.util.Map;

/**
 * Created by bboxh on 2016/5/16.
 */
public class PrintTask<Progress> extends CommandTask<Map<String, String>, Progress, Integer> {
    public static final String LP_PRINTER = "printer";
    public static final String LP_FILE = "file";
    public static final String LP_MEDIA = "media";
    public static final String LP_RESOLUTION = "resolution";
    public static final String LP_LANDSCAPE = "landscape";
    public static final String LP_LABEL = "label";
    public static final String LP_COPIES = "cpoies";

    protected int JOB_ID = -1;        //填充任务编号

    @Override
    protected String[] setCmd(Map<String, String>... params) {
        Map<String, String> map = params[0];
        map.get(LP_PRINTER);
        map.get(LP_FILE);
        map.get(LP_MEDIA);
        map.get(LP_RESOLUTION);
        map.get(LP_LANDSCAPE);
        map.get(LP_LABEL);

        // TODO: 2016/5/16 打印 C1

        return new String[]{};
    }

    @Override
    protected final Integer handleCommand(List<String> stdOut, List<String> stdErr) {
        return -1;
    }

    @Override
    protected String bindTAG() {
        return "PrintTask";
    }
}
