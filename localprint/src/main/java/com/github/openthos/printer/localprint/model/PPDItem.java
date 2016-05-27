package com.github.openthos.printer.localprint.model;

/**
 * Created by bboxh on 2016/5/23.
 */
public class PPDItem {
    private String model;
    private String brand;
    private String name;

    public PPDItem() {
    }

    public PPDItem(String model, String brand, String name) {
        this.model = model;
        this.brand = brand;
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PPDItem ppdItem = (PPDItem) o;

        if (model != null ? !model.equals(ppdItem.model) : ppdItem.model != null) return false;
        if (brand != null ? !brand.equals(ppdItem.brand) : ppdItem.brand != null) return false;
        return name != null ? name.equals(ppdItem.name) : ppdItem.name == null;

    }

    @Override
    public int hashCode() {
        int result = model != null ? model.hashCode() : 0;
        result = 31 * result + (brand != null ? brand.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }
}
