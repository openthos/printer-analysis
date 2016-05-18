package com.github.openthos.printer.localprint.task;

import java.util.List;

/**
 * Created by bboxh on 2016/5/16.
 */
public class DeletePrinterTask<Progress> extends CommandTask<String, Progress, Boolean> {


    @Override
    protected String[] setCmd(String[] name) {
        String printerName = name[0];
        return new String[]{};
    }

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {
        // TODO: 2016/5/16 删除打印机 B4
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected String bindTAG() {
        return "DeletePrinterTask";
    }
}
