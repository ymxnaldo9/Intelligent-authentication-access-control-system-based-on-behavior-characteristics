package com.example.syq.nfcpro00.tools.utils;

//import lombok.extern.log4j.Log4j;

/**
 * @author Gorio
 */
//@Log4j
public class MessageVerification {
    private static final int IS_EVEN_STRING =2;
    private static final int LEGAL_MESSAGE_LENGTH =32;
    private static final int COMPRESSION_MESSAGE_LENGTH =16;
    private static int messagesummation(String message){
        int sum =0;
        int length =message.length();
        for (int i = 0; i <length ; i++) {
            sum+=message.charAt(i);
        }
        return sum;
    }
    public static StringBuffer messagecompression(String message){
        StringBuffer stringBuffer =new StringBuffer();
        int length = message.length();
        int messagesum = messagesummation(message);
        //如果计数和为偶数
        if (messagesum%IS_EVEN_STRING==0){

            for (int i = 0; i <length ; i+=IS_EVEN_STRING) {
                int temp = message.charAt(i)+message.charAt(i+1);
                temp=messagesum%temp>=LEGAL_MESSAGE_LENGTH?messagesum%temp%LEGAL_MESSAGE_LENGTH:messagesum%temp;

                stringBuffer.append(message.charAt(temp));
            }
        }
        else {
            for (int i = 0; i <COMPRESSION_MESSAGE_LENGTH;i++) {
                int temp = message.charAt(i)+message.charAt(length-1-i);
                temp=messagesum%temp>=LEGAL_MESSAGE_LENGTH?messagesum%temp%LEGAL_MESSAGE_LENGTH:messagesum%temp;
                stringBuffer.append(message.charAt(temp));
            }
        }
        return stringBuffer;
    }

}
