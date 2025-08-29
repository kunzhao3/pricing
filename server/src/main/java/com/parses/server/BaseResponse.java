package com.parses.server;

import java.io.Serializable;

public class BaseResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String respCode;
    private String msg;

    public BaseResponse() {
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRespCode() {
        return this.respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public boolean isSuccess() {
        return "1000".equals(this.respCode);
    }
}
