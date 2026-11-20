package org.example.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.common.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理，保证所有异常都以统一结构返回。
 *
 * @author windy
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 {@code @Valid @ModelAttribute} 与 {@code @Valid @RequestBody} 的参数校验失败。
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleBindException(BindException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return R.fail(HttpStatus.BAD_REQUEST.value(), message.isEmpty() ? "参数校验失败" : message);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + " " + error.getDefaultMessage();
    }

    /**
     * 处理方法参数上的约束校验失败。
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleConstraintViolation(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败: {}", message);
        return R.fail(HttpStatus.BAD_REQUEST.value(), message);
    }

    /**
     * 处理非法的枚举值、日期等入参。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleIllegalArgumentException(IllegalArgumentException exception) {
        return R.fail(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    /**
     * 处理请求体 JSON 解析失败。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        return R.fail(HttpStatus.BAD_REQUEST.value(),
                "请求体格式错误: " + exception.getMostSpecificCause().getMessage());
    }

    /**
     * 处理 Open-Meteo 调用失败。
     */
    @ExceptionHandler(OpenMeteoException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public R<Void> handleOpenMeteoException(OpenMeteoException exception) {
        log.error("Open-Meteo 调用异常", exception);
        return R.fail(HttpStatus.BAD_GATEWAY.value(), exception.getMessage());
    }

    /**
     * 兜底异常。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception exception) {
        log.error("系统异常", exception);
        return R.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "系统异常，请稍后重试");
    }
}
