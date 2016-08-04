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
    private boolean mSharePrinter;

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

    public boolean ismSharePrinter() {
        return mSharePrinter;
    }

    public void setmSharePrinter(boolean mSharePrinter) {
        this.mSharePrinter = mSharePrinter;
    }

    /**
     * Convert the mediaSize from Android to CUPS.
     *
     * @param mediaSize mediaSize in Android
     * @return mediaSize in CUPS
     */
    public static String media2cups(PrintAttributes.MediaSize mediaSize) {

        String result = "A4";
        switch (mediaSize.getId()) {
            case "NA_LETTER":
                result = "Letter";
                break;
            case "ISO_A2":
                result = "A2";
                break;
            case "ISO_A3":
                result = "A3";
                break;
            case "ISO_A4":
                result = "A4";
                break;
            case "ISO_A5":
                result = "A5";
                break;
            case "ISO_A6":
                result = "A6";
                break;
            case "ISO_B5":
                result = "B5";
                break;
            case "ISO_B6":
                result = "B6";
                break;
            case "ISO_B7":
                result = "B7";
                break;
            case "ISO_C4":
                result = "C4";
                break;
            case "ISO_C5":
                result = "C5";
                break;
            case "NA_MONARCH":
                result = "Executive";
                break;
            case "ROC_8K":
                result = "8k";
                break;
            case "ROC_16K":
                result = "16k";
                break;
            case "NA_LEGAL":
                result = "Legal";
                break;
            case "NA_LEDGER":
                result = "Ledger";
                break;
            case "NA_TABLOID":
                result = "B";
                break;
            case "NA_INDEX_3X5":
                result = "Card3x5";
                break;
            case "JPN_HAGAKI":
                result = "Hagaki";
                break;
            case "NA_INDEX_4X6":
                result = "Photo4x6";
                break;
            case "NA_INDEX_5X8":
                result = "Card5x8";
                break;
            case "JPN_OUFUKU":
                result = "Oufuku";
                break;
            case "JIS_B5":
                result = "JB5";
                break;
            case "JIS_B7":
                result = "JB7";
                break;
            case "JIS_EXEC":
                result = "ExecutiveJIS";
                break;
            case "JPN_CHOU2":
                result = "EnvA2";
                break;
            case "JPN_CHOU3":
                result = "EnvCHOU3";
                break;
            case "JPN_CHOU4":
                result = "EnvCHOU4";
                break;
            case "PRC_5":
                result = "EnvDL";
                break;
            case "NA_QUARTO":
                result = "Mutsugiri";
                break;
            case "JPN_KAKU2":
                result = "EnvKaku2";
                break;
            case "JPN_YOU4":
                result = "Yougata4";
                break;
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

        switch(mediaSizeCupsItem) {
            case "Letter":
                result = PrintAttributes.MediaSize.NA_LETTER;
                break;
            case "A2":
                result = PrintAttributes.MediaSize.ISO_A2;
                break;
            case "A3":
                result = PrintAttributes.MediaSize.ISO_A3;
                break;
            case "A4":
                result = PrintAttributes.MediaSize.ISO_A4;
                break;
            case "A5":
                result = PrintAttributes.MediaSize.ISO_A5;
                break;
            case "A6":
            //case "A5Card":
                result = PrintAttributes.MediaSize.ISO_A6;
                break;
            case "B5":
            //case"ISOB5":
            //case "B5iso":
                result = PrintAttributes.MediaSize.ISO_B5;
                break;
            case "B6":
                result = PrintAttributes.MediaSize.ISO_B6;
                break;
            case "B7":
                result = PrintAttributes.MediaSize.ISO_B7;
                break;
            case "C4":
                result = PrintAttributes.MediaSize.ISO_C4;
                break;
            case "C5":
                result = PrintAttributes.MediaSize.ISO_C5;
                break;
            case "Executive":
                result = PrintAttributes.MediaSize.NA_MONARCH;
                break;
            case "8k":
            //case "270x390mm":
                result = PrintAttributes.MediaSize.ROC_8K;
                break;
            case "16k":
            //case "16k195x270":
            //case "195x270mm":
            //case "Big16K":
                result = PrintAttributes.MediaSize.ROC_16K;
                break;
            case "Legal":
                result = PrintAttributes.MediaSize.NA_LEGAL;
                break;
            case "Ledger":
                result = PrintAttributes.MediaSize.NA_LEDGER;
                break;
            case "B":
                result = PrintAttributes.MediaSize.NA_TABLOID;
                break;
            case "Card3x5":
            //case "3x5in":
                result = PrintAttributes.MediaSize.NA_INDEX_3X5;
                break;
            case "Hagaki":
            //case "Postcard":
            //case "100x148mm":
                result = PrintAttributes.MediaSize.JPN_HAGAKI;
                break;
            case "Photo4x6":
            //case "4x6in":
                result = PrintAttributes.MediaSize.NA_INDEX_4X6;
                break;
            case "Card5x8":
            //case "5x8in":
                result = PrintAttributes.MediaSize.NA_INDEX_5X8;
                break;
            case "Oufuku":
            //case "JapanesePostcard":
                result = PrintAttributes.MediaSize.JPN_OUFUKU;
                break;
            case "JB5":
            //case "B5jis":
            //case "JISB5":
                result = PrintAttributes.MediaSize.JIS_B5;
                break;
            case "JB7":
                result = PrintAttributes.MediaSize.JIS_B7;
                break;
            case "ExecutiveJIS":
            //case "FLSA":
                result = PrintAttributes.MediaSize.JIS_EXEC;
                break;
            case "EnvA2":
                result = PrintAttributes.MediaSize.JPN_CHOU2;
                break;
            case "EnvChou3":
            //case "Younaga3":
            //case "Nagagata3":
                result = PrintAttributes.MediaSize.JPN_CHOU3;
                break;
            case "EnvChou4":
                result = PrintAttributes.MediaSize.JPN_CHOU4;
                break;
            case "EnvDL":
            //case "ENVELOPEDL":
                result = PrintAttributes.MediaSize.PRC_5;
                break;
            case "Mutsugiri":
            //case "8x10in":
                result = PrintAttributes.MediaSize.NA_QUARTO;
                break;
            case "EnvKaku2":
                result = PrintAttributes.MediaSize.JPN_KAKU2;
                break;
            case "Yougata4":
                result = PrintAttributes.MediaSize.JPN_YOU4;
                break;

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
