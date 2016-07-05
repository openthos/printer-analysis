package com.github.openthos.printer.localprint.model;

import android.print.PrintAttributes;

import java.util.ArrayList;
import java.util.List;

/**
 * 打印机设置项
 * 设置安卓中匹配的项
 * Created by bboxh on 2016/5/29.
 */
public class PrinterOptionItem {

    private String mediaSizeName = null;           //在该驱动中纸张大小的标示
    private int mediaSizeSelected = 0;          //当前选择的
    private List<String> mediaSizeCupsList = new ArrayList<>();     //存放cups中
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
     * 获得选择的MediaSize项CUPS对应的内容
     * @return
     */
    public String getMediaSizeCupsSelectedItem(){
        return mediaSizeCupsList.get(mediaSizeSelected);
    }

    /**
     * 获得选择的MediaSize项的内容，android中的
     * @return
     */
    public PrintAttributes.MediaSize getMediaSizeSelectedItem(){
        return mediaSizeList.get(mediaSizeSelected);
    }

    /**
     * 添加一项尺寸
     * @param mediaSizeCupsItem     Cups中的内容
     * @param flag                  是否是默认值
     */
    public void addMediaSizeItem(String mediaSizeCupsItem, boolean flag){
        PrintAttributes.MediaSize size = cups2media(mediaSizeCupsItem);
        if(size == null){
            return;
        }
        mediaSizeCupsList.add(mediaSizeCupsItem);
        mediaSizeList.add(size);
        if(flag){
            mediaSizeSelected = mediaSizeCupsList.size() - 1;
        }
    }

    /**
     * 获取选择的色彩，android中的
     * @return
     */
    public int getColorModeSelectedItem(){
        return colorModeList.get(colorModeSelected);
    }

    /**
     * 获取选择的色彩，Cups中对应的内容
     * @return
     */
    public String getColorModeCupsSelectedItem(){
        return colorModeCupsList.get(colorModeSelected);
    }

    /**
     * 添加一项色彩
     * 注意：先添加默认项
     * @param colorModeCupsItem     Cups中的内容
     * @param flag                  是否是默认值
     */
    public void addColorModeItem(String colorModeCupsItem, boolean flag){

        Integer colorModeItem = cups2color(colorModeCupsItem);

        //每种色彩只添加一次
        if(colorModeList.contains(colorModeItem)){
            return;
        }

        colorModeCupsList.add(colorModeCupsItem);
        colorModeList.add(colorModeItem);
        if(flag){
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
     * 转换android中的mediaSize到Cups中的表示符号
     * @param mediaSize
     * @return
     */
    public static String media2cups(PrintAttributes.MediaSize mediaSize) {

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

    /**
     * 转换Cups中的mediaSize表示符号到android中的mediaSize
     * @param mediaSizeCupsItem
     * @return
     */
    public static PrintAttributes.MediaSize cups2media(String mediaSizeCupsItem) {
        PrintAttributes.MediaSize result = null;
        if(mediaSizeCupsItem.equals("Letter")) {
            result = PrintAttributes.MediaSize.NA_LETTER;
        }else if(mediaSizeCupsItem.equals("A4")) {
            result = PrintAttributes.MediaSize.ISO_A4;
        }else if(mediaSizeCupsItem.equals("A5")) {
            result = PrintAttributes.MediaSize.ISO_A5;
        }else if(mediaSizeCupsItem.equals("A6")) {
            result = PrintAttributes.MediaSize.ISO_A6;
        }else if(mediaSizeCupsItem.equals("B5")) {
            result = PrintAttributes.MediaSize.ISO_B5;
        }else if(mediaSizeCupsItem.equals("Executive")) {
            result = PrintAttributes.MediaSize.NA_MONARCH;
        }

        return result;
    }

    /**
     * 转换Cups中的colorMode表示符号到android中的颜色
     * @param colorModeCupsItem
     * @return
     */
    public static Integer cups2color(String colorModeCupsItem) {
        int result = PrintAttributes.COLOR_MODE_MONOCHROME;
        if(colorModeCupsItem.compareToIgnoreCase("Color") == 0){
            result = PrintAttributes.COLOR_MODE_COLOR;
        }else if(colorModeCupsItem.compareToIgnoreCase("Grayscale") == 0){
            result = PrintAttributes.COLOR_MODE_MONOCHROME;
        }else if(colorModeCupsItem.compareToIgnoreCase("ICM") == 0){
            result = PrintAttributes.COLOR_MODE_COLOR;
        }else if(colorModeCupsItem.compareToIgnoreCase("Monochrome") == 0){
            result = PrintAttributes.COLOR_MODE_MONOCHROME;
        }else if(colorModeCupsItem.compareToIgnoreCase("Gray") == 0){
            result = PrintAttributes.COLOR_MODE_MONOCHROME;
        }else if(colorModeCupsItem.compareToIgnoreCase("RGB") == 0){
            result = PrintAttributes.COLOR_MODE_COLOR;
        }
        return new Integer(result);
    }

    public static String resulution2cups(PrintAttributes.Resolution resolution) {
        // TODO: 2016/5/29  Resulution2cups
        return null;
    }

}
