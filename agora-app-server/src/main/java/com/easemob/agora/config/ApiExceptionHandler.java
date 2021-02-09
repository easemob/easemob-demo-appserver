package com.easemob.agora.config;

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
    public ResponseEntity<Object> handelIllegalArgumentExcetpion(IllegalArgumentException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_PARME_ERROR);

        return handleExceptionInternal(ex, param, headers, status, request);
    }

}
