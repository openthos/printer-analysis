package com.github.openthos.printer.openthosprintservice.ui.fragment;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.openthos.printer.openthosprintservice.APP;
import com.github.openthos.printer.openthosprintservice.R;
import com.github.openthos.printer.openthosprintservice.model.DriveGsFoo2zjsItemHelper;
import com.github.openthos.printer.openthosprintservice.model.PrinterItem;
import com.github.openthos.printer.openthosprintservice.model.PrinterItemHelper;
import com.github.openthos.printer.openthosprintservice.ui.ManagementActivity;

/**
 * Created by bboxh on 2016/4/16.
 */
public class ConfigPrinterDialogFragment extends DialogFragment {

    public static final String ITEM = "item";
    private PrinterItem item;
    private Button button_cancel;
    private Button button_ok;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dialog_config_printer, container, false);

        //getDialog().setTitle(item.getNickName());
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle(item.getNickName());
        toolbar.inflateMenu(R.menu.menu_config_printer_dialog);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.action_delete){
                    delete();
                }else if(item.getItemId() == R.id.action_test_page) {
                    test_page();
                }else if(item.getItemId() == R.id.action_tuning){
                    tuning();
                }

                return true;
            }
        });

        button_cancel = (Button) v.findViewById(R.id.button_cancel);
        button_ok = (Button) v.findViewById(R.id.button_ok);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigPrinterDialogFragment.this.dismiss();
            }
        });
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        return v;
    }

    private void tuning() {

    }

    private void test_page() {

    }

    /**
     * 删除该打印机
     */
    private void delete() {

        DriveGsFoo2zjsItemHelper helper1 = new DriveGsFoo2zjsItemHelper();
        helper1.delete(item.getPrinterId());
        helper1.close();

        PrinterItemHelper helper = new PrinterItemHelper();
        helper.delete(item.getPrinterId());
        helper.close();

        dismiss();
    }

    private void save() {
        // 2016/4/16 保存配置修改并退出
        dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Intent intent = new Intent(getActivity(), ManagementActivity.class);
        intent.putExtra(APP.TASK, APP.TASK_REFRESH_ADDED_PRINTERS);
        getActivity().startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = (PrinterItem) getArguments().getParcelable(ITEM);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

    }

    public static ConfigPrinterDialogFragment newInstance(PrinterItem item) {
        ConfigPrinterDialogFragment f = new ConfigPrinterDialogFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable(ITEM, item);
        f.setArguments(args);
        return f;
    }

}
