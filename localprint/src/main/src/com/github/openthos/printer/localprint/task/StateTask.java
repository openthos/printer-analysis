package com.github.openthos.printer.localprint.task;

import android.print.PrintAttributes;
import android.print.PrinterCapabilitiesInfo;
import android.print.PrinterId;
import android.print.PrinterInfo;

import com.github.openthos.printer.localprint.model.PrinterOptionItem;

import java.util.List;

/**
 * Track the printer's functions and status B6
 * Created by bboxh on 2016/5/17.
 */
public class StateTask<Progress> extends CommandTask<PrinterId, Progress, PrinterInfo> {

    private PrinterId mPrinterId;

    @Override
    protected String[] setCmd(PrinterId... params) {
        mPrinterId = params[0];
        String name = mPrinterId.getLocalId();
        return new String[]{"sh", "proot.sh", "lpoptions", "-p", name, "-l"};
    }

    @Override
    protected PrinterInfo handleCommand(List<String> stdOut, List<String> stdErr) {

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
            } else if (line.contains("The printer or class does not exist")) {
                ERROR = "The printer or class does not exist";
                return null;
            }

        }

        PrinterCapabilitiesInfo.Builder state = new PrinterCapabilitiesInfo.Builder(mPrinterId);

        //We have not find the margin option in CUPS, so we specify a fixed value
        state.setMinMargins(new PrintAttributes.Margins(200, 200, 200, 200));

        PrintAttributes.Resolution r1 = null;
        int colorMode = -1;
        for (String line : stdOut) {
            if (line.contains("PageSize")) {
                String[] splitLine = line.split(" ");
                for (int i = 2; i < splitLine.length; i++) {
                    if (splitLine[i].equals("Custom.WIDTHxHEIGHT"))
                        continue;
                    boolean flag = false;
                    if (splitLine[i].startsWith("*")) {
                        flag = true;
                        splitLine[i] = splitLine[i].replace("*", "");
                    }

                    PrintAttributes.MediaSize size = PrinterOptionItem.cups2media(splitLine[i]);
                    if (size != null) {
                        state.addMediaSize(size, flag);
                    }

                }
                //state.addMediaSize(PrintAttributes.MediaSize.ISO_A4, true);
            }

            if (line.contains("Resolution")) {

                String[] splitLine = line.split(" ");
                for (int i = 1; i < splitLine.length; i++) {
                    if (splitLine[i].startsWith("*")) {
                        splitLine[i] = splitLine[i].replace("*", "");
                        if (!splitLine[i].matches("^(\\d+)(.*)")) {
                            continue;
                        } else {
                            splitLine[i] = splitLine[i].replace("dpi", "");
                            String[] resolution = splitLine[i].split("x");
                            r1 = new PrintAttributes.Resolution("R" + i
                                    , resolution[0] + "x" + resolution[1]
                                    , Integer.parseInt(resolution[0])
                                    , Integer.parseInt(resolution[1]));
                            state.addResolution(r1, true);
                            continue;
                        }
                    }
                    if (!splitLine[i].matches("^(\\d+)(.*)")) {
                        continue;
                    }

                    splitLine[i] = splitLine[i].replace("dpi", "");
                    String[] resolution = splitLine[i].split("x");
                    r1 = new PrintAttributes.Resolution("R" + i, resolution[0] + "x"
                                                            + resolution[1],
                                                        Integer.parseInt(resolution[0]),
                                                        Integer.parseInt(resolution[1]));
                    state.addResolution(r1, false);
                }

            }

            if (line.startsWith("ColorMode")
                    || line.startsWith("ColorModel") || line.startsWith("Color/Color")) {
                int setDefault = PrintAttributes.COLOR_MODE_MONOCHROME;
                boolean color = false;
                String[] splitLine = line.split(" ");
                for (int i = 2; i < splitLine.length; i++) {
                    if (splitLine[i].startsWith("*")) {
                        splitLine[i] = splitLine[i].replace("*", "");
                        if (splitLine[i].equals("ICM") || splitLine[i].equals("RGB")
                                || splitLine[i].equals("Color") || splitLine[i].equals("CMYK")) {
                            color = true;
                            setDefault = PrintAttributes.COLOR_MODE_COLOR;
                        }
                    }
                    if (splitLine[i].equals("ICM") || splitLine[i].equals("RGB")
                            || splitLine[i].equals("Color") || splitLine[i].equals("CMYK")) {
                        color = true;
                    }
                }

                if (color)
                    state.setColorModes(PrintAttributes.COLOR_MODE_COLOR, setDefault);
                else
                    state.setColorModes(PrintAttributes.COLOR_MODE_MONOCHROME, setDefault);

                colorMode = 1;

                /**
                 * state.setColorModes(PrintAttributes.COLOR_MODE_MONOCHROME
                 *  | PrintAttributes.COLOR_MODE_COLOR, PrintAttributes.COLOR_MODE_MONOCHROME);
                 */

            }

        }

        if (r1 == null) {
            state.addResolution(new PrintAttributes.Resolution("R0", "600x600", 600, 600), true);
        }
        if (colorMode == -1) {
            state.setColorModes(PrintAttributes.COLOR_MODE_MONOCHROME
                    , PrintAttributes.COLOR_MODE_MONOCHROME);
        }


        PrinterCapabilitiesInfo capabilities = state.build();
        PrinterInfo.Builder builder = new PrinterInfo.Builder(mPrinterId,
                                              mPrinterId.getLocalId(), PrinterInfo.STATUS_IDLE);
        PrinterInfo printer = builder.setCapabilities(capabilities)
                //.setDescription(item.getManufacturerName())
                .build();

        return printer;
    }

    @Override
    protected String bindTAG() {
        return "StateTask";
    }

}
