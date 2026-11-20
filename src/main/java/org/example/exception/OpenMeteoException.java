package org.example.exception;

/**
 * Open-Meteo 调用异常，统一映射为 HTTP 502。
 *
 * @author windy
 */
public class OpenMeteoException extends RuntimeException {

    public OpenMeteoException(String message) {
        super(message);
    }

    public OpenMeteoException(String message, Throwable cause) {
        super(message, cause);
    }
}
