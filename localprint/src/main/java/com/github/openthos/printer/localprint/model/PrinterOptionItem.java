package com.github.openthos.printer.localprint.model;

import android.print.PrintAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * PrinterOptionItem represent the option that Android can read.
 * Setting the matching options between CUPS and Android.
 * Created by bboxh on 2016/5/29.
 */
public class PrinterOptionItem {

    /**
     * Describe the mediaSize symbol used in the current driver.
     */
    private String mediaSizeName = null;
    private int mediaSizeSelected = 0;
    private List<String> mediaSizeCupsList = new ArrayList<>();
    private List<PrintAttributes.MediaSize> mediaSizeList = new ArrayList<>();

    private String colorModeName = null;
    private int colorModeSelected = -1;
    private List<String> colorModeCupsList = new ArrayList<>();
    private List<Integer> colorModeList = new ArrayList<>();

    public PrinterOptionItem() {

    }

    public PrinterOptionItem(String mediaSizeName, String colorModeName) {
        this.mediaSizeName = mediaSizeName;
        this.colorModeName = colorModeName;
    }

    /**
     * Get the mapping item of selected MediaSize in CUPS.
     *
     * @return MediaSize
     */
    public String getMediaSizeCupsSelectedItem() {
        return mediaSizeCupsList.get(mediaSizeSelected);
    }

    /**
     * Get the mapping item of selected MediaSize in Android.
     *
     * @return MediaSize
     */
    public PrintAttributes.MediaSize getMediaSizeSelectedItem() {
        return mediaSizeList.get(mediaSizeSelected);
    }

    /**
     * Add a mediaSize.
     *
     * @param mediaSizeCupsItem Value in CUPS
     * @param flag              Set as default
     */
    public void addMediaSizeItem(String mediaSizeCupsItem, boolean flag) {
        PrintAttributes.MediaSize size = cups2media(mediaSizeCupsItem);
        if (size == null) {
            return;
        }
        mediaSizeCupsList.add(mediaSizeCupsItem);
        mediaSizeList.add(size);
        if (flag) {
            mediaSizeSelected = mediaSizeCupsList.size() - 1;
        }
    }

    /**
     * Get the mapping item of selected ColorMode in Android.
     *
     * @return ColorMode
     */
    public int getColorModeSelectedItem() {
        return colorModeList.get(colorModeSelected);
    }

    /**
     * Get the mapping item of selected ColorMode in CUPS.
     *
     * @return ColorMode
     */
    public String getColorModeCupsSelectedItem() {
        return colorModeCupsList.get(colorModeSelected);
    }

    /**
     * Add a ColorMode
     * Attention: add the default item firstly.
     *
     * @param colorModeCupsItem Value in CUPS
     * @param flag              Set as default
     */
    public void addColorModeItem(String colorModeCupsItem, boolean flag) {

        Integer colorModeItem = cups2color(colorModeCupsItem);

        //Each color can be added only once.
        if (colorModeList.contains(colorModeItem)) {
            return;
        }

        colorModeCupsList.add(colorModeCupsItem);
        colorModeList.add(colorModeItem);
        if (flag) {
            colorModeSelected = colorModeCupsList.size() - 1;
        }
    }


    public String getMediaSizeName() {
        return mediaSizeName;
    }

    public void setMediaSizeName(String mediaSizeName) {
        this.mediaSizeName = mediaSizeName;
    }

    public String getColorModeName() {
        return colorModeName;
    }

    public void setColorModeName(String colorModeName) {
        this.colorModeName = colorModeName;
    }

    public int getMediaSizeSelected() {
        return mediaSizeSelected;
    }

    public void setMediaSizeSelected(int mediaSizeSelected) {
        this.mediaSizeSelected = mediaSizeSelected;
    }

    public List<String> getMediaSizeCupsList() {
        return mediaSizeCupsList;
    }

    public void setMediaSizeCupsList(List<String> mediaSizeCupsList) {
        this.mediaSizeCupsList = mediaSizeCupsList;
    }

    public List<PrintAttributes.MediaSize> getMediaSizeList() {
        return mediaSizeList;
    }

    public void setMediaSizeList(List<PrintAttributes.MediaSize> mediaSizeList) {
        this.mediaSizeList = mediaSizeList;
    }

    public int getColorModeSelected() {
        return colorModeSelected;
    }

    public void setColorModeSelected(int colorModeSelected) {
        this.colorModeSelected = colorModeSelected;
    }

    public List<String> getColorModeCupsList() {
        return colorModeCupsList;
    }

    public void setColorModeCupsList(List<String> colorModeCupsList) {
        this.colorModeCupsList = colorModeCupsList;
    }

    public List<Integer> getColorModeList() {
        return colorModeList;
    }

    public void setColorModeList(List<Integer> colorModeList) {
        this.colorModeList = colorModeList;
    }


    /**
     * Convert the mediaSize from Android to CUPS.
     *
     * @param mediaSize mediaSize in Android
     * @return mediaSize in CUPS
     */
    public static String media2cups(PrintAttributes.MediaSize mediaSize) {

        // TODO: 2016/7/7 Add more mediaSize.

        String result = "A4";

        if (mediaSize.equals(PrintAttributes.MediaSize.NA_LETTER)) {
            result = "Letter";
        } else if (mediaSize.equals(PrintAttributes.MediaSize.ISO_A4)) {
            result = "A4";
        } else if (mediaSize.equals(PrintAttributes.MediaSize.ISO_A5)) {
            result = "A5";
        } else if (mediaSize.equals(PrintAttributes.MediaSize.ISO_A6)) {
            result = "A6";
        } else if (mediaSize.equals(PrintAttributes.MediaSize.ISO_B5)) {
            result = "B5";
        } else if (mediaSize.equals(PrintAttributes.MediaSize.NA_MONARCH)) {
            result = "Executive";
        }
        return result;
    }

    /**
     * Convert the mediaSize from CUPS to Android.
     *
     * @param mediaSizeCupsItem mediaSize in CUPS
     * @return mediaSize in Android
     */
    public static PrintAttributes.MediaSize cups2media(String mediaSizeCupsItem) {
        PrintAttributes.MediaSize result = null;
        if (mediaSizeCupsItem.equals("Letter")) {
            result = PrintAttributes.MediaSize.NA_LETTER;
        } else if (mediaSizeCupsItem.equals("A4")) {
            result = PrintAttributes.MediaSize.ISO_A4;
        } else if (mediaSizeCupsItem.equals("A5")) {
            result = PrintAttributes.MediaSize.ISO_A5;
        } else if (mediaSizeCupsItem.equals("A6")) {
            result = PrintAttributes.MediaSize.ISO_A6;
        } else if (mediaSizeCupsItem.equals("B5")) {
            result = PrintAttributes.MediaSize.ISO_B5;
        } else if (mediaSizeCupsItem.equals("Executive")) {
            result = PrintAttributes.MediaSize.NA_MONARCH;
        }

        return result;
    }

    /**
     * Convert the colorMode from CUPS to Android.
     *
     * @param colorModeCupsItem colorMode in CUPS
     * @return colorMode in Android
     */
    public static Integer cups2color(String colorModeCupsItem) {
        int result = PrintAttributes.COLOR_MODE_MONOCHROME;
        if (colorModeCupsItem.compareToIgnoreCase("Color") == 0) {
            result = PrintAttributes.COLOR_MODE_COLOR;
        } else if (colorModeCupsItem.compareToIgnoreCase("Grayscale") == 0) {
            result = PrintAttributes.COLOR_MODE_MONOCHROME;
        } else if (colorModeCupsItem.compareToIgnoreCase("ICM") == 0) {
            result = PrintAttributes.COLOR_MODE_COLOR;
        } else if (colorModeCupsItem.compareToIgnoreCase("Monochrome") == 0) {
            result = PrintAttributes.COLOR_MODE_MONOCHROME;
        } else if (colorModeCupsItem.compareToIgnoreCase("Gray") == 0) {
            result = PrintAttributes.COLOR_MODE_MONOCHROME;
        } else if (colorModeCupsItem.compareToIgnoreCase("RGB") == 0) {
            result = PrintAttributes.COLOR_MODE_COLOR;
        }
        return result;
    }

    public static String resulution2cups(PrintAttributes.Resolution resolution) {
        // TODO: 2016/5/29  Resulution2cups
        return null;
    }

}
