package com.mmrx.yunliao.presenter.util;/**
 * Created by mmrx on 16/4/20.
 */

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;

/**
 * 创建人: mmrx
 * 时间: 16/4/20下午2:37
 * 描述: 解码编码工具类
 */
public class EncodeDecodeUtil {

    private final String ENC = "UTF-8";
    private static EncodeDecodeUtil instance = new EncodeDecodeUtil();

    private EncodeDecodeUtil(){

    }

    public static EncodeDecodeUtil getInstance(){
        return instance;
    }

    /**
     * 解密
     * @param msg
     * @return
     */
    public static String decode(String msg)
    {
        try
        {
            String name = new String();
            java.util.StringTokenizer st=new java.util.StringTokenizer(msg,"%");
            while (st.hasMoreElements()) {
                int asc =  Integer.parseInt((String)st.nextElement()) - 27;
                name = name + (char)asc;
            }

            return name;
        }catch(Exception e)
        {
            e.printStackTrace() ;
            return null;
        }
    }

    /**
     * 加密
     * @param msg
     * @return
     */
    public String encode(String msg)
    {
        try
        {
            byte[] _ssoToken = msg.getBytes("ISO-8859-1");
            String name = new String();
            // char[] _ssoToken = ssoToken.toCharArray();
            for (int i = 0; i < _ssoToken.length; i++) {
                int asc = _ssoToken[i];
                _ssoToken[i] = (byte) (asc + 27);
                name = name + (asc + 27) + "%";
            }
            return name;
        }catch(Exception e)
        {
            e.printStackTrace() ;
            return null;
        }
    }


}
