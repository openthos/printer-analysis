package com.github.openthos.printer.localprint.ui.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.model.PrinterItem;
import com.github.openthos.printer.localprint.task.ListAddedTask;
import com.github.openthos.printer.localprint.task.SearchPrintersTask;
import com.github.openthos.printer.localprint.ui.ManagementActivity;
import com.github.openthos.printer.localprint.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bboxh on 2016/4/15.
 */
public class ManagementAdapter extends BaseAdapter {

    private static final String TAG = "ManagementAdapter";
    private final List<ManagementListItem> listItem;
    private final ManagementActivity context;
    private int local_printer_position = -1;
    private boolean IS_DECTECTING = false;
    private boolean IS_DECTECTING_ADDED = false;
    private int net_printer_position = -1;
    private List<PrinterItem> addedList = new ArrayList<>();

    public ManagementAdapter(ManagementActivity context, List<ManagementListItem> listItem) {
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
                holder2.textView_printer_info.setText(item.getPrinteritem().getURL());
                break;
            case ManagementListItem.TYPE_LOCAL_PRINTER:
                holder6.textView_printer_name.setText(item.getPrinteritem().getNickName());
                holder6.textView_printer_info.setText(item.getPrinteritem().getURL());
                break;
            default:
                break;
        }

        return convertView;
    }

    /**
     * 刷新已添加的打印机
     */
    public void refreshAddedPrinters(){

        if(IS_DECTECTING_ADDED){
            return;
        }

        IS_DECTECTING_ADDED = true;

        new ListAddedTask<Void, Void>(){
            @Override
            protected void onPostExecute(List<PrinterItem> printerItems) {
                addedList.clear();
                addedList.addAll(printerItems);
                setAddedPrinters(addedList);
                IS_DECTECTING_ADDED = false;
            }
        }.start();

        setAddedPrinters(null);

    }

    private void setAddedPrinters(List<PrinterItem> list){
        removeSub(listItem, 1, local_printer_position - 1);
        local_printer_position = 2 ;

        //null代表正在搜索
        if(list == null){
            listItem.add(local_printer_position ++ - 1, new ManagementListItem.Builder(ManagementListItem.TYPE_LOADING).get());
        }else if(!list.isEmpty()){
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
     * 获得已添加打印机
     * @return null代表正在获取
     */
    private List<PrinterItem> getAddedPrinters() {




        return null;
    }

    /**
     * 初始化列表
     */
    public void initList() {

        listItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_ADDED_PRINTERS_WORDS).get());

        refreshAddedPrinters();

        /*List<PrinterItem> list = getAddedPrinters();

        if(list == null){
            listItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_LOADING).get());
        }else if(list.isEmpty()){
            listItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_EMPTY).get());
        }else{
            for(PrinterItem printerItem: list) {
                ManagementListItem item = new ManagementListItem.Builder(ManagementListItem.TYPE_ADDED_PRINTER).get();
                item.setPrinteritem(printerItem);
                listItem.add(item);
            }
        }*/

        listItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_ADDED_ENDLINE).get());
    }

    /**
     * 显示检测打印机的条目
     */
    public void startDetecting() {

        if(IS_DECTECTING){
            removeSub(listItem, local_printer_position + 1, net_printer_position);
            net_printer_position = local_printer_position + 1;
            listItem.add(net_printer_position ++, new ManagementListItem.Builder(ManagementListItem.TYPE_LOADING).get());
        }else{
            IS_DECTECTING = true;
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
     * 添加本地可添加打印机
     */
    public void addLocalPrinter(){


        SearchPrintersTask<Void,Void> task = new SearchPrintersTask<Void, Void>(){
            @Override
            protected void onPostExecute(List<PrinterItem> printerItems) {

                removeSub(listItem, local_printer_position + 1, net_printer_position);
                net_printer_position = local_printer_position + 1;

                if(printerItems != null){
                    for(PrinterItem p: printerItems){
                        ManagementListItem item = new ManagementListItem.Builder(ManagementListItem.TYPE_LOCAL_PRINTER).get();
                        item.setPrinteritem(p);
                        listItem.add(net_printer_position ++, item);
                    }
                }

                if( net_printer_position == local_printer_position + 1 ){
                    listItem.add(net_printer_position ++, new ManagementListItem.Builder(ManagementListItem.TYPE_EMPTY).get());
                }

                //关闭正在搜索的标记
                context.setIS_DETECTING(false);
                notifyDataSetChanged();
                Toast.makeText(context, R.string.search_finished, Toast.LENGTH_SHORT).show();
            }
        };
        task.start();


    }

    public void removeSub(List<?> listItem, int i, int j){

        LogUtils.d(TAG, "removeSub() i -> " + i + " j -> " + j);

        for(j--;i <= j; j--){
            listItem.remove(j);
        }
    }


}
