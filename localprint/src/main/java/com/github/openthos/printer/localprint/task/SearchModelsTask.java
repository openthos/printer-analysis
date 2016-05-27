package com.github.openthos.printer.localprint.task;

import com.github.openthos.printer.localprint.model.ModelsItem;
import com.github.openthos.printer.localprint.model.PPDItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查找驱动
 * Created by bboxh on 2016/5/16.
 */
public class SearchModelsTask<Params, Progress> extends CommandTask<Params, Progress, ModelsItem> {
    @Override
    protected String[] setCmd(Params[] params) {
        return new String[]{"sh", "proot.sh", "lpinfo", "-m"};
    }

    @Override
    protected ModelsItem handleCommand(List<String> stdOut, List<String> stdErr) {

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
            }


        }

        List<String> brand = new ArrayList<>();
        Map<String, List<PPDItem>> models = new HashMap<>();
        // TODO: 2016/5/16 查找可添加打印机型号（驱动） B1
        for(String line:stdOut){
            String[] splitLine = line.split(" ");
            String currentPPD = splitLine[0];
            String currentBrand = splitLine[1];
            String currentDriver = "";
            for(int i = 2; i < splitLine.length;i++){
                currentDriver = currentDriver+ " " + splitLine[i];
            }

            List<PPDItem> location;

            if(brand.contains(currentBrand)){
                location = models.get(currentBrand);
            }
            else{
                brand.add(currentBrand);
                location = new ArrayList<>();
                models.put(currentBrand,location);
            }

            location.add(new PPDItem(currentPPD,currentBrand,currentDriver));
        }


//        brand.add("Epson");
//        List<String> epsonBrand = new ArrayList<>();
//        epsonBrand.add("AcuLaser CX17NF Foomatic/foo2hbpl2 (recommended)");
//        epsonBrand.add("AcuLaser M1400 Foomatic/foo2hbpl2 (recommended)");
//        models.put("Epson", epsonBrand);
//        List test = models.get("Epson");

        return new ModelsItem(brand,models);
    }

    @Override
    protected String bindTAG() {
        return "SearchModelsTask";
    }
}
