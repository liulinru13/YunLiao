package com.mmrx.yunliao.model.bean.contacts;

/**
 * Created by mmrx on 16/4/7.
 * 联系人数据结构
 */
public class ContactsBean {
    private String displayName;//显示名称
    private String[] address;//电话号码数组
    private String[] addressTypes;//对应电话号码的类型

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String[] getAddress() {
        return address;
    }

    public void setAddress(String[] address) {
        this.address = address;
    }

    public String[] getAddressTypes() {
        return addressTypes;
    }

    public void setAddressTypes(String[] addressTypes) {
        this.addressTypes = addressTypes;
    }
}
