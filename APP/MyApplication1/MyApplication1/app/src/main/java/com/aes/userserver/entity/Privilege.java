package com.aes.userserver.entity;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
/**
*
*  @author Gorio
*/
@Getter
@NoArgsConstructor

@Setter(AccessLevel.PUBLIC)
@ToString
public class Privilege implements Serializable {

    private static final long serialVersionUID = 1522325236281L;


    /**
    * 主键
    * 
    * isNullAble:0
    */
    private String userid;

    /**
    * 主键
    * 
    * isNullAble:0
    */
    private String doorid;

    /**
    * 
    * isNullAble:0
    */
    private String privilege;

    /**
    * 
    * isNullAble:1,defaultVal:-2
    */
    private Integer temporaryCount = 99999;

    /**
    * 主键
    * 
    * isNullAble:0
    */
    private Integer count;


}
