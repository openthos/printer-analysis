package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.PrinterOptionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bboxh on 2016/6/1.
 */
public abstract class UpdatePrinterOptionsTask<Progress> extends CommandTask<PrinterOptionItem, Progress, Boolean> {
    @Override
    protected String[] setCmd(PrinterOptionItem... params) {
        PrinterOptionItem item = params[0];

        List<String> list = new ArrayList<String>();
        list.add("sh");
        list.add("proot.sh");
        list.add("lpoptions");
        list.add("-p");
        list.add(getPrinter());
        list.add("-o");
        list.add(item.getMediaSizeName() + "=" + item.getMediaSizeCupsSelectedItem());
        list.add("-o");
        list.add(item.getColorModeName() + "=" + item.getColorModeCupsSelectedItem());

        return list.toArray(new String[0]);
    }

    /**
     * 取得打印机的名称
     * @return
     */
    protected abstract String getPrinter();

    @Override
    protected Boolean handleCommand(List<String> stdOut, List<String> stdErr) {
        boolean flag = true;

        // TODO: 2016/6/1 修改打印机设置 B11
        
        return flag;
    }

    @Override
    protected String bindTAG() {
        return "UpdatePrinterOptionsTask";
    }
}
