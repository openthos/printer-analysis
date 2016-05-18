package com.github.openthos.printer.localprint.ui.adapter;

import android.hardware.usb.UsbDevice;
import android.widget.TextView;

import com.github.openthos.printer.localprint.model.PrinterItem;

/**
 * Created by bboxh on 2016/4/15.
 */
public class ManagementListItem<T> {

    public static final int TYPE_ADDED_PRINTERS_WORDS = 0;
    public static final int TYPE_ADDED_PRINTER = 1;
    public static final int TYPE_ADDED_ENDLINE = 2;
    public static final int TYPE_LOCAL_PRINTER_WORDS = 3;
    public static final int TYPE_NET_PRINTER_WORDS = 4;
    public static final int TYPE_LOADING = 5;
    public static final int TYPE_LOCAL_PRINTER = 6;
    public static final int TYPE_EMPTY = 7;

    public static final int TYPE_COUNT = 8;


    private int type;
    private T viewHolder;
    private PrinterItem printeritem = null;

    public ManagementListItem(int type, T t) {
        this.type = type;
        viewHolder = t;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public T getViewHolder() {
        return viewHolder;
    }

    public void setViewHolder(T viewHolder) {
        this.viewHolder = viewHolder;
    }

    public PrinterItem getPrinteritem() {
        return printeritem;
    }

    public void setPrinteritem(PrinterItem printeritem) {
        this.printeritem = printeritem;
    }


    public static class Builder{

        ManagementListItem item = null;

        public Builder(int type) {
            super();

            switch (type) {
                case TYPE_ADDED_PRINTERS_WORDS:
                    item = new ManagementListItem<ADDED_PRINTERS_WORDS>(TYPE_ADDED_PRINTERS_WORDS, new ADDED_PRINTERS_WORDS());
                    break;
                case TYPE_ADDED_PRINTER:
                    item = new ManagementListItem<ADDED_PRINTER>(TYPE_ADDED_PRINTER, new ADDED_PRINTER());
                    break;
                case TYPE_ADDED_ENDLINE:
                    item = new ManagementListItem<ADDED_ENDLINE>(TYPE_ADDED_ENDLINE, new ADDED_ENDLINE());
                    break;
                case TYPE_LOCAL_PRINTER_WORDS:
                    item = new ManagementListItem<LOCAL_PRINTER_WORDS>(TYPE_LOCAL_PRINTER_WORDS, new LOCAL_PRINTER_WORDS());
                    break;
                case TYPE_NET_PRINTER_WORDS:
                    item = new ManagementListItem<NET_PRINTER_WORDS>(TYPE_NET_PRINTER_WORDS, new NET_PRINTER_WORDS());
                    break;
                case TYPE_LOADING:
                    item = new ManagementListItem<LOADING>(TYPE_LOADING, new LOADING());
                    break;
                case TYPE_LOCAL_PRINTER:
                    item = new ManagementListItem<LOCAL_PRINTER>(TYPE_LOCAL_PRINTER, new LOCAL_PRINTER());
                    break;
                case TYPE_EMPTY:
                    item = new ManagementListItem<EMPTY>(TYPE_EMPTY, new EMPTY());
                    break;
                default:

                    break;
            }
        }

        public ManagementListItem get(){
            return item;
        }

    }

    @Override
    public String toString() {
        return "ManagementListItem{" +
                "type=" + type +
                ", printeritem=" + printeritem +
                '}';
    }



}


class ADDED_PRINTERS_WORDS {

}

class ADDED_PRINTER {

    TextView textView_printer_name;
    TextView textView_printer_info;

}

class ADDED_ENDLINE {

}

class LOCAL_PRINTER_WORDS {

}

class NET_PRINTER_WORDS {

}

class LOADING {

}

class LOCAL_PRINTER {
    TextView textView_printer_name;
    TextView textView_printer_info;
}

class EMPTY {

}