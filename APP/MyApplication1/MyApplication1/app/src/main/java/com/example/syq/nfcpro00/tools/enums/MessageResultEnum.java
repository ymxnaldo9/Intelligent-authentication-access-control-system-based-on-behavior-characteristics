package com.example.syq.nfcpro00.tools.enums;


import lombok.Getter;

/**
 * Class Name MessageResultEum
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/3/30
 */
public enum MessageResultEnum {
    /**
     * 结果枚举类
     */
    YES("success"),
    NO("error!!");
    /**
     * 描述
     */
    @Getter
    private String desc;
    private MessageResultEnum(String s) {
        this.desc=s;
    }
}
