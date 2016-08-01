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
 * List adapter in Management activity
 * Created by bboxh on 2016/4/15.
 */
public class ManagementAdapter extends BaseAdapter {

    private static final String TAG = "ManagementAdapter";

    private final List<ManagementListItem> mListItem;
    private final ManagementActivity mContext;

    private boolean IS_DECTECTING = false;
    private boolean IS_DECTECTING_ADDED = false;

    private List<PrinterItem> mAddedList = new ArrayList<>();
    private List<PrinterItem> mDetectedList = new ArrayList<>();

    public ManagementAdapter(ManagementActivity context, List<ManagementListItem> listItem) {
        mContext = context;
        mListItem = listItem;
    }


    @Override
    public int getCount() {
        return mListItem.size();
    }

    @Override
    public ManagementListItem getItem(int position) {
        return mListItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        return mListItem.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return ManagementListItem.TYPE_COUNT;
    }

    @Override
    public boolean isEnabled(int position) {

        boolean flag = false;

        switch (mListItem.get(position).getType()) {
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
        ManagementListItem item = mListItem.get(position);
        int type = item.getType();

        LayoutInflater inflater = LayoutInflater.from(mContext);

        ADDED_PRINTER holder2 = null;
        LOCAL_PRINTER holder6 = null;

        if (convertView == null) {
            switch (type) {
                case ManagementListItem.TYPE_ADDED_PRINTERS_WORDS:
                    convertView = inflater.inflate(R.layout.item_added_printers_words, null);
                    break;
                case ManagementListItem.TYPE_ADDED_ENDLINE:
                    convertView = inflater.inflate(R.layout.item_added_endline, null);
                    break;
                case ManagementListItem.TYPE_LOCAL_PRINTER_WORDS:
                    convertView = inflater.inflate(R.layout.item_local_printer_words, null);
                    Button button_add_printers
                            = (Button) convertView.findViewById(R.id.button_add_printers);
                    //add listener for scanning new printers
                    button_add_printers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, ManagementActivity.class);
                            intent.putExtra(APP.TASK, APP.TASK_ADD_NEW_PRINTER);
                            mContext.startActivity(intent);
                        }
                    });
                    Button button_add_net_printers = (Button) convertView.findViewById(
                            R.id.button_add_net_printers);
                    button_add_net_printers.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, ManagementActivity.class);
                            intent.putExtra(APP.TASK, APP.TASK_ADD_NEW_NET_PRINTER);
                            mContext.startActivity(intent);
                        }
                    });
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
                    holder2 = (ADDED_PRINTER) item.getViewHolder();
                    convertView = inflater.inflate(R.layout.item_printer, null);
                    holder2.textView_printer_name
                            = (TextView) convertView.findViewById(R.id.textView_printer_name);
                    holder2.textView_printer_info
                            = (TextView) convertView.findViewById(R.id.textView_printer_info);
                    convertView.setTag(holder2);
                    break;
                case ManagementListItem.TYPE_LOCAL_PRINTER:
                    holder6 = (LOCAL_PRINTER) item.getViewHolder();
                    convertView = inflater.inflate(R.layout.item_printer, null);
                    holder6.textView_printer_name
                            = (TextView) convertView.findViewById(R.id.textView_printer_name);
                    holder6.textView_printer_info
                            = (TextView) convertView.findViewById(R.id.textView_printer_info);
                    convertView.setTag(holder6);
                    break;
                default:
                    break;
            }
        } else {
            switch (type) {
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

        switch (type) {
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

    public void initList() {
        refreshAddedPrinters();
        startDetecting();
    }

    private void showList() {

        List<ManagementListItem> list = mListItem;
        List<PrinterItem> addedList = mAddedList;
        List<PrinterItem> detectedList = mDetectedList;
        list.clear();

        list.add(new ManagementListItem
                .Builder(ManagementListItem.TYPE_ADDED_PRINTERS_WORDS).get());
        if (IS_DECTECTING_ADDED) {
            list.add(new ManagementListItem.Builder(ManagementListItem.TYPE_LOADING).get());
        } else if (!addedList.isEmpty()) {
            for (PrinterItem printerItem : addedList) {
                ManagementListItem item
                      = new ManagementListItem.Builder(ManagementListItem.TYPE_ADDED_PRINTER).get();
                item.setPrinteritem(printerItem);
                list.add(item);
            }
        } else {
            list.add(new ManagementListItem.Builder(ManagementListItem.TYPE_EMPTY).get());
        }

        list.add(new ManagementListItem.Builder(ManagementListItem.TYPE_ADDED_ENDLINE).get());
        mListItem.add(new ManagementListItem
                .Builder(ManagementListItem.TYPE_LOCAL_PRINTER_WORDS).get());

        if (IS_DECTECTING) {
            list.add(new ManagementListItem.Builder(ManagementListItem.TYPE_LOADING).get());
        } else if (!detectedList.isEmpty()) {
            for (PrinterItem printerItem : detectedList) {
                ManagementListItem item
                      = new ManagementListItem.Builder(ManagementListItem.TYPE_LOCAL_PRINTER).get();
                item.setPrinteritem(printerItem);
                list.add(item);
            }
        } else {
            list.add(new ManagementListItem.Builder(ManagementListItem.TYPE_EMPTY).get());
        }

        notifyDataSetChanged();

    }


    public void refreshAddedPrinters() {

        if (IS_DECTECTING_ADDED) {
            return;
        }

        IS_DECTECTING_ADDED = true;

        new ListAddedTask<Void, Void>() {
            @Override
            protected void onPostExecute(List<PrinterItem> printerItems) {

                mAddedList.clear();
                mAddedList.addAll(printerItems);

                IS_DECTECTING_ADDED = false;
                showList();
            }
        }.start();

        showList();

    }

    public void startDetecting() {

        if (IS_DECTECTING) {
            Toast.makeText(mContext, R.string.searching, Toast.LENGTH_SHORT).show();
            return;
        }

        SearchPrintersTask<Void, Void> task = new SearchPrintersTask<Void, Void>() {
            @Override
            protected void onPostExecute(List<PrinterItem> printerItems) {

                mDetectedList.clear();
                mDetectedList.addAll(printerItems);

                IS_DECTECTING = false;
                showList();
                Toast.makeText(mContext, R.string.search_finished, Toast.LENGTH_SHORT).show();
            }
        };

        task.start();
        IS_DECTECTING = true;
        showList();

    }

}
