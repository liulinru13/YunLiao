package com.mmrx.yunliao.presenter.util;

import com.mmrx.yunliao.model.bean.contacts.ContactsBean;
import com.mmrx.yunliao.model.bean.sms.SmsBean;
import com.mmrx.yunliao.model.bean.sms.SmsThread;
import com.mmrx.yunliao.model.bean.sms.SmsThreadBean;
import com.thoughtworks.xstream.XStream;

import java.util.List;

/**
 * Created by mmrx on 16/5/2.
 * xml写工具
 */
public class XmlWritter {

    public XmlWritter() {
    }

    public String getXmlFormatStringSms(List<SmsThread> list){
        XStream xstream = new XStream();
        xstream.alias("smsThread", SmsThread.class);
        xstream.alias("SmsThreadBean", SmsThreadBean.class);
        xstream.alias("SmsBean", SmsBean.class);
        String xml = xstream.toXML(list);
        return xml;
    }

    public String getXmlFormatStringContacts(List<ContactsBean> list){
        XStream xstream = new XStream();
        xstream.alias("ContactsBean", ContactsBean.class);
        String xml = xstream.toXML(list);
        return xml;
    }
}
