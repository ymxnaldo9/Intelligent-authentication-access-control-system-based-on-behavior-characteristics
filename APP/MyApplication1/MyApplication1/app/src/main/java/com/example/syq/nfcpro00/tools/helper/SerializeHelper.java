package com.example.syq.nfcpro00.tools.helper;

import java.io.*;

/**
 * Project Name userserver
 * Packege Name com.aes.userserver.tools
 * Class Name SerializeHelper
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/4/14 12:55
 */
public class SerializeHelper {
    private static final String TEMP_ENCODING = "ISO-8859-1";
    private static final String DEFAULT_ENCODING = "UTF-8";
    public static String writeObjectToString(Object o) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        String serializeString = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(o);
            serializeString = byteArrayOutputStream.toString(TEMP_ENCODING);
            serializeString = java.net.URLEncoder.encode(serializeString,DEFAULT_ENCODING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            byteArrayOutputStream.close();
        }
        return serializeString;
    }

    public static Object deserializeFromString(String serStr) throws IOException {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            String deserStr = java.net.URLDecoder.decode(serStr, DEFAULT_ENCODING);
            byteArrayInputStream = new ByteArrayInputStream(deserStr.getBytes(TEMP_ENCODING));
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
            if (byteArrayInputStream != null) {
                byteArrayInputStream.close();
            }
        }
        return null;
    }
}
