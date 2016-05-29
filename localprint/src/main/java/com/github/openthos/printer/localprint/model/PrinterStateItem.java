package com.github.openthos.printer.localprint.model;

import android.print.PrintAttributes;

/**
 * 打印机状态
 * Created by bboxh on 2016/5/29.
 */
public class PrinterStateItem {

    private String resolutionCups;
    private PrintAttributes.Resolution resolution;

    private String mediaSize;
    private PrintAttributes.MediaSize mediaSizeCups;

    private String colorModeCups;
    private int colorMode;

    public PrinterStateItem() {
    }

    public String getResolutionCups() {
        return resolutionCups;
    }

    public void setResolutionCups(String resolutionCups) {
        this.resolutionCups = resolutionCups;
    }

    public PrintAttributes.Resolution getResolution() {
        return resolution;
    }

    public void setResolution(PrintAttributes.Resolution resolution) {
        this.resolution = resolution;
    }

    public String getMediaSize() {
        return mediaSize;
    }

    public void setMediaSize(String mediaSize) {
        this.mediaSize = mediaSize;
    }

    public PrintAttributes.MediaSize getMediaSizeCups() {
        return mediaSizeCups;
    }

    public void setMediaSizeCups(PrintAttributes.MediaSize mediaSizeCups) {
        this.mediaSizeCups = mediaSizeCups;
    }

    public String getColorModeCups() {
        return colorModeCups;
    }

    public void setColorModeCups(String colorModeCups) {
        this.colorModeCups = colorModeCups;
    }

    public int getColorMode() {
        return colorMode;
    }

    public void setColorMode(int colorMode) {
        this.colorMode = colorMode;
    }
}
