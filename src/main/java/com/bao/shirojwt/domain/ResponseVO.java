package com.bao.shirojwt.domain;

public class ResponseVO {
    public Boolean success;
    public String message;
    public String code;

    public ResponseVO(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResponseVO(Boolean success, String message, String code) {
        this.success = success;
        this.message = message;
        this.code = code;
    }
}
