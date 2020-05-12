package com.easemob.live.server.liveroom.exception;

import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author shenchong@easemob.com 2020/2/19
 */
@RestControllerAdvice
public class LiveRoomExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        FieldError error = result.getFieldError();

        final ExceptionResponse response = new ExceptionResponse();
        response.setErrorDescription(error.getDefaultMessage());
        response.setException(ex.getClass().getName());
        response.setError("illegal_argument");
        return handleExceptionInternal(ex, response, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        final ExceptionResponse response = new ExceptionResponse();
        response.setErrorDescription(ex.getMessage());
        response.setException(ex.getClass().getName());
        response.setError("illegal_argument");

        return handleExceptionInternal(ex, response, headers, status, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex,
            WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ExceptionResponse response = new ExceptionResponse();
        response.setErrorDescription(ex.getMessage());
        response.setException(ex.getClass().getName());
        response.setError("illegal_argument");
        return handleExceptionInternal(ex, response, headers, status, request);
    }

    @ExceptionHandler(ForbiddenOpException.class)
    public ResponseEntity<Object> handleForbiddenOpException(ForbiddenOpException ex,
            WebRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        HttpHeaders headers = new HttpHeaders();
        final ExceptionResponse response = new ExceptionResponse();
        response.setErrorDescription(ex.getMessage());
        response.setException(ex.getClass().getName());
        response.setError("forbidden_op");
        return handleExceptionInternal(ex, response, headers, status, request);
    }

    @ExceptionHandler(LiveRoomException.class)
    public ResponseEntity<Object> handleLiveRoomException(LiveRoomException ex,
            WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders headers = new HttpHeaders();
        final ExceptionResponse response = new ExceptionResponse();
        response.setErrorDescription(ex.getMessage());
        response.setException(ex.getClass().getName());
        response.setError("failed");
        return handleExceptionInternal(ex, response, headers, status, request);
    }

    @ExceptionHandler(LiveRoomSecurityException.class)
    public ResponseEntity<Object> handleEasemobSecurityException(LiveRoomSecurityException ex, WebRequest request){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        HttpHeaders headers = new HttpHeaders();
        final ExceptionResponse response = new ExceptionResponse();
        response.setErrorDescription(ex.getMessage());
        response.setException(ex.getClass().getName());
        response.setError(ex.getType());
        return handleExceptionInternal(ex, response, headers, status, request);
    }

    @ExceptionHandler(LiveRoomNotFoundException.class)
    public ResponseEntity<Object> handleLiveRoomNotFoundException(LiveRoomNotFoundException ex, WebRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        HttpHeaders headers = new HttpHeaders();
        final ExceptionResponse response = new ExceptionResponse();
        response.setErrorDescription(ex.getMessage());
        response.setException(ex.getClass().getName());
        response.setError("resource_not_found");
        return handleExceptionInternal(ex, response, headers, status, request);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleHttpClientErrorException(HttpClientErrorException ex, WebRequest request){
        HttpStatus status = ex.getStatusCode();
        HttpHeaders headers = new HttpHeaders();
        final ExceptionResponse response =
                JSONObject.parseObject(ex.getResponseBodyAsString(), ExceptionResponse.class);
        return handleExceptionInternal(ex, response, headers, status, request);
    }
}
