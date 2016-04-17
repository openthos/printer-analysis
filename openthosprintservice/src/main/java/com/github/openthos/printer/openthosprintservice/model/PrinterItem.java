package com.github.openthos.printer.openthosprintservice.model;

import android.hardware.usb.UsbDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bboxh on 2016/4/14.
 */
public class PrinterItem implements Parcelable {

    private int PrinterId = -1;
    private String NickName;
    private String ManufacturerName;
    private int VendorId;
    private int ProductId;
    private String SerialNumber;
    private int DriverId;

    public PrinterItem() {
    }

    public PrinterItem(String nickName, String manufacturerName, int vendorId, int productId, String serialNumber, int driverId) {
        NickName = nickName;
        ManufacturerName = manufacturerName;
        VendorId = vendorId;
        ProductId = productId;
        SerialNumber = serialNumber;
        DriverId = driverId;
    }

    /**
     * NickName = deviceItem.getProductName();
     * ManufacturerName = deviceItem.getManufacturerName();
     * VendorId = deviceItem.getVendorId();
     * ProductId = deviceItem.getProductId();
     * SerialNumber = deviceItem.getSerialNumber();
     * DriverId = deviceItem.getDeviceId();
     * @param deviceItem
     */
    public PrinterItem(UsbDevice deviceItem) {
        NickName = deviceItem.getProductName();
        ManufacturerName = deviceItem.getManufacturerName();
        VendorId = deviceItem.getVendorId();
        ProductId = deviceItem.getProductId();
        SerialNumber = deviceItem.getSerialNumber();
        DriverId = deviceItem.getDeviceId();
    }

    protected PrinterItem(Parcel in) {
        PrinterId = in.readInt();
        NickName = in.readString();
        ManufacturerName = in.readString();
        VendorId = in.readInt();
        ProductId = in.readInt();
        SerialNumber = in.readString();
        DriverId = in.readInt();
    }

    public static final Creator<PrinterItem> CREATOR = new Creator<PrinterItem>() {
        @Override
        public PrinterItem createFromParcel(Parcel in) {
            return new PrinterItem(in);
        }

        @Override
        public PrinterItem[] newArray(int size) {
            return new PrinterItem[size];
        }
    };

    public int getPrinterId() {
        return PrinterId;
    }

    public void setPrinterId(int printerId) {
        PrinterId = printerId;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getManufacturerName() {
        return ManufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        ManufacturerName = manufacturerName;
    }

    public int getVendorId() {
        return VendorId;
    }

    public void setVendorId(int vendorId) {
        VendorId = vendorId;
    }

    public int getProductId() {
        return ProductId;
    }

    public void setProductId(int productId) {
        ProductId = productId;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    public int getDriverId() {
        return DriverId;
    }

    public void setDriverId(int driverId) {
        DriverId = driverId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PrinterItem that = (PrinterItem) o;

        if (PrinterId != that.PrinterId) return false;
        if (VendorId != that.VendorId) return false;
        if (ProductId != that.ProductId) return false;
        if (SerialNumber != null ? !SerialNumber.equals(that.SerialNumber) : that.SerialNumber != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (PrinterId ^ (PrinterId >>> 32));
        result = 31 * result + VendorId;
        result = 31 * result + ProductId;
        result = 31 * result + (SerialNumber != null ? SerialNumber.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PrinterItem{" +
                "PrinterId=" + PrinterId +
                ", NickName='" + NickName + '\'' +
                ", ManufacturerName='" + ManufacturerName + '\'' +
                ", VendorId=" + VendorId +
                ", ProductId=" + ProductId +
                ", SerialNumber='" + SerialNumber + '\'' +
                ", DriverId=" + DriverId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(PrinterId);
        dest.writeString(NickName);
        dest.writeString(ManufacturerName);
        dest.writeInt(VendorId);
        dest.writeInt(ProductId);
        dest.writeString(SerialNumber);
        dest.writeInt(DriverId);
    }
}
