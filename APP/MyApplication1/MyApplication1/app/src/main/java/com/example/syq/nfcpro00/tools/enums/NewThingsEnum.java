package com.example.syq.nfcpro00.tools.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;

/**
 * Class Name NewThings
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/3/31
 */
@Slf4j
public enum NewThingsEnum {
    /**
     * TAGs
     */
    NEWDOOR("newdoor"),
    NEWUSER("newuser");
    /**
     * 描述
     */
    @Getter
    private String desc;
    private NewThingsEnum(String s) {
        this.desc=s;
    }
    public static NewThingsEnum whatIsTheString(String s){
        log.info("被判别的字符串是{}",s);
        if (Strings.isNullOrEmpty(s)){
            return null;
        }
        if (s.length()!=7){
            return null;
        }
        switch (s){
            case "newdoor":return NewThingsEnum.NEWDOOR;
            case "newuser":return NewThingsEnum.NEWUSER;
            default:return null;
        }
    }

}
