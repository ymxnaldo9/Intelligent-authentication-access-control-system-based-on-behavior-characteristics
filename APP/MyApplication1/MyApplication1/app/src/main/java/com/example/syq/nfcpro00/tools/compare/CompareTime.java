package com.example.syq.nfcpro00.tools.compare;

import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class Name CompareTime
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/3/30
 */
public class CompareTime {
    private static final Logger log = LoggerFactory.getLogger(CompareTime.class);
    private static final long MAX_MESSAGE_TIME_INTERVAL = 60;
    private static final long MIN_MESSAGE_TIME_INTERVAL = 0;
    private static final String TAG = "LOG compare time";

    public static boolean compareTime(@NonNull String instant,@NonNull String sendTime){
        log.info("instant ==>{}",instant);
        log.info("sendTime==>{}",sendTime);
        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmm");
        Date instantDate,sendTimeDate;
        try {
            instantDate = dateFormat.parse(instant);
            sendTimeDate = dateFormat.parse(sendTime);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(),e);
            return false;
        }
        if (sendTimeDate.after(instantDate)){
            return false;
        }
        else {
            return isLegitimateTime(getDistanceDays(instantDate,sendTimeDate));
        }
    }

    private static long getDistanceDays(@NonNull Date instantdate, @NonNull Date sendtimedate) {
        long days=0;
        long time1 = instantdate.getTime();
        long time2 = sendtimedate.getTime();
        long diff ;
        if(time1<time2) {
            diff = time2 - time1;
        }
        else {
            diff = time1 - time2;
        }
        days = diff / (1000);
        return days;
    }
    /**
     * 判断时间间隔是否在合法间隔之内
     * @param second long 时间间隔秒数 不可为空
     * @return boolean
     */
    private static boolean isLegitimateTime(long second){
        return second <= MAX_MESSAGE_TIME_INTERVAL && second >= MIN_MESSAGE_TIME_INTERVAL;
    }
}
