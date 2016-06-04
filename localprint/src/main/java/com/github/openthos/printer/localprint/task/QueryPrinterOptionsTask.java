package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.PrinterOptionItem;

import java.util.List;

/**
 * Created by bboxh on 2016/5/31.
 */
public class QueryPrinterOptionsTask<Progress> extends  CommandTask<String, Progress, PrinterOptionItem>  {
    @Override
    protected String[] setCmd(String... params) {

        String printerName = params[0];

        return new String[]{};
    }

    @Override
    protected PrinterOptionItem handleCommand(List<String> stdOut, List<String> stdErr) {

        // TODO: 2016/6/1 查询打印机设置 B10
        
        PrinterOptionItem item = new PrinterOptionItem();
        //模拟数据
        item.setColorModeName("ColorMode");
        item.setMediaSizeName("PageSize");
        item.addColorModeItem("Color", true);
        item.addColorModeItem("Grayscale", true);
        item.addMediaSizeItem("Letter", false);
        item.addMediaSizeItem("A4", true);

        return item;
    }

    @Override
    protected String bindTAG() {
        return "QueryPrinterOptionsTask";
    }
}
