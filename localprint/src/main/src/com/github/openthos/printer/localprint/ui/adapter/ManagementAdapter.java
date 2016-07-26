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

    private int mLocalPrinterPosition = -1;
    private int mNetPrinterPosition = -1;

    private List<PrinterItem> mAddedList = new ArrayList<>();

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
                    //ADDED_ENDLINE holder3 = (ADDED_ENDLINE) item.getViewHolder();
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
                    Button button_add_net_printers = (Button)convertView.findViewById(
                                                                 R.id.button_add_net_printers);
                    button_add_net_printers.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, ManagementActivity.class);
                            intent.putExtra(APP.TASK,APP.TASK_ADD_NEW_NET_PRINTER);
                            mContext.startActivity(intent);
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
                setAddedPrinters(mAddedList);
                IS_DECTECTING_ADDED = false;
            }
        }.start();

        setAddedPrinters(null);

    }

    private void setAddedPrinters(List<PrinterItem> list) {
        removeSub(mListItem, 1, mLocalPrinterPosition - 1);
        mLocalPrinterPosition = 2;

        //null represent searching event is being executed
        if (list == null) {
            mListItem.add(mLocalPrinterPosition++ - 1
                    , new ManagementListItem.Builder(ManagementListItem.TYPE_LOADING).get());
        } else if (!list.isEmpty()) {
            for (PrinterItem printerItem : list) {
                ManagementListItem item
                      = new ManagementListItem.Builder(ManagementListItem.TYPE_ADDED_PRINTER).get();
                item.setPrinteritem(printerItem);
                mListItem.add(mLocalPrinterPosition++ - 1, item);
            }
        } else {
            mListItem.add(mLocalPrinterPosition++ - 1
                    , new ManagementListItem.Builder(ManagementListItem.TYPE_EMPTY).get());
        }

        notifyDataSetChanged();
    }

    /**
     * Get added printers
     *
     * @return null represent this time is in retrieving process
     */
    private List<PrinterItem> getAddedPrinters() {

        return null;
    }

    public void initList() {

        mListItem.add(new ManagementListItem
                              .Builder(ManagementListItem.TYPE_ADDED_PRINTERS_WORDS).get());
        refreshAddedPrinters();

        /*List<PrinterItem> list = getAddedPrinters();

        if (list == null) {
            mListItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_LOADING).get());
        } else if (list.isEmpty()) {
            mListItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_EMPTY).get());
        } else {
            for(PrinterItem printerItem: list) {
                ManagementListItem item
                    = new ManagementListItem.Builder(ManagementListItem.TYPE_ADDED_PRINTER).get();
                item.setPrinteritem(printerItem);
                mListItem.add(item);
            }
        }*/

        mListItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_ADDED_ENDLINE).get());
    }

    /**
     * show the detecting progress
     */
    public void startDetecting() {

        if (IS_DECTECTING) {
            removeSub(mListItem, mLocalPrinterPosition + 1, mNetPrinterPosition);
            mNetPrinterPosition = mLocalPrinterPosition + 1;
            mListItem.add(mNetPrinterPosition++
                    , new ManagementListItem.Builder(ManagementListItem.TYPE_LOADING).get());
        } else {
            IS_DECTECTING = true;
            mListItem.add(new ManagementListItem
                                  .Builder(ManagementListItem.TYPE_LOCAL_PRINTER_WORDS).get());
            mLocalPrinterPosition = mListItem.size() - 1;
            mListItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_LOADING).get());
            mListItem.add(new ManagementListItem
                                  .Builder(ManagementListItem.TYPE_NET_PRINTER_WORDS).get());
            mNetPrinterPosition = mListItem.size() - 1;
            mListItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_EMPTY).get());
            //mListItem.add(new ManagementListItem.Builder(ManagementListItem.TYPE_LOADING).get());
        }

        addLocalPrinter();

        LogUtils.d(TAG, "mLocalPrinterPosition -> " + mLocalPrinterPosition
                + " mNetPrinterPosition -> " + mNetPrinterPosition);

        this.notifyDataSetChanged();

    }

    public void addLocalPrinter() {


        SearchPrintersTask<Void, Void> task = new SearchPrintersTask<Void, Void>() {
            @Override
            protected void onPostExecute(List<PrinterItem> printerItems) {

                removeSub(mListItem, mLocalPrinterPosition + 1, mNetPrinterPosition);
                mNetPrinterPosition = mLocalPrinterPosition + 1;

                if (printerItems != null) {
                    for (PrinterItem p : printerItems) {
                        ManagementListItem item = new ManagementListItem
                                             .Builder(ManagementListItem.TYPE_LOCAL_PRINTER).get();
                        item.setPrinteritem(p);
                        mListItem.add(mNetPrinterPosition++, item);
                    }
                }

                if (mNetPrinterPosition == mLocalPrinterPosition + 1) {
                    mListItem.add(mNetPrinterPosition++
                            , new ManagementListItem.Builder(ManagementListItem.TYPE_EMPTY).get());
                }

                //Close the detecting flag
                mContext.setIS_DETECTING(false);
                notifyDataSetChanged();
                Toast.makeText(mContext, R.string.search_finished, Toast.LENGTH_SHORT).show();
            }
        };
        task.start();


    }

    public void removeSub(List<?> listItem, int i, int j) {

        LogUtils.d(TAG, "removeSub() i -> " + i + " j -> " + j);

        for (j--; i <= j; j--) {
            listItem.remove(j);
        }
    }


}
