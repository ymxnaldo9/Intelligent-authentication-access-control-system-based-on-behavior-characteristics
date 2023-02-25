package com.example.syq.nfcpro00.core;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.syq.nfcpro00.MainActivity;
import com.example.syq.nfcpro00.tools.MessageDecomposition;
import com.example.syq.nfcpro00.tools.compare.CompareHmac;
import com.example.syq.nfcpro00.tools.compare.CompareTime;
import com.example.syq.nfcpro00.tools.crypto.Aes;
import com.example.syq.nfcpro00.tools.crypto.rsa.RSAUtils;
import com.example.syq.nfcpro00.tools.crypto.sha1.Sha1;
import com.example.syq.nfcpro00.tools.helper.PreferenceHelper;
import com.example.syq.nfcpro00.tools.helper.TransformationHelper;
import com.example.syq.nfcpro00.tools.utils.MessageVerification;

import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

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
 * Class Name messageToKDC
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/4/14 15:51
 */


public class MessageToKDC {
    private static final String URL_KDC = "http://192.168.0.129:8088/apps/";
    private static final Logger log = LoggerFactory.getLogger(MessageToKDC.class);
    private static String appPubKey,kdcPublKey,appPriKey,MAC ;
    @Getter private static String aesMessageToUserServer = "";
    private static boolean init(Map map){
        appPubKey = (String) map.get("app_public_key");
        appPriKey = (String) map.get("app_private_key");
        kdcPublKey = (String) map.get("kdc_public_key");
        MAC = (String) map.get("app_IMEI");
        if (Strings.isNullOrEmpty(appPubKey)){
            log.error("appPubKey error",new NullPointerException());
            return false;
        }
        log.info("init appPubKey=={}",appPubKey);
        if (Strings.isNullOrEmpty(appPriKey)){
            log.error("appPriKey error",new NullPointerException());
            return false;
        }
        log.info("init appPriKey=={}",appPriKey);
        if (Strings.isNullOrEmpty(kdcPublKey)){
            log.error("kdcPublKey error",new NullPointerException());
            return false;
        }
        log.info("init kdcPublKey=={}",kdcPublKey);
        if (Strings.isNullOrEmpty(MAC)){
            log.error("MAC error",new NullPointerException());
            return false;
        }
        log.info("init MAC=={}",MAC);
        return true;
    }
    public static String newApp(Map map) throws Exception {
        if (!init(map)){
            log.error("init error",new NullPointerException());
            return "Refuse";
        }
        log.info("new App init success");
        String rsaDe = MAC+appPubKey;
        log.info("newApp rsaDe==>{}",rsaDe);
        String aesDe = MessageDecomposition.getTime();
        String rsaEn = TransformationHelper.byteToHex(RSAUtils.encryptByPublicKey(rsaDe.getBytes(),kdcPublKey));
        String aesEn = Aes.Encrypt(aesDe, MessageVerification.messagecompression(MAC).toString());
        String hmac = Sha1.HmacSHA1Encrypt(rsaEn+aesEn,MAC);
        String urlTemp = URL_KDC +"300000/"+"300000"+hmac+aesEn+rsaEn;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(urlTemp).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()){
            throw new IOException("服务器端错误: " + response);
        }
        ResponseBody responseHeaders = response.body();
        String returnMessage = null;
        if (responseHeaders != null) {
            returnMessage = responseHeaders.string();
        }
        if (returnMessage != null) {
            hmac = returnMessage.substring(6,46);
            rsaEn = returnMessage.substring(46);
        }
        String hmacMake = Sha1.HmacSHA1Encrypt(rsaEn,MAC);
        //HMAC匹配成功
        if (CompareHmac.compareHmac(hmac,hmacMake)){
            rsaDe =new String (RSAUtils.decryptByPrivateKey(TransformationHelper.hexToByte(rsaEn),appPriKey));
            if (CompareTime.compareTime(MessageDecomposition.getTime(),rsaDe.substring(6))){
                //匹配时间成功
                return rsaDe.substring(0,6);
            }
            else {
                return "Refuse";
            }
        }
        else {
            return "Refuse";
        }
    }
    public static String updataUserSharedKey(String userID) throws Exception {
        if (!init(MainActivity.keyMap)){
            log.error("init error",new NullPointerException());
            return "Refuse";
        }
        String rsaDe = MAC;
        String aesDe = MessageDecomposition.getTime();

        String temp =rsaDe+userID;
        String rsaEn = TransformationHelper.byteToHex(RSAUtils.encryptByPublicKey(temp.getBytes(),kdcPublKey));
        String aesEn = Aes.Encrypt(aesDe,MessageVerification.messagecompression(rsaDe).toString());
        System.out.println("AES EN==>"+aesEn);
        String hmac = Sha1.HmacSHA1Encrypt(rsaEn+aesEn,rsaDe);

        String urltemp = URL_KDC+"300100/"+"300100"+hmac+aesEn+rsaEn;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(urltemp).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()){
            throw new IOException("服务器端错误: " + response);
        }
        ResponseBody responseHeaders = response.body();

        String returnMessage = null;
        if (responseHeaders != null) {
            returnMessage = responseHeaders.string();
        }

        if (returnMessage != null) {
            hmac = returnMessage.substring(6,46);
            rsaEn = returnMessage.substring(46);
        }
        String hmacMake = Sha1.HmacSHA1Encrypt(rsaEn,MAC);
        //HMAC匹配成功
        if (CompareHmac.compareHmac(hmac,hmacMake)){
            rsaDe =new String (RSAUtils.decryptByPrivateKey(TransformationHelper.hexToByte(rsaEn),appPriKey));
            if (CompareTime.compareTime(MessageDecomposition.getTime(),rsaDe.substring(16))){
                //匹配时间成功
                return rsaDe.substring(0,16);
            }
            else {
                return "Refuse";
            }
        }
        else {
            return "Refuse";
        }
    }

    static String getSessionKey(Context context)throws Exception{
        if (!init(MainActivity.keyMap)) {
            log.error("init error", new NullPointerException());
            return "Refuse";
        }
        SharedPreferences preferences = PreferenceHelper.getSharedPreferences(context);
        String userID = preferences.getString("userID", "");
        String kc = preferences.getString("kc", "");
        String serverId = "qwerty";
        String aesDe = MessageDecomposition.getTime() + serverId;
        String aesEn = Aes.Encrypt(aesDe, kc);
        String hmac = Sha1.HmacSHA1Encrypt(aesEn, kc + kc);
        String urltemp = URL_KDC + "300011/" + "300011" + userID + hmac + aesEn;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(urltemp).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("服务器端错误: " + response);
        }
        ResponseBody responseHeaders = response.body();

        String returnMessage = null;
        if (responseHeaders != null) {
            returnMessage = responseHeaders.string();
        }

        if (returnMessage != null) {
            hmac = returnMessage.substring(12, 52);
            aesEn = returnMessage.substring(52);
        }

        String hmacMake = Sha1.HmacSHA1Encrypt(aesEn, kc + kc);
        //HMAC匹配成功
        if (CompareHmac.compareHmac(hmac, hmacMake)) {
            aesDe = Aes.Decrypt(aesEn, kc);
            if (aesDe != null) {
                if (CompareTime.compareTime(MessageDecomposition.getTime(), aesDe.substring(16, 26))) {
                    //匹配时间成功,返回sessionkey
                    aesMessageToUserServer = aesDe.substring(26);
                    return aesDe.substring(0, 16);
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
    public static String getUserID(Context context)throws Exception{
        if (!init(MainActivity.keyMap)) {
            log.error("init error", new NullPointerException());
            return "Refuse";
        }

        log.info("appPubKey{}",appPubKey);
        log.info("kdcPublKey{}",kdcPublKey);
        log.info("appPriKey{}",appPriKey);
        log.info("MAC{}",MAC);
        String rsaDe = MAC;
        String aesDe = MessageDecomposition.getTime();
        log.info("aseDe==>"+aesDe);
        String rsaEn = TransformationHelper.byteToHex(RSAUtils.encryptByPublicKey(MAC.getBytes(),kdcPublKey));
        String aesEn = Aes.Encrypt(aesDe,MessageVerification.messagecompression(rsaDe).toString());
        String hmac = Sha1.HmacSHA1Encrypt(rsaEn+aesEn,MAC);
        String urlTrmp = URL_KDC +"300001/"+"300001"+hmac+aesEn+rsaEn;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(urlTrmp).build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()){
            throw new IOException("服务器端错误: " + response);
        }
        ResponseBody responseHeaders = response.body();
        String returnMessage = null;
        if (responseHeaders != null) {
            returnMessage = responseHeaders.string();
        }

        if (returnMessage != null) {
            hmac = returnMessage.substring(6,46);
            rsaEn = returnMessage.substring(46);
        }

        String hmacMake = Sha1.HmacSHA1Encrypt(rsaEn,MAC);
        //HMAC匹配成功
        if (CompareHmac.compareHmac(hmac,hmacMake)){
            rsaDe =new String (RSAUtils.decryptByPrivateKey(TransformationHelper.hexToByte(rsaEn),appPriKey));
            if (CompareTime.compareTime(MessageDecomposition.getTime(),rsaDe.substring(6))){
                //匹配时间成功
                return rsaDe.substring(0,6);
            }
            else {
                return "Refuse";
            }
        }
        else {
            return "Refuse";
        }
    }

}
