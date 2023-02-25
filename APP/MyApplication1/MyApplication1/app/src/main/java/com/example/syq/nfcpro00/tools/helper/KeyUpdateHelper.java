package com.example.syq.nfcpro00.tools.helper;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class Name KeyUpdate
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/2/26
 */
public class KeyUpdateHelper {
    private static final String key= "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int KEY_LENGTH = key.length();
    /**
     * 生成新的安全密钥，使用双随机机制
     * @param length 需要密钥得长度
     * @return
     */
    public static String getRandomString(int length ){
        String str = shuffleForSortingString(key);
        Random random = new Random();
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuffer.append(str.charAt(random.nextInt(KEY_LENGTH)));
        }
        return stringBuffer.toString();
    }
    /***
     * 将每次的密钥根都随机一下，使每次生成的密钥根都不同
     * @param s
     * @return
     */
    private static String shuffleForSortingString(@NonNull String s) {
        char[] c = s.toCharArray();
        List<Character> lst = new ArrayList<Character>();
        for (char aC : c) {
            lst.add(aC);
        }
        Collections.shuffle(lst);
        StringBuilder resultStr = new StringBuilder();
        for (Character aLst : lst) {
            resultStr.append(aLst);
        }
        return resultStr.toString();
    }
}
