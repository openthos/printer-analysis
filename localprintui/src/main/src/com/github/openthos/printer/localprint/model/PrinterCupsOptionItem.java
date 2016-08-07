package com.github.openthos.printer.localprint.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * PrinterCupsOptionItem describes one option of a printer in CUPS.
 * Created by bboxh on 2016/5/27.
 */
public class PrinterCupsOptionItem implements Parcelable {

    private int def = 0;
    private List<String> option = new ArrayList<>();

    /**
     * The nickName of the option.
     */
    private String name;

    /**
     * The name of the option , which is CUPS can read.
     */
    private String option_id;

    /**
     * The default value after a change operation.
     */
    private int def2 = -1;

    public PrinterCupsOptionItem() {
    }

    public List<String> getOption() {
        return option;
    }

    /**
     * Add a optional value.
     *
     * @param item value
     * @param flag to be default , many times assignment will record the last one.
     */
    public void add(String item, boolean flag) {
        this.option.add(item);
        if (flag) {
            def = this.option.size() - 1;
        }
    }

    public int getDef() {
        return def;
    }

    public void setDef2(int def2) {
        this.def2 = def2;
    }

    public int getDef2() {
        return def2 == -1 ? def : def2;
    }

    public String getDefValue() {
        return option.get(def);
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOption_id() {
        return option_id;
    }

    public void setOption_id(String option_id) {
        this.option_id = option_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrinterCupsOptionItem that = (PrinterCupsOptionItem) o;

        if (def != that.def) return false;
        if (def2 != that.def2) return false;
        if (option != null ? !option.equals(that.option) : that.option != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return option_id != null ? option_id.equals(that.option_id) : that.option_id == null;

    }

    @Override
    public int hashCode() {
        int result = def;
        result = 31 * result + (option != null ? option.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (option_id != null ? option_id.hashCode() : 0);
        result = 31 * result + def2;
        return result;
    }

    @Override
    public String toString() {
        return "PrinterCupsOptionItem{" +
                "def=" + def +
                ", option=" + option +
                ", name='" + name + '\'' +
                ", option_id='" + option_id + '\'' +
                ", def2=" + def2 +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.def);
        dest.writeStringList(this.option);
        dest.writeString(this.name);
        dest.writeString(this.option_id);
        dest.writeInt(this.def2);
    }

    protected PrinterCupsOptionItem(Parcel in) {
        this.def = in.readInt();
        this.option = in.createStringArrayList();
        this.name = in.readString();
        this.option_id = in.readString();
        this.def2 = in.readInt();
    }

    public static final Creator<PrinterCupsOptionItem> CREATOR = new Creator<PrinterCupsOptionItem>() {
        @Override
        public PrinterCupsOptionItem createFromParcel(Parcel source) {
            return new PrinterCupsOptionItem(source);
        }

        @Override
        public PrinterCupsOptionItem[] newArray(int size) {
            return new PrinterCupsOptionItem[size];
        }
    };

}
