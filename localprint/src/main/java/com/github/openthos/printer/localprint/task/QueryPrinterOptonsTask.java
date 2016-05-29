package com.github.openthos.printer.localprint.task;

import android.print.PrinterInfo;

import com.github.openthos.printer.localprint.model.PrinterOptionItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bboxh on 2016/5/27.
 */
public class QueryPrinterOptonsTask<Progress> extends CommandTask<String, Progress, List<PrinterOptionItem>> {
    @Override
    protected String[] setCmd(String... params) {
        if(params == null){
            return null;
        }
        return new String[]{"sh", "proot.sh", "lpoptions", "-p", params[0], "-l"};
    }

    @Override
    protected List<PrinterOptionItem> handleCommand(List<String> stdOut, List<String> stdErr) {

        for(String line: stdErr){

            if( line.startsWith("WARNING") )
                continue;
            else if (line.contains("Bad file descriptor")){
                if( startCups() ){
                    runCommandAgain();      //再次运行命令
                    return null;
                }else{
                    ERROR = "Cups start failed.";
                    return null;
                }
            }else if (line.contains("The printer or class does not exist")){
                ERROR = "The printer or class does not exist.";
                return null;
            }

        }

        // TODO: 2016/5/27 查询打印机设置  B9
        List<PrinterOptionItem> options = new ArrayList<>();
        //模拟数据
        PrinterOptionItem item1 = new PrinterOptionItem();
        item1.setName("Printing Quality");
        item1.setOption_id("Quality");
        item1.add("draft", true);
        item1.add("normal", false);
        options.add(item1);
        PrinterOptionItem item2 = new PrinterOptionItem();
        item2.setOption_id("ColorMode");
        item2.setName("Color Mode");
        item2.add("ICM", false);
        item2.add("Monochrome", true);
        options.add(item2);

        return options;
    }

    @Override
    protected String bindTAG() {
        return "QueryPrinterOptonsTask";
    }
}
