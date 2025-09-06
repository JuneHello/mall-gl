package com.siro.common.constant;

/**
 * @author starsea
 * @date 2022-05-12
 */
public enum WareStatusEnum {

    LOCK_WARE(1,"锁定"),
    UNLOCK_WARE(2,"解锁"),
    REDUCE_WARE(3,"扣减");

    private Integer code;
    private String msg;

    WareStatusEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
