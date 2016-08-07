package com.github.openthos.printer.localprint.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The PPD driver info returned from CUPS.
 * Created by bboxh on 2016/5/16.
 */
public class ModelsItem implements Parcelable {

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.brand);
        dest.writeInt(this.models.size());
        for (Map.Entry<String, List<PPDItem>> entry : this.models.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeTypedList(entry.getValue());
        }
    }

    protected ModelsItem(Parcel in) {
        this.brand = in.createStringArrayList();
        int modelsSize = in.readInt();
        this.models = new HashMap<String, List<PPDItem>>(modelsSize);
        for (int i = 0; i < modelsSize; i++) {
            String key = in.readString();
            List<PPDItem> value = in.createTypedArrayList(PPDItem.CREATOR);
            this.models.put(key, value);
        }
    }

    public static final Creator<ModelsItem> CREATOR = new Creator<ModelsItem>() {
        @Override
        public ModelsItem createFromParcel(Parcel source) {
            return new ModelsItem(source);
        }

        @Override
        public ModelsItem[] newArray(int size) {
            return new ModelsItem[size];
        }
    };
}
