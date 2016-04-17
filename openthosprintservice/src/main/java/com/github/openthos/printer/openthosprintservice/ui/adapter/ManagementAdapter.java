package com.github.openthos.printer.openthosprintservice.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.github.openthos.printer.openthosprintservice.APP;
import com.github.openthos.printer.openthosprintservice.R;
import com.github.openthos.printer.openthosprintservice.model.PrinterItem;
import com.github.openthos.printer.openthosprintservice.model.PrinterItemHelper;
import com.github.openthos.printer.openthosprintservice.ui.ManagementActivity;
import com.github.openthos.printer.openthosprintservice.util.LogUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bboxh on 2016/4/15.
 */
public class ManagementAdapter extends BaseAdapter {

    private static final String TAG = "ManagementAdapter";
    private final List<ManagementListItem> listItem;
    private final Context context;
    private int local_printer_position = -1;
    private boolean IS_DECTECTING_EXIST = false;
    private int net_printer_position = -1;

    public ManagementAdapter(Context context, List<ManagementListItem> listItem) {
        this.context = context;
        this.listItem = listItem;

    }



    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public ManagementListItem getItem(int position) {
        return listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        return listItem.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return ManagementListItem.TYPE_COUNT;
    }

    @Override
    public boolean isEnabled(int position) {

        boolean flag = false;

        switch (listItem.get(position).getType()){
            case ManagementListItem.TYPE_ADDED_PRINTER:
                flag = true;
                break;
            case ManagementListItem.TYPE_LOCAL_PRINTER:
                flag = true;
                break;
            default:
                break;
        }
        return flag;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ManagementListItem item = listItem.get(position);
        int type = item.getType();

        LayoutInflater inflater = LayoutInflater.from(context);

        ADDED_PRINTER holder2 = null;
        LOCAL_PRINTER holder6 = null;

        if(convertView == null){
            switch (type){
                case ManagementListItem.TYPE_ADDED_PRINTERS_WORDS:
                    convertView = inflater.inflate(R.layout.item_added_printers_words, null);
                    break;
                case ManagementListItem.TYPE_ADDED_ENDLINE:
                    convertView = inflater.inflate(R.layout.item_added_endline, null);
                    //ADDED_ENDLINE holder3 = (ADDED_ENDLINE) item.getViewHolder();
                    Button button_add_printers = (Button)convertView.findViewById(R.id.button_add_printers);
                    //添加监听->扫描新的打印机
                    button_add_printers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ManagementActivity.class);
                            intent.putExtra(APP.TASK, APP.TASK_ADD_NEW_PRINTER);
                            context.startActivity(intent);
                        }
                    });
                    break;
                case ManagementListItem.TYPE_LOCAL_PRINTER_WORDS:
                    convertView = inflater.inflate(R.layout.item_local_printer_words, null);
                    break;
                case ManagementListItem.TYPE_NET_PRINTER_WORDS:
                    convertView = inflater.inflate(R.layout.item_net_printer_words, null);
                    break;
                case ManagementListItem.TYPE_LOADING:
                    convertView = inflater.inflate(R.layout.item_loading, null);
                    break;
                case ManagementListItem.TYPE_EMPTY:
                    convertView = inflater.inflate(R.layout.item_empty, null);
                    break;
                case ManagementListItem.TYPE_ADDED_PRINTER:
                    holder2 = (ADDED_PRINTER)item.getViewHolder();
                    convertView = inflater.inflate(R.layout.item_printer, null);
                    holder2.textView_printer_name = (TextView) convertView.findViewById(R.id.textView_printer_name);
                    holder2.textView_printer_info = (TextView) convertView.findViewById(R.id.textView_printer_info);
                    convertView.setTag(holder2);
                    break;
                case ManagementListItem.TYPE_LOCAL_PRINTER:
                    holder6 = (LOCAL_PRINTER)item.getViewHolder();
                    convertView = inflater.inflate(R.layout.item_printer, null);
                    holder6.textView_printer_name = (TextView) convertView.findViewById(R.id.textView_printer_name);
                    holder6.textView_printer_info = (TextView) convertView.findViewById(R.id.textView_printer_info);
                    convertView.setTag(holder6);
                    break;
                default:
                    break;
            }
        }else{
            switch (type){
                case ManagementListItem.TYPE_ADDED_PRINTER:
                    holder2 = (ADDED_PRINTER) convertView.getTag();
                    break;
                case ManagementListItem.TYPE_LOCAL_PRINTER:
                    holder6 = (LOCAL_PRINTER) convertView.getTag();
                    break;
                default:
                    break;
            }
        }

        switch (type){
            case ManagementListItem.TYPE_ADDED_PRINTER:
                holder2.textView_printer_name.setText(item.getPrinteritem().getNickName());
                holder2.textView_printer_info.setText(item.getPrinteritem().getSerialNumber());
                break;
            case ManagementListItem.TYPE_LOCAL_PRINTER:
                holder6.textView_printer_name.setText(item.getPrinteritem().getNickName());
                holder6.textView_printer_info.setText(item.getPrinteritem().getSerialNumber());
                break;
            default:
                break;
        }

        return convertView;
    }

    /**
     * 刷新已添加的打印机
     */
    public void refreshAddedprinters(){

        removeSub(listItem, 1, local_printer_position - 1);
        local_printer_position = 2 ;
        PrinterItemHelper helper = new PrinterItemHelper();
        List<PrinterItem> list = helper.queryAll();
        if(list != null){
            for(PrinterItem printerItem: list) {
                ManagementListItem item = new ManagementListItem.Builder(ManagementListItem.TYPE_ADDED_PRINTER).get();
                item.setPrinteritem(printerItem);
                listItem.add(local_printer_position ++ - 1, item);
            }
        }else{
            listItem.add(local_printer_position ++ - 1, new ManagementListItem.Builder(ManagementListItem.TYPE_EMPTY).get());
        }

        notifyDataSetChanged();

    }

    /**
     * 初始化列表
     */
    public void initList() {

        listItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_ADDED_PRINTERS_WORDS).get());

        PrinterItemHelper helper = new PrinterItemHelper();
        List<PrinterItem> list = helper.queryAll();
        if(list != null){
            for(PrinterItem printerItem: list) {
                ManagementListItem item = new ManagementListItem.Builder(ManagementListItem.TYPE_ADDED_PRINTER).get();
                item.setPrinteritem(printerItem);
                listItem.add(item);
            }
        }else{
            listItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_EMPTY).get());
        }

        listItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_ADDED_ENDLINE).get());
    }

    /**
     * 显示检测打印机的条目
     */
    public void startDetecting() {

        if(IS_DECTECTING_EXIST){
            /*removeSub(listItem, local_printer_position + 1, net_printer_position);
            net_printer_position = local_printer_position + 1;
            listItem.add(net_printer_position ++, new ManagementListItem.Builder(ManagementListItem.TYPE_LOADING).get());*/
        }else{
            IS_DECTECTING_EXIST = true;
            listItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_LOCAL_PRINTER_WORDS).get());
            local_printer_position = listItem.size() - 1;
            listItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_LOADING).get());
            listItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_NET_PRINTER_WORDS).get());
            net_printer_position = listItem.size() - 1;
            listItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_EMPTY).get());
            //listItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_LOADING).get());
        }

        addLocalPrinter();

        LogUtils.d(TAG, "local_printer_position -> " + local_printer_position + " net_printer_position -> " + net_printer_position);

        this.notifyDataSetChanged();

    }

    /**
     * 添加本地打印机
     */
    public void addLocalPrinter(){

        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

        removeSub(listItem, local_printer_position + 1, net_printer_position);
        net_printer_position = local_printer_position + 1;

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();


            boolean is_printer = false;

            for(int i=0; i < device.getInterfaceCount(); i++ ){
                // InterfaceClass 7 代表打印机
                if(device.getInterface(i).getInterfaceClass() == 7){
                    is_printer = true;
                    break;
                    }
                }
            if(!is_printer){
                continue;
            }


            ManagementListItem item = new ManagementListItem.Builder(ManagementListItem.TYPE_LOCAL_PRINTER).get();
            PrinterItem printerItem = new PrinterItem();

            item.setDeviceItem(device);
            item.setPrinteritem(printerItem);
            printerItem.setNickName(device.getProductName());
            printerItem.setManufacturerName(device.getManufacturerName());
            printerItem.setSerialNumber(device.getSerialNumber());
            printerItem.setDriverId(1);
            listItem.add(net_printer_position ++, item);
        }

        if( net_printer_position == local_printer_position + 1 ){
            listItem.add(net_printer_position ++, new ManagementListItem.Builder(ManagementListItem.TYPE_EMPTY).get());
        }

        //this.notifyDataSetChanged();

    }

    public void removeSub(List<?> listItem, int i, int j){

        LogUtils.d(TAG, "removeSub() i -> " + i + " j -> " + j);

        for(j--;i <= j; j--){
            listItem.remove(j);
        }
    }


}
