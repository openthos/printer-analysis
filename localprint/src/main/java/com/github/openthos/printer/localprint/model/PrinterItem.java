package com.github.openthos.printer.localprint.model;

import android.hardware.usb.UsbDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bboxh on 2016/4/14.
 */
public class PrinterItem implements Parcelable {

    private String NickName;
    private String URL;
    private String TYPE;

    public PrinterItem() {
    }

    public PrinterItem(String nickName, String URL, String TYPE) {
        NickName = nickName;
        this.URL = URL;
        this.TYPE = TYPE;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrinterItem that = (PrinterItem) o;

        if (NickName != null ? !NickName.equals(that.NickName) : that.NickName != null)
            return false;
        if (URL != null ? !URL.equals(that.URL) : that.URL != null) return false;
        return TYPE != null ? TYPE.equals(that.TYPE) : that.TYPE == null;

    }

    @Override
    public int hashCode() {
        int result = NickName != null ? NickName.hashCode() : 0;
        result = 31 * result + (URL != null ? URL.hashCode() : 0);
        result = 31 * result + (TYPE != null ? TYPE.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PrinterItem{" +
                "NickName='" + NickName + '\'' +
                ", URL='" + URL + '\'' +
                ", TYPE='" + TYPE + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.NickName);
        dest.writeString(this.URL);
        dest.writeString(this.TYPE);
    }

    protected PrinterItem(Parcel in) {
        this.NickName = in.readString();
        this.URL = in.readString();
        this.TYPE = in.readString();
    }

    public static final Creator<PrinterItem> CREATOR = new Creator<PrinterItem>() {
        @Override
        public PrinterItem createFromParcel(Parcel source) {
            return new PrinterItem(source);
        }

        @Override
        public PrinterItem[] newArray(int size) {
            return new PrinterItem[size];
        }
    };
}
