package com.github.openthos.printer.openthosprintservice.util;

import com.github.openthos.printer.openthosprintservice.APP;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by bboxh on 2016/4/12.
 */
public class FileUtils {


    private static final String TAG = "FileUtils";

    /**
     * 解压ZIP文件
     *
     * @param zippath
     * @param outzippath
     */
    public static void UnZipFile(String zippath, String outzippath) throws IOException {
            File file = new File(zippath);
            ZipInputStream zipInput = new ZipInputStream(new FileInputStream(file));
            UnZipZipInputStream(zipInput, outzippath);
    }

    public static void UnZipInputStream(InputStream open, String outzippath) throws IOException {
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(open));

        UnZipZipInputStream(zis, outzippath);
    }

    public static void UnZipZipInputStream(ZipInputStream zipInput, String outzippath) throws IOException {

        File outFile;
        ZipEntry entry;
        InputStream input = null;
        OutputStream output = null;

        try {

            while ((entry = zipInput.getNextEntry()) != null) {
                LogUtils.d(TAG, "unzip-> " + entry.getName());
                outFile = new File(outzippath + File.separator + entry.getName());
                if (!outFile.getParentFile().exists()) {
                    outFile.getParentFile().mkdir();
                }

                if(entry.isDirectory()){

                    if(!outFile.exists()){
                        outFile.mkdir();
                    }
                    continue;
                }

                if (!outFile.exists()) {
                    outFile.createNewFile();
                }else{
                    outFile.delete();
                    outFile.createNewFile();
                }

                output = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int count;
                while ((count = zipInput.read(buffer)) != -1) {
                    output.write(buffer, 0, count);
                }
            }

        }finally {
            if(input != null){
                input.close();
            }
            if(output != null){
                output.close();
            }
        }
    }

    /**
     * 获得待打印文件的绝对路径
     * @return
     * @param s 根据s值自动生成文件名
     */
    public static String getDocuFilePath(String s){
        return APP.getApplicatioContext().getFilesDir().getAbsolutePath() + "/" + s + "_" + APP.DOCU_FILE ;
    }

    public static String getComponentPath(){
        return APP.getApplicatioContext().getFilesDir().getAbsolutePath() + "/componment";
    }

}
