package com.easemob.agora.config;

import com.easemob.agora.exception.*;
import com.easemob.agora.model.ResCode;
import com.easemob.agora.model.ResponseParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author skyfour
 * @date 2021/2/9
 * @email skyzhang@easemob.com
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentExcetpion(IllegalArgumentException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_REQUEST_PARAM_ERROR);

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundExcetpion(ASNotFoundException ex, WebRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_USER_NOT_FOUND);

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASGetEasemobUserNameException.class)
    public ResponseEntity<Object> handleGetEasemobUserIdExcetpion(ASGetEasemobUserNameException ex, WebRequest request){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_UNKNOWN);

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASRegisterEasemobUserNameException.class)
    public ResponseEntity<Object> handleRegisterEasemobUserIdExcetpion(ASRegisterEasemobUserNameException ex, WebRequest request){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_UNKNOWN);

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASGetEasemobUserIdException.class)
    public ResponseEntity<Object> handleGetEasemobUserIdExcetpion(ASGetEasemobUserIdException ex, WebRequest request){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_UNKNOWN);

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASGetTokenReachedLimitException.class)
    public ResponseEntity<Object> handleGetTokenReachedLimitExcetpion(ASGetTokenReachedLimitException ex, WebRequest request){
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_REACH_LIMIT);

        return handleExceptionInternal(ex, param, headers, status, request);
    }
}
