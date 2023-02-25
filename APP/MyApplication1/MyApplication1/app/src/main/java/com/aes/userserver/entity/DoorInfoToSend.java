package com.aes.userserver.entity;

import lombok.*;

import java.io.Serializable;

/**
 * Project Name userserver
 * Packege Name com.aes.userserver.entity
 * Class Name DoorInfoTosend
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/4/15 11:34
 */
@Getter
@NoArgsConstructor

@Setter(AccessLevel.PUBLIC)
@ToString
public class DoorInfoToSend implements Serializable {

    private static final long serialVersionUID = -7386787472032103564L;

    /**
     * 主键
     *
     * isNullAble:0
     */
    private String doorId;

    /**
     *
     * isNullAble:1
     */
    private String address;

    /**
     *
     */
    private String doorStatus;
    private String userPrivilege;


}
