package com.mmrx.yunliao.model.exception;/**
 * Created by mmrx on 16/3/13.
 */

import com.mmrx.yunliao.model.Contant;

/**
 * 创建人: mmrx
 * 时间: 16/3/13下午2:50
 * 描述: 群发短信数据库实例为null的异常
 */
public class YlDBisNullExcetption extends Exception {

    public YlDBisNullExcetption(String msg){
        super(msg + " " + Contant.EXCEPTION_YLDB_IS_NULL);
    }
    public YlDBisNullExcetption(){
        super(Contant.EXCEPTION_YLDB_IS_NULL);
    }
}
