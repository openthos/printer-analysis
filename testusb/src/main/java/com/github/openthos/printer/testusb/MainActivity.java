package com.github.openthos.printer.testusb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView textview;
    private UsbManager manager;
    private ListView listview;
    private List<UsbDevice> usbItem;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        init();

    }

    private void init() {
        usbItem = new ArrayList<>();
        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        initView();
        refresh();
        USBreceiver();
    }

    /**
     * 监听USB HOST设备的插入和拔出
     */
    private void USBreceiver() {
        /*IntentFilter filter = new IntentFilter();
        filter.addAction("android.hardware.usb.action.USB_STATE");*/
        String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(new USBReceiver(), filter);
    }

    public class USBReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "intent.getAction() =>" + intent.getAction());
            Toast.makeText(MainActivity.this, "intent.getAction() =>" + intent.getAction(), Toast.LENGTH_SHORT).show();

            fab.callOnClick();

            if (intent.getExtras().getBoolean("connected")) {
                //do your stuff
                Log.d(TAG, "connected");
            }
        }
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
                Snackbar.make(view, "Refreshing!", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        textview = (TextView)findViewById(R.id.textview);
        listview = (ListView)findViewById(R.id.listView);

        listview.setAdapter(new BaseAdapter() {


            @Override
            public int getCount() {
                return usbItem.size();
            }

            @Override
            public UsbDevice getItem(int position) {
                return usbItem.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                UsbDevice item = usbItem.get(position);
                View view = convertView;
                if (view == null) // no view to re-use, create new
                    view = MainActivity.this.getLayoutInflater().inflate(R.layout.list_item, null);
                ((TextView)view.findViewById(R.id.item_textView)).setText("DeviceName " + item.getDeviceName() + "\nDeviceClass " + item.getDeviceClass()
                        + "\nProductName " + item.getProductName() + "\nManufacturerName " + item.getManufacturerName()
                        + "\nSerialNumber " + item.getSerialNumber());
                //((TextView)view.findViewById(R.id.item_textView)).setText(item.toString());
                ((Button)view.findViewById(R.id.item_button_select)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UsbDevice item = usbItem.get(position);
                        Intent intent = new Intent(MainActivity.this, TestPrintActivity.class);
                        intent.putExtra(CONSTENT.USB_DEVICE_ITEM, item);
                        startActivity(intent);
                    }
                });
                return view;
            }
        });
    }

    private void refresh() {
        textview.setText("");
        usbItem.clear();
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

        Log.d(TAG, "get device list  = " + deviceList.size());
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            Log.d(TAG, "device name = " + device.getDeviceName());
            /*textview.setText(textview.getText() + "DeviceName " +  device.getDeviceName() + "\nDeviceClass " + device.getDeviceClass()
                    + "\nProductName " + device.getProductName() + "\nManufacturerName " + device.getManufacturerName()
                    + "\nSerialNumber " + device.getSerialNumber() + "\n\n" );*/

                usbItem.add(device);


        }

        ((BaseAdapter)listview.getAdapter()).notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
