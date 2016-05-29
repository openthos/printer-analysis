package com.github.openthos.printer.localprint.ui.fragment;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.model.PrinterItem;
import com.github.openthos.printer.localprint.task.DeletePrinterTask;
import com.github.openthos.printer.localprint.task.PrintTask;
import com.github.openthos.printer.localprint.ui.AdvancedPrintOptionActivity;
import com.github.openthos.printer.localprint.ui.ManagementActivity;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by bboxh on 2016/4/16.
 */
public class ConfigPrinterDialogFragment extends DialogFragment {

    public static final String ITEM = "item";
    private boolean IS_DELETEING = false;
    private PrinterItem item;
    private Button button_cancel;
    private Button button_ok;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dialog_config_printer, container, false);

        //getDialog().setTitle(item.getNickName());
        Toolbar toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        //toolbar.setTitle(R.string.printer_setting);
        toolbar.setTitle("");
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

        TextView textView_printer_name = (TextView) v.findViewById(R.id.textView_printer_name);
        textView_printer_name.setText(item.getNickName());

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

    /**
     * 高级设置界面
     */
    private void tuning() {
        Intent intent = new Intent(getActivity(), AdvancedPrintOptionActivity.class);
        getActivity().startActivity(intent);
    }

    /**
     * 打印测试页
     */
    private void test_page() {
        PrintTask<Void> task = new PrintTask<Void>() {
            @Override
            protected void onPostExecute(String jobId) {
                if(jobId == null){
                    Toast.makeText(getActivity(), getResources().getString(R.string.print_error) + " " + ERROR, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), R.string.printing, Toast.LENGTH_SHORT).show();
                }

            }
        };

        Map<String, String> map = new HashMap<>();
        map.put(PrintTask.LP_PRINTER, item.getNickName());
        //map.put(PrintTask.LP_FILE, "/docu3.pdf");
        map.put(PrintTask.LP_FILE, "/usr/share/cups/data/testprint");

        task.start(map);
        Toast.makeText(getActivity(), R.string.start_printing, Toast.LENGTH_SHORT).show();
    }

    /**
     * 删除该打印机
     */
    private void delete() {

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.confirm_delete)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(IS_DELETEING){
                            Toast.makeText(getActivity(), R.string.deleting, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DeletePrinterTask<Void> task = new DeletePrinterTask<Void>() {
                            @Override
                            protected void onPostExecute(Boolean aBoolean) {
                                if(aBoolean){
                                    Toast.makeText(getActivity(), R.string.deleted_successfully, Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(getActivity(), R.string.delete_failed, Toast.LENGTH_SHORT).show();
                                }
                                IS_DELETEING = false;
                                dismiss();
                            }
                        };
                        task.start(item.getNickName());
                        IS_DELETEING = true;

                    }
                })
                .create().show();



    }

    private void save() {
        //// TODO: 2016/4/16 保存配置修改并退出 B5

    }

    @Override
    public void dismiss() {
        super.dismissAllowingStateLoss();           //avoid report : Can not perform this action after onSaveInstanceState
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
