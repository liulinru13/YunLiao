package com.mmrx.yunliao.model.exception;

/**
 * Created by mmrx on 16/5/6.
 */
public class MmsException  extends Exception  {

    public MmsException() {
    }

    public MmsException(String detailMessage) {
        super(detailMessage);
    }

    public MmsException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public MmsException(Throwable throwable) {
        super(throwable);
    }


}
