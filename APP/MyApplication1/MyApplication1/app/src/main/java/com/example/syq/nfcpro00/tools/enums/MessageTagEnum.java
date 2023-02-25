package com.example.syq.nfcpro00.tools.enums;

import lombok.Getter;

/**
 * Class Name MessageTagEnum
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/3/30
 */
public enum MessageTagEnum {
    /**
     * TAGs
     */
    NEW_USER("100001"),
    LOGIN_IN("100010"),
    OPEN_DOOR("100011"),
    SHOW_PERSON_INFO("100101"),
    USER_PRIVILEGE("100110"),
    CHAGE_USER_PRIVILEGE("100000"),
    DOOR_PRIVILEGE("100111"),
    CHAGE_DOOR_PRIVILEGE("100100"),
    REST_DOOR("010011");
    /**
     * 描述
     */
    @Getter
    private String desc;
    private MessageTagEnum(String s) {
        this.desc=s;
    }
}
