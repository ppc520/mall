package com.exception;

import com.common.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

/**
 * 异常类
 */
@Slf4j
@RestControllerAdvice
public class MallException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MallException() {}

    public MallException(String message) {
        super(message);
    }
}
