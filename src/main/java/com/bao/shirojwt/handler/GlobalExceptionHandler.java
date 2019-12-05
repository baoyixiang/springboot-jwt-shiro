package com.bao.shirojwt.handler;

import com.bao.shirojwt.controller.LoginController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

// @ControllerAdvice 注解让这个类成为全局异常处理类，可以指定处理哪个类抛出的异常
@ControllerAdvice(assignableTypes = LoginController.class)
public class GlobalExceptionHandler {

    // @ExceptionHandler 可以指定这个方法处理哪个异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handlerValidationExceptions(MethodArgumentNotValidException ex) {

        Map<String, String> errs = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessages = error.getDefaultMessage();
            errs.put(fieldName, errorMessages);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errs);
    }
}
