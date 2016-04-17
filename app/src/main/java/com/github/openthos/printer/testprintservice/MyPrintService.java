package com.github.openthos.printer.testprintservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.print.PrintJobInfo;
import android.printservice.PrintDocument;
import android.printservice.PrintJob;
import android.printservice.PrintService;
import android.printservice.PrinterDiscoverySession;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class MyPrintService extends PrintService {

    private static final String TAG = "MyPrintService";

    @Override
    protected PrinterDiscoverySession onCreatePrinterDiscoverySession() {
        Log.d(TAG, "onCreatePrinterDiscoverySession()");
        return new MyPrintDiscoverySession(this);
    }

    @Override
    protected void onRequestCancelPrintJob(PrintJob printJob) {
        Log.d(TAG, "onRequestCancelPrintJob()");
        printJob.cancel();
    }

    @Override
    protected void onPrintJobQueued(PrintJob printJob) {
        Log.d(TAG, "onPrintJobQueued()");
        PrintJobInfo printjobinfo = printJob.getInfo();
        PrintDocument printdocument = printJob.getDocument();

        if (!printJob.isQueued()) {
            return;
        }
        printJob.start();

        String filename = "docu.pdf";
        File outfile = new File(this.getFilesDir(), filename);
        outfile.delete();

        FileInputStream file = new ParcelFileDescriptor.AutoCloseInputStream(printdocument.getData());
        //创建一个长度为1024的内存空间
        byte[] bbuf = new byte[1024];

        //用于保存实际读取的字节数
        int hasRead = 0;
        //使用循环来重复读取数据
        try {

            FileOutputStream outStream = new FileOutputStream(outfile);

            while ((hasRead = file.read(bbuf)) > 0) {

                //将字节数组转换为字符串输出
                System.out.print(new String(bbuf, 0, hasRead));
                outStream.write(bbuf);
            }

            outStream.close();


        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭文件输出流，放在finally块里更安全
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



        //这个方法无法传输，有问题
        /*
        try {
            FileInputStream inStream = new FileInputStream(printdocument.getData().getFileDescriptor());
            FileOutputStream outStream = new FileOutputStream(outfile);
            FileChannel in = inStream.getChannel();
            FileChannel out = outStream.getChannel();
            //long length = out.transferFrom(in, 0, in.size());
            long length = in.transferTo(0, in.size(), out);
            Log.d(TAG, "copy to docu.pdf length: " + length + " @" + this.getFilesDir());
            inStream.close();
            outStream.close();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        printJob.complete();

    }


}
