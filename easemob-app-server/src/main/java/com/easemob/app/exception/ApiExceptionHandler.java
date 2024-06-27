package com.easemob.app.exception;

import com.easemob.app.model.ResCode;
import com.easemob.app.model.ResponseParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_REQUEST_PARAM_ERROR.code);

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(ASNotFoundException ex, WebRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_USER_NOT_FOUND.code);

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASRequestRestApiException.class)
    public ResponseEntity<Object> handleRequestRestApiException(ASRequestRestApiException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_REQUEST_REST_ERROR.code);

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASPasswordErrorException.class)
    public ResponseEntity<Object> handlePasswordErrorException(ASPasswordErrorException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_REQUEST_PARAM_ERROR.code);

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASDuplicateUniquePropertyExistsException.class)
    public ResponseEntity<Object> handleDuplicateUniquePropertyExistsException(ASDuplicateUniquePropertyExistsException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_USER_ALREADY_EXISTS.code);

        return handleExceptionInternal(ex, param, headers, status, request);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
        BindingResult result = ex.getBindingResult();
        FieldError error = result.getFieldError();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(error.getDefaultMessage());
        param.setCode(ResCode.RES_REQUEST_PARAM_ERROR.code);

        return handleExceptionInternal(ex, param, headers, status, request);
    }
}
