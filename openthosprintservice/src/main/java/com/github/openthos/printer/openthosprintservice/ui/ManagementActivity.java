package com.github.openthos.printer.openthosprintservice.ui;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.openthos.printer.openthosprintservice.APP;
import com.github.openthos.printer.openthosprintservice.R;
import com.github.openthos.printer.openthosprintservice.model.DriveGsFoo2zjsItem;
import com.github.openthos.printer.openthosprintservice.model.DriveGsFoo2zjsItemHelper;
import com.github.openthos.printer.openthosprintservice.model.PrinterItem;
import com.github.openthos.printer.openthosprintservice.model.PrinterItemHelper;
import com.github.openthos.printer.openthosprintservice.ui.adapter.ManagementAdapter;
import com.github.openthos.printer.openthosprintservice.ui.adapter.ManagementListItem;
import com.github.openthos.printer.openthosprintservice.ui.fragment.ConfigPrinterDialogFragment;
import com.github.openthos.printer.openthosprintservice.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class ManagementActivity extends BaseActivity {

    private boolean IS_DETECTING = false;       //是否正在检测新打印机
    private ListView listview;
    private ManagementAdapter adapter;
    private final List<ManagementListItem> listItem = new ArrayList<>();;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        LogUtils.d(TAG, "onNewIntent()");
        handlerIntent(getIntent());
    }

    private void init(){


        setContentView(R.layout.activity_management);
        listview = (ListView)findViewById(R.id.listView);
        adapter = new ManagementAdapter(this, listItem);
        adapter.initList();
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ManagementListItem item = adapter.getItem(position);
                LogUtils.d(TAG, "onItemClick -> " + item.toString());
                if(item.getType() == ManagementListItem.TYPE_ADDED_PRINTER){
                    showConfigDialog(item);
                }else if(item.getType() == ManagementListItem.TYPE_LOCAL_PRINTER){
                    showAddLocalDialog(item);
                }
            }
        });
    }

    private void showConfigDialog(ManagementListItem item) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(ConfigPrinterDialogFragment.ITEM);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = ConfigPrinterDialogFragment.newInstance(item.getPrinteritem());
        newFragment.show(ft, ConfigPrinterDialogFragment.ITEM);
    }

    private void showAddLocalDialog(final ManagementListItem deviceItem) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        final EditText text = new EditText(this);
        text.setText(deviceItem.getDeviceItem().getProductName());
        TextView tips = new TextView(this);

        tips.setText(R.string.the_printer_is_untested);

        for(int i=0; i < deviceItem.getDeviceItem().getInterfaceCount(); i++ ){
            // InterfaceClass 7 代表打印机，目前是写死的，只能支持HP P1108打印机，其他打印机提示未测试
            if(deviceItem.getDeviceItem().getInterface(i).getInterfaceClass() == 7
                    && deviceItem.getDeviceItem().getVendorId() == 1008 && deviceItem.getDeviceItem().getProductId() == 42 ){
                tips.setText(R.string.the_printer_can_be_added_directly);
            }
        }

        layout.addView(text);
        layout.addView(tips);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.add_a_local_printer)
                .setView(layout)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PrinterItemHelper helper = new PrinterItemHelper();
                        PrinterItem item = new PrinterItem(deviceItem.getDeviceItem());
                        if(!text.getText().toString().equals(""))
                            item.setNickName(text.getText().toString());
                        helper.insert(item);
                        helper.close();
                        DriveGsFoo2zjsItemHelper helper1 = new DriveGsFoo2zjsItemHelper();
                        DriveGsFoo2zjsItem ditem = new DriveGsFoo2zjsItem(item.getPrinterId(), DriveGsFoo2zjsItem.DEFAULT_GS, DriveGsFoo2zjsItem.DEFAULT_FOO2ZJS);
                        helper1.insert(ditem);
                        helper1.close();
                        Toast.makeText(ManagementActivity.this, R.string.add_success, Toast.LENGTH_SHORT).show();
                        adapter.refreshAddedprinters();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.create().show();
    }

    @Override
    protected String bindTAG() {
        return "ManagementActivity";
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.d(TAG, "onNewIntent()");
        handlerIntent(intent);
    }

    private void handlerIntent(Intent intent) {

        if(intent == null){
            return;
        }

        int task = intent.getIntExtra(APP.TASK, APP.TASK_DEFAULT);
        LogUtils.d(TAG, "Intent -> task = " + task);

        switch (task){
            case APP.TASK_ADD_NEW_PRINTER:
                detect_printers();
                break;
            case APP.TASK_REFRESH_ADDED_PRINTERS:
                adapter.refreshAddedprinters();
                break;
            case APP.TASK_DEFAULT:
                break;
        }
    }

    private void detect_printers() {
        // TODO: 2016/4/16 检测网络打印机时使用，本地打印机耗时短无需 IS_DETECTING
        if(IS_DETECTING){
            return;
        }

        //IS_DETECTING = true;

        adapter.startDetecting();

    }

    @Override
    protected void onResume() {
        super.onResume();
        APP.MANAGEMENT_ACTIVITY_ON_TOP = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        APP.MANAGEMENT_ACTIVITY_ON_TOP = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_management, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(ManagementActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if(id == R.id.action_system_print_service){
            Intent intent = new Intent(Settings.ACTION_PRINT_SETTINGS);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
