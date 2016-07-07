package com.github.openthos.printer.localprint.model;

import java.util.List;
import java.util.Map;

/**
 * The PPD driver info returned from CUPS.
 * Created by bboxh on 2016/5/16.
 */
public class ModelsItem {

    private List<String> brand;
    private Map<String, List<PPDItem>> models;

    public ModelsItem(List<String> brand, Map<String, List<PPDItem>> models) {
        this.brand = brand;
        this.models = models;
    }

    public ModelsItem() {
    }

    public List<String> getBrand() {
        return brand;
    }

    public void setBrand(List<String> brand) {
        this.brand = brand;
    }

    public Map<String, List<PPDItem>> getModels() {
        return models;
    }

    public void setModels(Map<String, List<PPDItem>> models) {
        this.models = models;
    }

    @Override
    public String toString() {
        return "ModelsItem{" +
                "brand=" + brand +
                ", models=" + models +
                '}';
    }

}
