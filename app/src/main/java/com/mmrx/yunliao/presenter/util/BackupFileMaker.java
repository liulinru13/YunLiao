package com.mmrx.yunliao.presenter.util;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by mmrx on 16/5/2.
 * 备份文件生成
 */
public class BackupFileMaker {

    private static final String DIR = "backup";

    private String filePath;
    public static final String smsFileName = "backup_sms.xml";
    public static final String conFileName = "backup_con.xml";

    private Context mContext;
    private String mXml;
    private BackupType type;
    public enum BackupType{
        SMS,
        CON
    }


    public BackupFileMaker(Context mContext, String mXml,BackupType type) {
        this.mContext = mContext;
        this.mXml = mXml;
        this.type = type;
    }

    public BackupFileMaker(Context mContext) {
        this.mContext = mContext;
    }

    public void make(){
        if(type == BackupType.SMS) {
            make(smsFileName);
        }
        else{
            make(conFileName);
        }
    }

    private void make(String fileName){
        if(mContext == null || mXml == null)
            return;
        //存储目录在/data/data/appname目录下
        filePath = getPath(mContext);

        FileWriter fw = null;
        BufferedWriter bw = null;

        try{
            //创建文件夹
            File dir = new File(filePath);
            if(!dir.exists()){
                dir.mkdirs();
            }
            //创建文件
            filePath += "/"+fileName;
            File file = new File(filePath);
            if(file.exists()){
                //存在旧的文件,删除
                if(file.delete()){
                    file.createNewFile();
                }
            }
            fw = new FileWriter(filePath, true);//
            // 创建FileWriter对象，用来写入字符流
            bw = new BufferedWriter(fw); // 将缓冲对文件的输出
            bw.write(mXml); // 写入文件
            bw.newLine();
            bw.flush(); // 刷新该流的缓冲
            bw.close();
            fw.close();

        }catch (IOException ioe){
            ioe.printStackTrace();
        }finally {
            try {
                bw.close();
                fw.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
            }
        }


    }

    public static String getPath(Context context){
        return context.getApplicationContext()
                .getFilesDir().getAbsolutePath()
                + "/" + DIR;
    }
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public String getmXml() {
        return mXml;
    }

    public void setmXml(String mXml) {
        this.mXml = mXml;
    }
}
