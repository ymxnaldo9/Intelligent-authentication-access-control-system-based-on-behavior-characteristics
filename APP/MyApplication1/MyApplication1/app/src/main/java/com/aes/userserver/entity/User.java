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
public class User implements Serializable {

    private static final long serialVersionUID = 1522325240246L;


    /**
    * 主键
    * 
    * isNullAble:0
    */
    private String clientId;

    /**
    * 
    * isNullAble:0
    */
    private String clientName;

    /**
    * 
    * isNullAble:0
    */
    private String sm3LoginPassword;

    /**
    * 
    * isNullAble:1
    */
    private String openPassword;

}
