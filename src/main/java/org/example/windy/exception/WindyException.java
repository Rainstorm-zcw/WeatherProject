package org.example.windy.exception;

/**
 * Windy 接口调用异常，统一映射为 HTTP 502。
 *
 * @author windy
 */
public class WindyException extends RuntimeException {

    public WindyException(String message) {
        super(message);
    }

    public WindyException(String message, Throwable cause) {
        super(message, cause);
    }
}
