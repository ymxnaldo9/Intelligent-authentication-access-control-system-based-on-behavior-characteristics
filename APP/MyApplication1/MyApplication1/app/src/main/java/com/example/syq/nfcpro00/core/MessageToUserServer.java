package com.example.syq.nfcpro00.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.syq.nfcpro00.MainActivity;
import com.example.syq.nfcpro00.tools.MessageDecomposition;
import com.example.syq.nfcpro00.tools.compare.CompareHmac;
import com.example.syq.nfcpro00.tools.compare.CompareTime;
import com.example.syq.nfcpro00.tools.crypto.Aes;
import com.example.syq.nfcpro00.tools.crypto.SM3;
import com.example.syq.nfcpro00.tools.crypto.SM3Digest;
import com.example.syq.nfcpro00.tools.crypto.sha1.Sha1;
import com.example.syq.nfcpro00.tools.enums.MessageResultEnum;
import com.example.syq.nfcpro00.tools.helper.PreferenceHelper;

import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Project Name MyApplication1
 * Packege Name com.example.syq.nfcpro00.core
 * Class Name MessageToUserServer
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/4/14 18:51
 */

public class MessageToUserServer {
    private static final Logger log = LoggerFactory.getLogger(MessageToUserServer.class);
    private static final String URL_USER_SERVER = "http://192.168.0.129:8089/user/";
    private static String appPubKey,kdcPublKey,appPriKey,MAC ;

    private static String sm3digest(String msg){
        SM3Digest sm3 = new SM3Digest();
        msg = sm3.SM3encode(msg);
        return msg;
    }
    private static boolean init(Map map){
        appPubKey = (String) map.get("app_public_key");
        appPriKey = (String) map.get("app_private_key");
        kdcPublKey = (String) map.get("kdc_public_key");
        MAC = (String) map.get("app_IMEI");
        if (Strings.isNullOrEmpty(appPubKey)){
            log.error("appPubKey error",new NullPointerException());
            return false;
        }
        if (Strings.isNullOrEmpty(appPriKey)){
            log.error("appPubKey error",new NullPointerException());
            return false;
        }
        if (Strings.isNullOrEmpty(kdcPublKey)){
            log.error("appPubKey error",new NullPointerException());
            return false;
        }
        if (Strings.isNullOrEmpty(MAC)){
            log.error("appPubKey error",new NullPointerException());
            return false;
        }
        return true;
    }
    public static String userLogin(Context context, String loginPassword) throws Exception {
        if (!init(MainActivity.keyMap)){
            log.error("init error",new NullPointerException());
            return "Refuse";
        }
        SharedPreferences preferences = PreferenceHelper.getSharedPreferences(context);
        String userID = preferences.getString("userID", "");
        String kc = preferences.getString("kc", "");
        String sessionKey = MessageToKDC.getSessionKey(context);
        String aesDe = MessageDecomposition.getTime()+MAC+userID+ sm3digest(loginPassword);
        String aesEn = Aes.Encrypt(aesDe,sessionKey);
        String hmac = Sha1.HmacSHA1Encrypt(MessageToKDC.getAesMessageToUserServer()+aesEn,MAC);
        String urltemp = URL_USER_SERVER+"100010/100010"+hmac+MessageToKDC.getAesMessageToUserServer()+aesEn;
        OkHttpClient client1 = new OkHttpClient();
        Request request = new Request.Builder().url(urltemp).build();
        Response response = client1.newCall(request).execute();
        if (!response.isSuccessful()){
            throw new IOException("服务器端错误: " + response);
        }
        ResponseBody responseHeaders1 = response.body();
        String returnMessage = null;
        if (responseHeaders1 != null) {
            returnMessage = responseHeaders1.string();
        }
        if (returnMessage != null) {
            hmac = returnMessage.substring(6,46);
            aesEn = returnMessage.substring(46);
        }
        String hmacMake = Sha1.HmacSHA1Encrypt(aesEn, MAC);
        //HMAC匹配成功
        if (CompareHmac.compareHmac(hmac, hmacMake)) {

            aesDe = Aes.Decrypt(aesEn, sessionKey);
            if (aesDe != null) {
                if (CompareTime.compareTime(MessageDecomposition.getTime(), aesDe.substring(0, 10))) {
                    //匹配时间成功,返回sessionKey
                    if (aesDe.substring(10,17).equals(MessageResultEnum.NO.getDesc())){
                        log.error("login failed");
                        return "Refuse";
                    }
                    log.info("onLogin aes De==>{}",aesDe);
                    return aesDe.substring(10, 17);
                } else {
                    log.error("CompareTime error");
                    return "Refuse";
                }
            }
            else {
                log.error("onLogin aesDe is null");
                return "Refuse";
            }
        } else {
            log.error("CompareHmac error");
            return "Refuse";
        }
    }
    public static String newUser(Context context,String lopwd,String nickName,String openpwd)throws Exception {
        if (!init(MainActivity.keyMap)){
            log.error("init error",new NullPointerException());
            return "Refuse";
        }
        SharedPreferences preferences = PreferenceHelper.getSharedPreferences(context);
        String userID = preferences.getString("userID", "");
        String kc = preferences.getString("kc", "");
        String sessionKey = MessageToKDC.getSessionKey(context);

        String aesDe = MessageDecomposition.getTime()+MAC+userID+sm3digest(lopwd)+sm3digest(openpwd)+nickName;
        String aesEn = Aes.Encrypt(aesDe,sessionKey);
        String hmac = Sha1.HmacSHA1Encrypt(MessageToKDC.getAesMessageToUserServer()+aesEn,MAC);
        String urltemp1 = URL_USER_SERVER+"100001/"+"100001"+hmac+MessageToKDC.getAesMessageToUserServer()+aesEn;

        OkHttpClient client1 = new OkHttpClient();
        Request request1 = new Request.Builder().url(urltemp1).build();
        Response response1 = client1.newCall(request1).execute();
        if (!response1.isSuccessful()){
            throw new IOException("服务器端错误: " + response1);
        }
        ResponseBody responseHeaders1 = response1.body();
        String returnMessage = null;
        if (responseHeaders1 != null) {
            returnMessage = responseHeaders1.string();
        }
        if (returnMessage != null) {
            hmac = returnMessage.substring(6,46);
            aesEn = returnMessage.substring(46);
        }
        String hmacMake = Sha1.HmacSHA1Encrypt(aesEn, MAC);
        //HMAC匹配成功
        if (CompareHmac.compareHmac(hmac, hmacMake)) {
            aesDe = Aes.Decrypt(aesEn, sessionKey);
            if (aesDe != null) {
                if (CompareTime.compareTime(MessageDecomposition.getTime(), aesDe.substring(0, 10))) {
                    //匹配时间成功,返回sessionKey
                    if (aesDe.substring(10,17).equals(MessageResultEnum.NO.getDesc())){
                        log.error("new User failed");
                        return "Refuse";
                    }
                    return aesDe.substring(10, 17);
                } else {
                    return "Refuse";
                }
            }
            else {
                return "Refuse";
            }
        } else {
            return "Refuse";
        }
    }

    public static String getDoorInfo(Context context)throws Exception{
        if (!init(MainActivity.keyMap)){
            log.error("init error",new NullPointerException());
            return "Refuse";
        }
        SharedPreferences preferences = PreferenceHelper.getSharedPreferences(context);
        String userID = preferences.getString("userID", "");
        String kc = preferences.getString("kc", "");
        String sessionKey = MessageToKDC.getSessionKey(context);


        String aesDe = MessageDecomposition.getTime()+MAC+userID;
        String aesEn = Aes.Encrypt(aesDe,sessionKey);
        String hmac = Sha1.HmacSHA1Encrypt(MessageToKDC.getAesMessageToUserServer()+aesEn,MAC);
        String urltemp1 = URL_USER_SERVER+"100111/"+"100111"+hmac+MessageToKDC.getAesMessageToUserServer()+aesEn;

        OkHttpClient client1 = new OkHttpClient();
        Request request1 = new Request.Builder().url(urltemp1).build();
        Response response1 = client1.newCall(request1).execute();
        if (!response1.isSuccessful()){
            throw new IOException("服务器端错误: " + response1);
        }
        ResponseBody responseHeaders1 = response1.body();
        String returnMessage = null;
        if (responseHeaders1 != null) {
            returnMessage = responseHeaders1.string();
        }
        if (returnMessage != null) {
            hmac = returnMessage.substring(6,46);
            aesEn = returnMessage.substring(46);
        }
        String hmacMake = Sha1.HmacSHA1Encrypt(aesEn, MAC);
        //HMAC匹配成功
        if (CompareHmac.compareHmac(hmac, hmacMake)) {
            aesDe = Aes.Decrypt(aesEn, sessionKey);
            if (aesDe != null) {
                if (CompareTime.compareTime(MessageDecomposition.getTime(), aesDe.substring(0, 10))) {
                    //匹配时间成功,返回序列化的值（以解密）
                    return Aes.Decrypt(aesDe.substring(10),sessionKey);
                } else {
                    return "Refuse";
                }
            }
            else {
                return "Refuse";
            }
        } else {
            return "Refuse";
        }
    }
    public static String openDoor(Context context, String doorId, String strPassword)throws Exception{
        if (!init(MainActivity.keyMap)){
            log.error("init error",new NullPointerException());
            return "Refuse";
        }
        SharedPreferences preferences = PreferenceHelper.getSharedPreferences(context);
        String userID = preferences.getString("userID", "");
        String kc = preferences.getString("kc", "");
        String sessionKey = MessageToKDC.getSessionKey(context);


        String aesDe = MessageDecomposition.getTime()+MAC+userID+doorId+sm3digest(strPassword);
        String aesEn = Aes.Encrypt(aesDe,sessionKey);
        String hmac = Sha1.HmacSHA1Encrypt(MessageToKDC.getAesMessageToUserServer()+aesEn,MAC);
        String urltemp1 = URL_USER_SERVER+"100011/"+"100011"+hmac+MessageToKDC.getAesMessageToUserServer()+aesEn;

        OkHttpClient client1 = new OkHttpClient();
        Request request1 = new Request.Builder().url(urltemp1).build();
        Response response1 = client1.newCall(request1).execute();
        if (!response1.isSuccessful()){
            throw new IOException("服务器端错误: " + response1);
        }
        ResponseBody responseHeaders1 = response1.body();
        String returnMessage = null;
        if (responseHeaders1 != null) {
            returnMessage = responseHeaders1.string();
        }
        if (returnMessage != null) {
            hmac = returnMessage.substring(6,46);
            aesEn = returnMessage.substring(46);
        }
        String hmacMake = Sha1.HmacSHA1Encrypt(aesEn, MAC);
        //HMAC匹配成功
        if (CompareHmac.compareHmac(hmac, hmacMake)) {
            aesDe = Aes.Decrypt(aesEn, sessionKey);
            if (aesDe != null) {
                if (CompareTime.compareTime(MessageDecomposition.getTime(), aesDe.substring(0, 10))) {
                    //匹配时间成功,返回序列化的值（以解密）
                    String result = aesDe.substring(10,17);
                    if ("error!!".equals(result)){
                        return "Refuse";
                    }
                    //返回开门密钥
                    return aesDe.substring(17);
                } else {
                    return "Refuse";
                }
            }
            else {
                return "Refuse";
            }
        } else {
            return "Refuse";
        }
    }

    public static String show_Privilege(Context context)throws Exception{
        if (!init(MainActivity.keyMap)){
            log.error("init error",new NullPointerException());
            return "Refuse";
        }
        SharedPreferences preferences = PreferenceHelper.getSharedPreferences(context);
        String userID = preferences.getString("userID", "");
        String kc = preferences.getString("kc", "");
        String sessionKey = MessageToKDC.getSessionKey(context);


        String aesDe = MessageDecomposition.getTime()+MAC+userID;
        String aesEn = Aes.Encrypt(aesDe,sessionKey);
        String hmac = Sha1.HmacSHA1Encrypt(MessageToKDC.getAesMessageToUserServer()+aesEn,MAC);
        String urltemp1 = URL_USER_SERVER+"100110/"+"100110"+hmac+MessageToKDC.getAesMessageToUserServer()+aesEn;

        OkHttpClient client1 = new OkHttpClient();
        Request request1 = new Request.Builder().url(urltemp1).build();
        Response response1 = client1.newCall(request1).execute();
        if (!response1.isSuccessful()){
            throw new IOException("服务器端错误: " + response1);
        }
        ResponseBody responseHeaders1 = response1.body();
        String returnMessage = null;
        if (responseHeaders1 != null) {
            returnMessage = responseHeaders1.string();
        }
        if (returnMessage != null) {
            hmac = returnMessage.substring(6,46);
            aesEn = returnMessage.substring(46);
        }
        String hmacMake = Sha1.HmacSHA1Encrypt(aesEn, MAC);
        //HMAC匹配成功
        if (CompareHmac.compareHmac(hmac, hmacMake)) {
            aesDe = Aes.Decrypt(aesEn, sessionKey);
            if (aesDe != null) {
                if (CompareTime.compareTime(MessageDecomposition.getTime(), aesDe.substring(0, 10))) {
                    //匹配时间成功,返回序列化的值（以解密）
                    return Aes.Decrypt( aesDe.substring(10),sessionKey);
                } else {
                    return "Refuse";
                }
            }
            else {
                return "Refuse";
            }
        } else {
            return "Refuse";
        }
    }

    public static String show_User_Info(Context context)throws Exception{
        if (!init(MainActivity.keyMap)){
            log.error("init error",new NullPointerException());
            return "Refuse";
        }
        SharedPreferences preferences = PreferenceHelper.getSharedPreferences(context);
        String userID = preferences.getString("userID", "");
        String kc = preferences.getString("kc", "");
        String sessionKey = MessageToKDC.getSessionKey(context);


        String aesDe = MessageDecomposition.getTime()+MAC+userID;
        String aesEn = Aes.Encrypt(aesDe,sessionKey);
        String hmac = Sha1.HmacSHA1Encrypt(MessageToKDC.getAesMessageToUserServer()+aesEn,MAC);
        String urltemp1 = URL_USER_SERVER+"101000/"+"101000"+hmac+MessageToKDC.getAesMessageToUserServer()+aesEn;

        OkHttpClient client1 = new OkHttpClient();
        Request request1 = new Request.Builder().url(urltemp1).build();
        Response response1 = client1.newCall(request1).execute();
        if (!response1.isSuccessful()){
            throw new IOException("服务器端错误: " + response1);
        }
        ResponseBody responseHeaders1 = response1.body();
        String returnMessage = null;
        if (responseHeaders1 != null) {
            returnMessage = responseHeaders1.string();
        }
        if (returnMessage != null) {
            hmac = returnMessage.substring(6,46);
            aesEn = returnMessage.substring(46);
        }
        String hmacMake = Sha1.HmacSHA1Encrypt(aesEn, MAC);
        //HMAC匹配成功
        if (CompareHmac.compareHmac(hmac, hmacMake)) {
            aesDe = Aes.Decrypt(aesEn, sessionKey);
            if (aesDe != null) {
                if (CompareTime.compareTime(MessageDecomposition.getTime(), aesDe.substring(0, 10))) {
                    //匹配时间成功,返回序列化的值（以解密）
                    return Aes.Decrypt( aesDe.substring(10),sessionKey);
                } else {
                    return "Refuse";
                }
            }
            else {
                return "Refuse";
            }
        } else {
            return "Refuse";
        }
    }


    public static String addDoorToUser(Context context,String doorid,String userToChage,String privi,String openpassword)throws Exception{
        if (!init(MainActivity.keyMap)){
            log.error("init error",new NullPointerException());
            return "Refuse";
        }
        SharedPreferences preferences = PreferenceHelper.getSharedPreferences(context);
        String userID = preferences.getString("userID", "");
        String kc = preferences.getString("kc", "");
        String sessionKey = MessageToKDC.getSessionKey(context);


        String aesDe = MessageDecomposition.getTime()+MAC+userID+doorid+userToChage+privi+"newuser"+sm3digest(openpassword);
        String aesEn = Aes.Encrypt(aesDe,sessionKey);
        String hmac = Sha1.HmacSHA1Encrypt(MessageToKDC.getAesMessageToUserServer()+aesEn,MAC);
        String urltemp1 = URL_USER_SERVER+"100100/"+"100100"+hmac+MessageToKDC.getAesMessageToUserServer()+aesEn;

        OkHttpClient client1 = new OkHttpClient();
        Request request1 = new Request.Builder().url(urltemp1).build();
        Response response1 = client1.newCall(request1).execute();
        if (!response1.isSuccessful()){
            throw new IOException("服务器端错误: " + response1);
        }
        ResponseBody responseHeaders1 = response1.body();
        String returnMessage = null;
        if (responseHeaders1 != null) {
            returnMessage = responseHeaders1.string();
        }
        if (returnMessage != null) {
            hmac = returnMessage.substring(6,46);
            aesEn = returnMessage.substring(46);
        }
        String hmacMake = Sha1.HmacSHA1Encrypt(aesEn, MAC);
        //HMAC匹配成功
        if (CompareHmac.compareHmac(hmac, hmacMake)) {
            aesDe = Aes.Decrypt(aesEn, sessionKey);
            if (aesDe != null) {
                if (CompareTime.compareTime(MessageDecomposition.getTime(), aesDe.substring(0, 10))) {
                    //匹配时间成功,返回序列化的值（以解密）
                    return  aesDe.substring(10);
                } else {
                    return "Refuse";
                }
            }
            else {
                return "Refuse";
            }
        } else {
            return "Refuse";
        }
    }
    public static String addUserToDoor(Context context,String doorid,String userToChage,String privi,String openpassword)throws Exception{
        if (!init(MainActivity.keyMap)){
            log.error("init error",new NullPointerException());
            return "Refuse";
        }
        SharedPreferences preferences = PreferenceHelper.getSharedPreferences(context);
        String userID = preferences.getString("userID", "");
        String kc = preferences.getString("kc", "");
        String sessionKey = MessageToKDC.getSessionKey(context);


        String aesDe = MessageDecomposition.getTime()+MAC+userID+doorid+userToChage+privi+"newdoor"+sm3digest(openpassword);
        String aesEn = Aes.Encrypt(aesDe,sessionKey);
        String hmac = Sha1.HmacSHA1Encrypt(MessageToKDC.getAesMessageToUserServer()+aesEn,MAC);
        String urltemp1 = URL_USER_SERVER+"100100/"+"100100"+hmac+MessageToKDC.getAesMessageToUserServer()+aesEn;

        OkHttpClient client1 = new OkHttpClient();
        Request request1 = new Request.Builder().url(urltemp1).build();
        Response response1 = client1.newCall(request1).execute();
        if (!response1.isSuccessful()){
            throw new IOException("服务器端错误: " + response1);
        }
        ResponseBody responseHeaders1 = response1.body();
        String returnMessage = null;
        if (responseHeaders1 != null) {
            returnMessage = responseHeaders1.string();
        }
        if (returnMessage != null) {
            hmac = returnMessage.substring(6,46);
            aesEn = returnMessage.substring(46);
        }
        String hmacMake = Sha1.HmacSHA1Encrypt(aesEn, MAC);
        //HMAC匹配成功
        if (CompareHmac.compareHmac(hmac, hmacMake)) {
            aesDe = Aes.Decrypt(aesEn, sessionKey);
            if (aesDe != null) {
                if (CompareTime.compareTime(MessageDecomposition.getTime(), aesDe.substring(0, 10))) {
                    //匹配时间成功,返回序列化的值（以解密）
                    return  aesDe.substring(10);
                } else {
                    return "Refuse";
                }
            }
            else {
                return "Refuse";
            }
        } else {
            return "Refuse";
        }
    }
}
