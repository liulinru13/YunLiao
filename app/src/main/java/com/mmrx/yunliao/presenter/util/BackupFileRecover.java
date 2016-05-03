package com.mmrx.yunliao.presenter.util;

import android.content.Context;

import com.mmrx.yunliao.model.bean.contacts.ContactsBean;
import com.mmrx.yunliao.model.bean.sms.SmsThread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by mmrx on 16/5/2.
 * 备份文件恢复
 */
public class BackupFileRecover {
    private final String TAG = "BackupFileRecoverLog";
    private Context mContext;
    private BackupFileMaker.BackupType type;

    public BackupFileRecover(Context context,BackupFileMaker.BackupType type) {
        this.mContext = context;
        this.type = type;
    }

    public List<SmsThread> getSmsFromFile(){
        final String filePath = BackupFileMaker.getPath(mContext)
                + "/"
                + BackupFileMaker.smsFileName;
        String xml = readFromFile(filePath);
        XmlReader xmlReader = new XmlReader();
        return xmlReader.getXmlFormatStringSms(xml);
    }

    public List<ContactsBean> getContactsFromFile(){
        final String filePath = BackupFileMaker.getPath(mContext)
                + "/"
                + BackupFileMaker.conFileName;
        String xml = readFromFile(filePath);
        XmlReader xmlReader = new XmlReader();
        return xmlReader.getXmlFormatStringContacts(xml);
    }

    public  String readFromFile(String strFilePath)
    {
        String path = strFilePath;
        StringBuilder content = new StringBuilder(); //文件内容字符串
        //打开文件
        File file = new File(path);
        //如果path是传递过来的参数，可以做一个非目录的判断
        if (file.isDirectory())
        {
            L.e(TAG, "The File doesn't not exist.");
        }else if(!file.exists()){
            L.e(TAG, "The File doesn't not exist.");
        }
        else
        {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line;
                    //分行读取
                    while (( line = buffreader.readLine()) != null) {
                        content.append(line + "\n");
                    }
                    instream.close();
                }
            }
            catch (java.io.FileNotFoundException e)
            {
                L.e(TAG, "The File doesn't not exist.");
            }
            catch (IOException e)
            {
                L.e(TAG, e.getMessage());
            }
        }
        return content.toString();
    }
}
