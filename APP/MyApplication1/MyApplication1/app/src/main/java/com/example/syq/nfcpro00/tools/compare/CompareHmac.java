package com.example.syq.nfcpro00.tools.compare;

import org.assertj.core.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class Name CompareHmac
 * Created by Gorio
 *
 * @author Gorio
 * @date 2018/3/30
 */
public class CompareHmac {
    private static final Logger log = LoggerFactory.getLogger(CompareHmac.class);
    public static boolean compareHmac(String hmacGet,String hmacMake) {
        log.info("hmacGet==>{}",hmacGet);
        log.info("hmacMake==>{}",hmacMake);
        return !Strings.isNullOrEmpty(hmacGet) && !Strings.isNullOrEmpty(hmacMake) && hmacGet.equals(hmacMake);
    }
}
