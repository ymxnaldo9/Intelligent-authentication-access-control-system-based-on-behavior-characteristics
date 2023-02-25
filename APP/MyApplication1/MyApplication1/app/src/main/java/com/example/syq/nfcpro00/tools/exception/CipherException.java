package com.example.syq.nfcpro00.tools.exception;

import lombok.NoArgsConstructor;

/**
 * @author Gorio
 */
@NoArgsConstructor
public class CipherException extends RuntimeException{


    public CipherException(String s) {
        super(s);
    }

    public CipherException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CipherException(Throwable throwable) {
        super(throwable);
    }

    protected CipherException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
