package com.github.openthos.printer.openthosprintservice.task;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.ParcelFileDescriptor;

import com.github.openthos.printer.openthosprintservice.APP;
import com.github.openthos.printer.openthosprintservice.model.PrinterItem;
import com.github.openthos.printer.openthosprintservice.model.PrinterItemHelper;
import com.github.openthos.printer.openthosprintservice.service.OpenthosPrintService;
import com.github.openthos.printer.openthosprintservice.util.FileUtils;
import com.github.openthos.printer.openthosprintservice.util.LogUtils;
import com.github.openthos.printer.openthosprintservice.util.TaskUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

/**
 * GsFoo2zjs 打印步骤
 * 1 拷贝文件到应用空间里     copyFile()
 * 2 执行gs命令             gs()
 * 3 执行foo2zjs命令        foo2zjs()
 * 4 发送数据到USB端口       send()
 *
 * Created by bboxh on 2016/4/16.
 */
public class PrintGsFoo2zjsTask extends BaseTask {

    private static final String ACTION_USB_PERMISSION = "com.github.openthos.printer.openthosprintservice.USB_PERMISSION";

    private final String jobId;
    private final ParcelFileDescriptor data;
    private final String printerLocalId;
    private String docu_file_path;
    private final StringBuffer  message = new StringBuffer();          //错误信息

    public PrintGsFoo2zjsTask(String TAG, String jobId, ParcelFileDescriptor data, String printerLocalId) {
        super(TAG + "-> PrintGsFoo2zjsTask");
        this.jobId = jobId;
        this.data = data;
        this.printerLocalId = printerLocalId;
    }


    /**
     *
     * @return  成功 失败
     */
    private boolean copyFile(){

        boolean flag = false;

        /*if (!printJob.isQueued()) {
            return;
        }*/

        //printJob.start();

        docu_file_path = FileUtils.getDocuFilePath(jobId.toString());

        File outfile = new File(docu_file_path);
        LogUtils.d(TAG, "copyfile ->" + docu_file_path);
        outfile.delete();

        FileInputStream file = new ParcelFileDescriptor.AutoCloseInputStream(data);
        //创建一个长度为1024的内存空间
        byte[] bbuf = new byte[1024];

        //用于保存实际读取的字节数
        int hasRead = 0;
        //使用循环来重复读取数据
        try {

            FileOutputStream outStream = new FileOutputStream(outfile);

            while ((hasRead = file.read(bbuf)) > 0) {

                //将字节数组转换为字符串输出
                //System.out.print(new String(bbuf, 0, hasRead));
                outStream.write(bbuf);

            }

            flag = true;
            LogUtils.d(TAG, "copyfile finished");
            outStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            message.append(" IOException ");
            flag = false;
        }finally {
            //关闭文件输出流，放在finally块里更安全
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //printJob.complete();

        return flag;

    }

    public boolean gs(){

        final boolean[] flag = {true};

        String command = "./gs -q -dBATCH -dSAFER -dQUIET -dNOPAUSE -sPAPERSIZE=a4 -r600x600 -sDEVICE=pbmraw -sOutputFile="+ docu_file_path+".pbm " + docu_file_path;
        LogUtils.d(TAG, "gs -> " + command);
        try {
            File file = new File(FileUtils.getComponentPath());
            final Process p = Runtime.getRuntime().exec(command, null, file);

            final Lock lock_in = new Lock();
            final Lock lock_error = new Lock();

            BaseTask BaeTaskIn = new BaseTask(new String(TAG + " gs-In")) {
                @Override
                public void run() {

                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    try {
                        while ((line = in.readLine()) != null) {
                            LogUtils.d(TAG, "input -> " + line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    synchronized (lock_in){
                        lock_in.notify();
                        lock_in.setFinish(true);
                    }


                }
            };

            BaseTask BaeTaskError = new BaseTask(new String(TAG + " gs-Error")) {
                @Override
                public void run() {

                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line = null;
                    try {
                        while((line = in.readLine()) != null){
                            LogUtils.d(TAG, "error -> " + line);
                            //flag[0] = false;
                            message.append(" ErrorStream ");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    synchronized (lock_error){
                        lock_error.notify();
                        lock_error.setFinish(true);
                    }
                }
            };

            TaskUtils.execute(BaeTaskIn);

            TaskUtils.execute(BaeTaskError);

            synchronized (lock_in){
                if(!lock_in.isFinish()){
                    lock_in.wait();
                }
                LogUtils.d(TAG, "gsIn.wait() finish");
            }

            synchronized (lock_error){
                if(!lock_error.isFinish()){
                    lock_error.wait();
                }
                LogUtils.d(TAG, "gsError.wait() finish");
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            flag[0] = false;
            message.append(" InterruptedException ");
        }

        return flag[0];
    }

    private boolean foo2zjs() {
        final boolean[] flag = {true};

        String command = "./foo2zjs -z3 -p9 -r600x600 " + docu_file_path+".pbm " + " >"+ docu_file_path + ".data";
        LogUtils.d(TAG, "foo2zjs -> " + command);
        try {
            File file = new File(FileUtils.getComponentPath());
            final Process p = Runtime.getRuntime().exec(new String[]{"sh", "-c", command}, null, file);

            final Lock lock_in = new Lock();
            final Lock lock_error = new Lock();

            BaseTask BaeTaskIn = new BaseTask(TAG + "foo2zjsIn") {
                @Override
                public void run() {

                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = null;
                    try {
                        while ((line = in.readLine()) != null) {
                            LogUtils.d(TAG, "input -> " + line);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    synchronized (lock_in){
                        lock_in.notify();
                        lock_in.setFinish(true);
                    }

                }
            };

            BaseTask BaeTaskError = new BaseTask(TAG + "foo2zjsError") {
                @Override
                public void run() {

                    BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line = null;
                    try {
                        while((line = in.readLine()) != null){
                            LogUtils.d(TAG, "error -> " + line);
                            //flag[0] = false;          暂时屏蔽错误，因为会有系统WARNING，造成误判
                            message.append(" ErrorStream ");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    synchronized (lock_error){
                        lock_error.notify();
                        lock_error.setFinish(true);
                    }

                }
            };

            TaskUtils.execute(BaeTaskIn);

            TaskUtils.execute(BaeTaskError);

            synchronized (lock_in){
                if(!lock_in.isFinish()){
                    lock_in.wait();
                }
                LogUtils.d(TAG, "foo2zjs_in.wait()");
            }

            synchronized (lock_error){
                if(!lock_error.isFinish()){
                    lock_error.wait();
                }

                LogUtils.d(TAG, "foo2zjs_error.wait()");
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            flag[0] = false;
        }



        return flag[0];
    }

    private boolean send() {

        //查找到该打印机的USB接口

        PrinterItemHelper helper = new PrinterItemHelper();
        PrinterItem item = helper.query(Integer.parseInt(printerLocalId));

        UsbManager manager = (UsbManager) APP.getApplicatioContext().getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();

        final UsbDevice[] deviceitem = {null};

        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            if(device.getVendorId() == item.getVendorId() && device.getProductId() == item.getProductId()
                    && device.getSerialNumber().equals(item.getSerialNumber())){
                deviceitem[0] = device;
                break;
            }
        }

        if(deviceitem[0] == null){
            message.append(" deviceitem == null ");
            return false;
        }

        UsbInterface usbInterface = null;

        for(int i = 0; i < deviceitem[0].getInterfaceCount(); i++ ){
            // InterfaceClass 7 代表打印机
            if(deviceitem[0].getInterface(i).getInterfaceClass() == 7){
                usbInterface = deviceitem[0].getInterface(i);
                break;
            }
        }

        if(usbInterface == null){
            message.append(" usbInterface == null ");
            return false;
        }

        //打开设备

        final Lock usb_lock = new Lock();
        final boolean[] flag = {true};

        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (ACTION_USB_PERMISSION.equals(action)) {
                    synchronized (this) {
                        UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                        if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                            if(device != null){
                                flag[0] = true;
                                deviceitem[0] = device;
                            }
                        }else {
                            flag[0] = false;
                            message.append(" EXTRA_PERMISSION_GRANTED ERROR ");
                        }

                        synchronized (usb_lock){
                            usb_lock.notify();
                            usb_lock.setFinish(true);
                        }
                    }
                }
            }
        };
        APP.getApplicatioContext().registerReceiver(mUsbReceiver, filter);

        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(APP.getApplicatioContext(), 0, new Intent(ACTION_USB_PERMISSION), 0);
        manager.requestPermission(deviceitem[0], mPermissionIntent);

        synchronized (usb_lock){
            try {
                if(!usb_lock.isFinish()){
                    usb_lock.wait();
                }
                LogUtils.d(TAG, "usb_lock.wait() finish");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(!flag[0]){
            return false;
        }

        UsbDeviceConnection connection = manager.openDevice(deviceitem[0]);

        if(!connection.claimInterface(usbInterface, true)){
            message.append(" connection.claimInterface error ");
            return false;
        }

        LogUtils.d(TAG, "connection.claimInterface -> " + usbInterface.getName());

        UsbEndpoint usbEndpointBulkOut = null;
        UsbEndpoint usbEndpointBulkIn = null;


        for(int i = 0 ; i < usbInterface.getEndpointCount() ; i++){
            UsbEndpoint usbEndpoint = usbInterface.getEndpoint(i);

            if(usbEndpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK){
                if(usbEndpoint.getDirection() == UsbConstants.USB_DIR_OUT){
                    usbEndpointBulkOut = usbEndpoint;
                }else if(usbEndpoint.getDirection() == UsbConstants.USB_DIR_IN){
                    usbEndpointBulkIn = usbEndpoint;
                }
            }
        }

        if(usbEndpointBulkOut == null || usbEndpointBulkIn == null){
            message.append(" usbEndpointBulkOut ");
            return false;
        }

        //传送数据

        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(new File(docu_file_path + ".data"));
            byte[] bbuf = new byte[usbEndpointBulkOut.getMaxPacketSize() - 1];

            int hasRead = 0;

            while( (hasRead = inStream.read(bbuf)) > 0 ){

                LogUtils.d(TAG, "hasRead -> " + hasRead);

                if( connection.bulkTransfer(usbEndpointBulkOut, bbuf, hasRead, 0) < 0 ){
                    LogUtils.d(TAG, "SEND DATA ERROR!");
                    message.append(" SEND DATA ERROR ");
                    return false;
                }

            }

            LogUtils.d(TAG, "SEND SUCCESS!");

            inStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;
    }

    @Override
    public void run() {

        boolean flag = true;

        if(!copyFile()){
            flag = false;
            message.append("--copyFile Error");
        }else if(!gs()){
            flag = false;
            message.append("--gs Error");
        }else if(!foo2zjs()){
            flag = false;
            message.append("--foo2zjs Error");
        }else if(!send()){
            flag = false;
            message.append("--send Error");
        }

        new File(docu_file_path).delete();
        new File(docu_file_path+".pbm").delete();
        new File(docu_file_path+".data").delete();

        LogUtils.d(TAG, "message" + message.toString());

        Intent intent = new Intent(APP.getApplicatioContext(), OpenthosPrintService.class);
        intent.putExtra(APP.TASK, APP.TASK_JOB_RESULT);
        intent.putExtra(APP.RESULT, flag);
        intent.putExtra(APP.JOBID, jobId);
        intent.putExtra(APP.MESSAGE, message.toString());
        APP.getApplicatioContext().startService(intent);

    }

    class   Lock{
        boolean finish = false;

        public boolean isFinish() {
            return finish;
        }

        public void setFinish(boolean finish) {
            this.finish = finish;
        }
    }

}
