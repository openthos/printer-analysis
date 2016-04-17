package com.github.openthos.printer.openthosprintservice.model;

/**
 * Created by bboxh on 2016/4/14.
 */
public class DriveGsFoo2zjsItem {

    public static final String DEFAULT_GS = "-q -dBATCH -dSAFER -dQUIET -dNOPAUSE -sbinPAPERSIZE=a4 -r600x600 -sDEVICE=pbmraw";
    public static final String DEFAULT_FOO2ZJS = "-z3 -p9 -r600x600";
    int PrinterId;
    private String Gs;
    private String Foo2zjs;

    public DriveGsFoo2zjsItem() {
    }

    public DriveGsFoo2zjsItem(int printerId, String Gs, String Foo2zjs) {
        PrinterId = printerId;
        this.Gs = Gs;
        this.Foo2zjs = Foo2zjs;
    }


    public int getPrinterId() {
        return PrinterId;
    }

    public void setPrinterId(int printerId) {
        PrinterId = printerId;
    }

    public String getGs() {
        return Gs;
    }

    public void setGs(String gs) {
        this.Gs = gs;
    }

    public String getFoo2zjs() {
        return Foo2zjs;
    }

    public void setFoo2zjs(String foo2zjs) {
        this.Foo2zjs = foo2zjs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DriveGsFoo2zjsItem that = (DriveGsFoo2zjsItem) o;

        return PrinterId == that.PrinterId;

    }

    @Override
    public int hashCode() {
        return PrinterId;
    }

    @Override
    public String toString() {
        return "DriveGsFoo2zjsItem{" +
                "PrinterId=" + PrinterId +
                ", Gs='" + Gs + '\'' +
                ", Foo2zjs='" + Foo2zjs + '\'' +
                '}';
    }
}
