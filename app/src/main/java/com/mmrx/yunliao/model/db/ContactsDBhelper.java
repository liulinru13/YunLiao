package com.mmrx.yunliao.model.db;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * Created by mmrx on 16/4/7.
 * 用于获取联系人数据的辅助类
 */
public class ContactsDBhelper {

    private final String TAG = "ContactsDBhelperLog";

    private static ContactsDBhelper ourInstance = new ContactsDBhelper();

    public static ContactsDBhelper getInstance() {
        return ourInstance;
    }

    private ContactsDBhelper() {
    }

    /**
     * 根据电话号码来获取显示名称
     * @param context
     * @param address
     * @return
     */
    public String getPeopleNameFromPerson(Context context,String address){
        if(address == null || address == ""){
            return "( no address )\n";
        }

        String strPerson = "null";
        String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        Uri uri_Person = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, address);  // address 手机号过滤
        Cursor cursor = context.getContentResolver().query(uri_Person, projection, null, null, null);

        if(cursor.moveToFirst()){
            int index_PeopleName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String strPeopleName = cursor.getString(index_PeopleName);
            strPerson = strPeopleName;
        }
        cursor.close();

        return strPerson;
    }

    /**
     * 通过手机号来获取联系人的头像
     * @param context
     * @param address
     * @return
     */
    public byte[] getContactIconFromAddress(Context context,String address){
        byte[] iconBytes = null;

        if(address == null || address == ""){
            return iconBytes;
        }

        String[] projection = new String[] {ContactsContract.Data.DATA15};
        Uri uri_addr = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, address);  // address 手机号过滤
        Cursor cursor = context.getContentResolver().query(uri_addr, projection, null, null, null);

        if(cursor.moveToFirst()){
            iconBytes = cursor.getBlob(cursor.getColumnIndex(ContactsContract.Data.DATA15));
        }
        return iconBytes;
    }


}
