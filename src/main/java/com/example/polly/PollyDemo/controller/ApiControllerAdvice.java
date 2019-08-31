package com.example.polly.PollyDemo.controller;

import com.example.polly.PollyDemo.NotFoundException;
import com.example.polly.PollyDemo.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    /**
     * 요청이 올바르지 않은 경우
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("IllegalArgumentException", ex);
        return ex.getMessage();
    }

    /**
     * 토큰이 유효하지 않은 경우
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("UnauthorizedException", ex);
        return ex.getMessage();
    }

    /**
     * 리소스가 존재하지 않는 경우
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NotFoundException ex) {
        log.warn("NotFoundException", ex);
        return ex.getMessage();
    }

    /**
     * 서버 에러
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex) {
        log.error("Exception", ex);
        return ex.getMessage();
    }
}
