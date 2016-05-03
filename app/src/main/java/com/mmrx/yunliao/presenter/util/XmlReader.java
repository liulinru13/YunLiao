package com.mmrx.yunliao.presenter.util;

import com.mmrx.yunliao.model.bean.contacts.ContactsBean;
import com.mmrx.yunliao.model.bean.sms.SmsBean;
import com.mmrx.yunliao.model.bean.sms.SmsThread;
import com.mmrx.yunliao.model.bean.sms.SmsThreadBean;
import com.thoughtworks.xstream.XStream;

import java.util.List;

/**
 * Created by mmrx on 16/5/2.
 * xml读工具
 */
public class XmlReader {

    public XmlReader() {
    }

    public List<SmsThread> getXmlFormatStringSms(String xml){
        XStream xstream = new XStream();
        List<SmsThread> arr = ( List<SmsThread>) xstream.fromXML(xml);
        return arr;
    }

    public List<ContactsBean> getXmlFormatStringContacts(String xml){
        XStream xstream = new XStream();
        List<ContactsBean> arr = ( List<ContactsBean>) xstream.fromXML(xml);
        return arr;
    }
}
