package com.mmrx.yunliao.model.db;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.mmrx.yunliao.model.bean.contacts.ContactsBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mmrx on 16/4/7.
 * 用于获取联系人数据的辅助类
 */
public class ContactsDBhelper {

    private final String TAG = "ContactsDBhelperLog";
    private final Uri CONTACT_URL = ContactsContract.Contacts.CONTENT_URI;
    private final Uri PHONE_CONTACT_URL = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    //Contacts表中主键_id
    private final String CONTACTS_ID = ContactsContract.Contacts._ID;
    private final String DATA_CONTACTS_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    //Data表中字段data1
    private final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
    //Data表中字段data2,表示字段data1的数据含义
    private final String PHONE_TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;

//    private final String MIME_TYPE_PHONE_V2 = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE;

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
    public synchronized String getPeopleNameFromPerson(Context context,String address){
        if(address == null || address == ""){
            return "( no address )\n";
        }

        String strPerson = "null";
        String[] projection = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                PHONE_NUMBER};

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
    public synchronized byte[] getContactIconFromAddress(Context context,String address){
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

    /**
     * 获取所有联系人的名称及其电话号码
     * @param context
     * @return
     */
    public synchronized List<ContactsBean> getAllContactsList(Context context){
        List<ContactsBean> list = new ArrayList<ContactsBean>();
        //搜索Contacts表
        Cursor cursor = context.getContentResolver().query(CONTACT_URL,null,null,null,null);
        while (cursor.moveToNext()){
            ContactsBean bean = new ContactsBean();
            bean.setDisplayName(cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)));
            int contactsID = cursor.getInt(cursor
                    .getColumnIndex(CONTACTS_ID));
            String selection =  DATA_CONTACTS_ID + "=" + contactsID;
            //搜索Data表中的电话号
            Cursor numCursor = context.getContentResolver()
                    .query(PHONE_CONTACT_URL
                            , null, selection, null, null);
            ArrayList<String> numArr = new ArrayList<String>();
            ArrayList<String> numTypeArr = new ArrayList<String>();
            while (numCursor.moveToNext()){
                //电话号
                numArr.add(numCursor.getString(numCursor
                        .getColumnIndex(PHONE_NUMBER)));
                //电话号类型 工作.家庭...
                numTypeArr.add(numCursor.getString(numCursor
                        .getColumnIndex(PHONE_TYPE)));
            }
            bean.setAddress(numArr.toArray(new String[]{}));
            bean.setAddressTypes(numTypeArr.toArray(new String[]{}));
            list.add(bean);
        }

        return list;
    }


}
