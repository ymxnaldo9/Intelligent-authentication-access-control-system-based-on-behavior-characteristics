package com.example.syq.nfcpro00.tools.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Strings;

import java.util.List;

/**
 * Class Name PrivilegeEnum
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/3/31
 */
@Slf4j
public enum  PrivilegeEnum {
    /**
     * 权限
     */
    ROOT("ROOT"),
    TEMP("TEMP"),
    COMM("COMM");
    /**
     * 描述
     */
    @Getter
    private String desc;
    public static PrivilegeEnum whatIsTheString(String s){
        log.info("被判别的字符串是{}",s);
        if (Strings.isNullOrEmpty(s)){
            return null;
        }
        if (s.length()!=4){
            return null;
        }
        switch (s){
            case "ROOT":return PrivilegeEnum.ROOT;
            case "TEMP":return PrivilegeEnum.TEMP;
            case "COMM":return PrivilegeEnum.COMM;
            default:return null;
        }
    }

    private PrivilegeEnum(String s) {
        this.desc=s;
    }
}
