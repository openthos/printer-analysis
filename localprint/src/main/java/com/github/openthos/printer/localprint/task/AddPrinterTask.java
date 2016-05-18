package com.github.openthos.printer.localprint.task;

import java.util.List;
import java.util.Map;

/**
 * 添加打印机
 * Created by bboxh on 2016/5/16.
 */
public class AddPrinterTask<Progress> extends CommandTask<Map<String,String>, Progress, Boolean> {
    @Override
    protected String[] setCmd(Map<String, String>[] params) {
        return new String[]{};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {
        boolean flag = false;

        // TODO: 2016/5/10 添加打印机 B2
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return flag;
    }

    @Override
    protected String bindTAG() {
        return "AddPrinterTask";
    }
}
