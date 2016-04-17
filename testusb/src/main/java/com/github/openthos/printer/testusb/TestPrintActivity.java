package com.github.openthos.printer.testusb;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class TestPrintActivity extends AppCompatActivity {


    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final String TAG = "TestPrintActivity";
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(device != null){
                            //call method to set up device communication
                            //TestPrintActivity.this.device = device;
                            TestPrintActivity.this.openDevice();
                        }
                    }
                    else {
                        Log.d(TAG, "permission denied for device " + device);
                        TestPrintActivity.this.println("permission denied for device " + device);
                    }
                }
            }
        }
    };

    private TextView info_textView;
    private Button send_button;
    private UsbDevice device;
    private TextView data_textView;
    private UsbInterface usbInterface0;
    private UsbInterface usbInterface1;
    private ScrollView data_scrollView;
    private UsbEndpoint usbEndpointBULK;
    private UsbManager mUsbManager;
    private UsbDeviceConnection connection;
    private PendingIntent mPermissionIntent;
    private Runnable scrollRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_print);
        Intent intent = getIntent();
        device = (UsbDevice)intent.getParcelableExtra(CONSTENT.USB_DEVICE_ITEM);

        init();
    }

    private void init() {

        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        initView();
        setDevice();

        scrollRunnable = new Runnable(){

            @Override
            public void run() {
                data_scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        };

    }

    private void initView() {
        info_textView = (TextView)findViewById(R.id.info_textView);
        info_textView.setText("DeviceName " + device.getDeviceName() + "\nDeviceClass " + device.getDeviceClass()
                + "\nProductName " + device.getProductName() + "\nManufacturerName " + device.getManufacturerName()
                + "\nSerialNumber " + device.getSerialNumber());

        send_button = (Button)findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_data();
            }
        });
        send_button.setEnabled(false);

        data_scrollView = (ScrollView)findViewById(R.id.dada_scrollView);

        data_textView = (TextView)findViewById(R.id.data_textView);
        clear();
    }


    /**
     * 发送测试数据
     */
    private void send_data(){

        send_button.setEnabled(false);

        new Thread(new Runnable() {

            @Override
            public void run() {
                InputStream inStream = null;
                try {

                    inStream = TestPrintActivity.this.getAssets().open("docu11_lp0");

                    UIprintln("getAssets().open(\"docu11_lp0\")");

                    byte[] bbuf = new byte[500];

                    int hasRead = 0;


                    while( (hasRead = inStream.read(bbuf)) > 0 ){

                        UIprintln("hasRead -> " + hasRead);

                        if( connection.bulkTransfer(usbEndpointBULK, bbuf, hasRead, 0) < 0 ){
                            UIprintln("SEND DATA ERROR!");
                            return;
                        }

                    }

                    UIprintln("SEND SUCCESS!");

                    inStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    if(inStream == null) {
                        UIprintln("getAssets ERROR!");
                        return;
                    }
                    UIprintln("SEND ERROR!");
                }

                TestPrintActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        send_button.setEnabled(true);
                    }
                });

            }

            private void UIprintln(final CharSequence s) {
                TestPrintActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        println(s);
                    }
                });
            }
        }).start();

    }

    private void setDevice(){

        if(device.getInterfaceCount() != 2){
            println("GetInterface ERROR! ==> InterfaceCount --> " + device.getInterfaceCount());
            return;
        }
        if( (usbInterface0 = device.getInterface(0)) == null){
            println("GetInterface ERROR! ==> usbInterface0 --> null ");
            return;
        }

        println("getInterface(0) " + "getName() -> " + usbInterface0.getName());

        if( (usbInterface1 = device.getInterface(1)) == null){
            println("GetInterface ERROR! ==> usbInterface1 --> null ");
            return;
        }

        println("getInterface(1) " + "getName() -> " + usbInterface1 .getName());

        for(int i = 0 ; i < usbInterface0.getEndpointCount() ; i++){
            UsbEndpoint usbEndpoint = usbInterface0.getEndpoint(i);

            if(usbEndpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK){
                if(usbEndpoint.getDirection() == UsbConstants.USB_DIR_OUT){
                    usbEndpointBULK  = usbEndpoint;
                }
            }

            println("getEndpoint ==> " + i + " usbEndpoint.getType() --> " + usbEndpoint.getType() + " usbEndpoint.getDirection() --> " + usbEndpoint.getDirection());

        }

        if(usbEndpointBULK == null){
            println("getEndpoint ERROR! ==> NO USB_ENDPOINT_XFER_BULK");
            return;
        }

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        mUsbManager.requestPermission(device, mPermissionIntent);

    }

    private void openDevice(){
        connection = mUsbManager.openDevice(device);

        if(!connection.claimInterface(usbInterface0, true)){
            printf("claimInterface ERROR!");
            return;
        }

        send_button.setEnabled(true);
        println("OPEN SUCCESSFULLY!");

    }

    private void printf(CharSequence text){
        if(text == null){
            return;
        }
        data_textView.setText(data_textView.getText().toString() + text.toString());
    }

    private void println(CharSequence text){
        if(text == null){
            text = "";
        }
        printf(text + "\n");

        new Handler().postDelayed(scrollRunnable, 500);

    }

    private void clear(){
        data_textView.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mUsbReceiver);
    }

}