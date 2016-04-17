package com.github.openthos.printer.openthosprintservice.model;

/**
 * Created by bboxh on 2016/4/14.
 */
public class DriveItem {

    private int DriverId;
    private String DriverName;
    private int VendorId;
    private int ProductId;

    public DriveItem() {
    }

    public DriveItem(int driverId, String driverName, int vendorId, int productId) {
        ProductId = productId;
        DriverId = driverId;
        DriverName = driverName;
        VendorId = vendorId;
    }

    public int getDriverId() {
        return DriverId;
    }

    public void setDriverId(int driverId) {
        DriverId = driverId;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DriveItem driveItem = (DriveItem) o;

        if (DriverId != driveItem.DriverId) return false;
        if (VendorId != driveItem.VendorId) return false;
        return ProductId == driveItem.ProductId;

    }

    @Override
    public int hashCode() {
        int result = DriverId;
        result = 31 * result + VendorId;
        result = 31 * result + ProductId;
        return result;
    }

    @Override
    public String toString() {
        return "DriveItem{" +
                "DriverId=" + DriverId +
                ", DriverName='" + DriverName + '\'' +
                ", VendorId=" + VendorId +
                ", ProductId=" + ProductId +
                '}';
    }
}
