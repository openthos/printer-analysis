package com.github.openthos.printer.localprint.task;

import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;

import java.util.List;

/**
 * 追踪打印机的功能和状态 B6
 * Created by bboxh on 2016/5/17.
 */
public class StateTask<Progress> extends CommandTask<PrinterId , Progress, PrinterInfo> {

    private PrinterId printerId;

    @Override
    protected String[] setCmd(PrinterId... params) {
        this.printerId = params[0];
        String name = printerId.getLocalId();
        return new String[]{"sh","proot.sh","lpoptions","-p",name,"-l"};
    }

    @Override
    protected PrinterInfo handleCommand(List<String> stdOut, List<String> stdErr) {

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
            }else if(line.contains("The printer or class does not exist")){
                ERROR = "The printer or class does not exist";
                return null;
            }

        }

        PrinterCapabilitiesInfo.Builder state = new PrinterCapabilitiesInfo.Builder(printerId);

        state.setMinMargins(new PrintAttributes.Margins(200, 200, 200, 200));   //cups中暂未找到对应配置
        for(String line:stdOut) {
            if (line.contains("PageSize")){
                String[] splitLine = line.split(" ");
                for (int i = 2; i < splitLine.length; i++) {
                    if(splitLine[i].equals("Custom.WIDTHxHEIGHT"))
                        continue;
                    boolean flag = false;
                    if(splitLine[i].startsWith("*")) {
                        flag = true;
                        splitLine[i] = splitLine[i].replace("*","");
                    }
                    if(splitLine[i].equals("Letter"))
                        state.addMediaSize(PrintAttributes.MediaSize.NA_LETTER,flag);
                    if(splitLine[i].equals("A4"))
                        state.addMediaSize(PrintAttributes.MediaSize.ISO_A4,flag);
                    if(splitLine[i].equals("A5"))
                        state.addMediaSize(PrintAttributes.MediaSize.ISO_A5,flag);
                    if(splitLine[i].equals("A6"))
                        state.addMediaSize(PrintAttributes.MediaSize.ISO_A6,flag);
                    if(splitLine[i].equals("B5"))
                        state.addMediaSize(PrintAttributes.MediaSize.ISO_B5,flag);
                    if(splitLine[i].equals("Executive"))
                        state.addMediaSize(PrintAttributes.MediaSize.NA_MONARCH,flag);
                }
                //state.addMediaSize(PrintAttributes.MediaSize.ISO_A4, true);
            }

            if(line.contains("Resolution")) {
                String[] splitLine = line.split(" ");
                for (int i = 1; i < splitLine.length; i++) {
                    if(splitLine[i].startsWith("*")){
                        splitLine[i] = splitLine[i].replace("*","");
                        if(!splitLine[i].matches("^(\\d+)(.*)"))
                            continue;
                        else {
                            splitLine[i] = splitLine[i].replace("dpi", "");
                            String[] resolution = splitLine[i].split("x");
                            state.addResolution(new PrintAttributes.Resolution("R" + i, resolution[0] + "x" + resolution[1], Integer.parseInt(resolution[0]), Integer.parseInt(resolution[1])), true);
                            continue;
                        }
                    }
                    if(!splitLine[i].matches("^(\\d+)(.*)"))
                        continue;

                    splitLine[i] = splitLine[i].replace("dpi","");
                    String[] resolution = splitLine[i].split("x");
                    state.addResolution(new PrintAttributes.Resolution("R"+i,resolution[0]+"x"+resolution[1],Integer.parseInt(resolution[0]),Integer.parseInt(resolution[1])),false);
                }
                //state.addResolution(new PrintAttributes.Resolution("R1", "600x600", 600, 600), true);
            }

            if(line.startsWith("ColorMode") || line.startsWith("ColorModel") || line.startsWith("Color/Color")){
                int setDefault = PrintAttributes.COLOR_MODE_MONOCHROME;
                boolean color = false;
                String[] splitLine = line.split(" ");
                for (int i = 2; i < splitLine.length; i++) {
                    if(splitLine[i].startsWith("*")) {
                        splitLine[i] = splitLine[i].replace("*", "");
                        if(splitLine[i].equals("ICM") || splitLine[i].equals("RGB") || splitLine[i].equals("Color") || splitLine[i].equals("CMYK")){
                            color = true;
                            setDefault = PrintAttributes.COLOR_MODE_COLOR;
                        }
                    }
                    if(splitLine[i].equals("ICM") || splitLine[i].equals("RGB") || splitLine[i].equals("Color") || splitLine[i].equals("CMYK")){
                        color = true;
                    }
                }

                if(color)
                    state.setColorModes(PrintAttributes.COLOR_MODE_MONOCHROME|PrintAttributes.COLOR_MODE_COLOR,setDefault);
                else
                    state.setColorModes(PrintAttributes.COLOR_MODE_MONOCHROME,setDefault);

                //state.setColorModes(PrintAttributes.COLOR_MODE_MONOCHROME | PrintAttributes.COLOR_MODE_COLOR, PrintAttributes.COLOR_MODE_MONOCHROME);
            }

        }

        PrinterCapabilitiesInfo capabilities =state.build();

        PrinterInfo.Builder builder = new PrinterInfo.Builder(printerId, printerId.getLocalId(), PrinterInfo.STATUS_IDLE);


        PrinterInfo printer = builder.setCapabilities(capabilities)
                //.setDescription(item.getManufacturerName())
                .build();

        return printer;
    }

    @Override
    protected String bindTAG() {
        return "StateTask";
    }

    public static String Media2cups(PrintAttributes.MediaSize mediaSize) {

        String result = "A4";

        if(mediaSize.equals(PrintAttributes.MediaSize.NA_LETTER)){
            result = "Letter";
        }else if(mediaSize.equals(PrintAttributes.MediaSize.ISO_A4)){
            result = "A4";
        }else if(mediaSize.equals(PrintAttributes.MediaSize.ISO_A5)){
            result = "A5";
        }else if(mediaSize.equals(PrintAttributes.MediaSize.ISO_A6)){
            result = "A6";
        }else if(mediaSize.equals(PrintAttributes.MediaSize.ISO_B5)){
            result = "B5";
        }else if(mediaSize.equals(PrintAttributes.MediaSize.NA_MONARCH)){
            result = "Executive";
        }
        return result;
    }

    public static String Resulution2cups(PrintAttributes.Resolution resolution) {
        // TODO: 2016/5/29  Resulution2cups
        return null;
    }
}
