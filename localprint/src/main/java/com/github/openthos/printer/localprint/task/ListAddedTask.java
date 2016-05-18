package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.PrinterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取已添加打印
 * Created by bboxh on 2016/5/16.
 */
public class ListAddedTask<Params, Progress> extends CommandTask<Params, Progress, List<PrinterItem>> {
    @Override
    protected String[] setCmd(Params... params) {
        return new String[]{""};
    }

    @Override
    protected List<PrinterItem> handleCommand(List<String> stdOut, List<String> stdErr) {
        // TODO: 2016/5/10 获取已添加打印机 B3
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //示例
        List<PrinterItem> list = new ArrayList<>();
        list.add(new PrinterItem("HP LaserJet Professional P1108", "usb://HP/LaserJet%20Professional%20P1108?serial=000000000Q8D9XVKPR1a","direct"));
        return list;
    }

    @Override
    protected String bindTAG() {
        return "ListAddedTask";
    }
}
