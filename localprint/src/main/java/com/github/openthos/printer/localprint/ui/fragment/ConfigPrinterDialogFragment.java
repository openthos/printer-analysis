package com.github.openthos.printer.localprint.ui.fragment;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.support.annotation.Nullable;
import android.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.openthos.printer.localprint.APP;
import com.github.openthos.printer.localprint.R;
import com.github.openthos.printer.localprint.model.PrinterItem;
import com.github.openthos.printer.localprint.model.PrinterOptionItem;
import com.github.openthos.printer.localprint.task.DeletePrinterTask;
import com.github.openthos.printer.localprint.task.PrintTask;
import com.github.openthos.printer.localprint.task.QueryPrinterOptionsTask;
import com.github.openthos.printer.localprint.task.UpdatePrinterOptionsTask;
import com.github.openthos.printer.localprint.ui.AdvancedPrintOptionActivity;
import com.github.openthos.printer.localprint.ui.ManagementActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by bboxh on 2016/4/16.
 */
public class ConfigPrinterDialogFragment extends DialogFragment {

    public static final String ITEM = "item";
    private boolean UPDATING = false;
    private boolean IS_DELETEING = false;
    private PrinterItem item;
    private Button button_cancel;
    private Button button_ok;
    private PrinterOptionItem optionItem;
    private ArrayAdapter<String> spinner_color_adapter;
    private ArrayAdapter<String> media_size_adapter;
    private ArrayAdapter<String> spinner_duplex_adapter;
    private Spinner spinner_media_size;
    private Spinner spinner_color_mode;
    private Spinner spinner_duplex_mode;

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

        spinner_media_size = (Spinner) v.findViewById(R.id.spinner_media_size);
        spinner_color_mode = (Spinner) v.findViewById(R.id.spinner_color_mode);
        spinner_duplex_mode = (Spinner) v.findViewById(R.id.spinner_duplex_mode);   //暂时不处理

        initData();

        return v;
    }

    private void initData() {
        QueryPrinterOptionsTask<Void> task = new QueryPrinterOptionsTask<Void>(){
            @Override
            protected void onPostExecute(PrinterOptionItem printerOptionItem) {

                optionItem = printerOptionItem;

                media_size_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, optionItem.getMediaSizeCupsList()){
                    @Override
                    public String getItem(int position) {
                        return optionItem.getMediaSizeList().get(position).getId();
                    }
                };

                spinner_color_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, optionItem.getColorModeCupsList()){
                    @Override
                    public String getItem(int position) {
                        String colorMode = getResources().getString(R.string.black_white);
                        if(optionItem.getColorModeList().get(position) == PrintAttributes.COLOR_MODE_COLOR){
                            colorMode = getResources().getString(R.string.color);
                        }
                        return colorMode;
                    }
                };

                spinner_duplex_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, new String[]{getString(R.string.no_double_side)});

                spinner_media_size.setAdapter(media_size_adapter);
                spinner_color_mode.setAdapter(spinner_color_adapter);
                spinner_duplex_mode.setAdapter(spinner_duplex_adapter);
            }
        };

        task.start(item.getNickName());
    }

    /**
     * 高级设置界面
     */
    private void tuning() {
        Intent intent = new Intent(getActivity(), AdvancedPrintOptionActivity.class);
        getActivity().startActivity(intent);
        // TODO: 2016/5/31 intent高级设置传参
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

        if(UPDATING){
            Toast.makeText(getActivity(), R.string.updating, Toast.LENGTH_SHORT).show();
            return;
        }

        UpdatePrinterOptionsTask<Void> task = new UpdatePrinterOptionsTask<Void>() {

            @Override
            protected void onPostExecute(Boolean aBoolean) {

                UPDATING = false;

                if(aBoolean){
                    Toast.makeText(getActivity(), R.string.update_success, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), getString(R.string.update_failed) + " " + ERROR, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String getPrinter() {
                return item.getNickName();
            }
        };
        task.start(optionItem);
        UPDATING = true;
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
        item = getArguments().getParcelable(ITEM);
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
