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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASAuthException.class)
    public ResponseEntity<Object> handleAuthException(ASAuthException ex, WebRequest request){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_UNAUTHORIZED_ERROR.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(ASNotFoundException ex, WebRequest request){
        HttpStatus status = HttpStatus.NOT_FOUND;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_USER_NOT_FOUND.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASGetChatUserNameException.class)
    public ResponseEntity<Object> handleGetChatUserIdException(ASGetChatUserNameException ex, WebRequest request){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_UNKNOWN.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASRegisterChatUserNameException.class)
    public ResponseEntity<Object> handleRegisterChatUserIdException(ASRegisterChatUserNameException ex, WebRequest request){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_UNKNOWN.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASGetChatUserIdException.class)
    public ResponseEntity<Object> handleGetChatUserIdException(ASGetChatUserIdException ex, WebRequest request){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_UNKNOWN.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASPasswordErrorException.class)
    public ResponseEntity<Object> handlePasswordErrorException(ASPasswordErrorException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASResourceLimitedException.class)
    public ResponseEntity<Object> handleResourceLimitedException(ASResourceLimitedException ex, WebRequest request) {
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_REACH_LIMIT.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASDuplicateUniquePropertyExistsException.class)
    public ResponseEntity<Object> handleDuplicateUniquePropertyExistsException(ASDuplicateUniquePropertyExistsException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_USER_ALREADY_EXISTS.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASRestException.class)
    public ResponseEntity<Object> handleRestException(ASRestException ex, WebRequest request){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_UNKNOWN.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASUnAuthorizedException.class)
    public ResponseEntity<Object> handleDuplicateNoAuthException(ASUnAuthorizedException ex, WebRequest request){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_UNAUTHORIZED_ERROR.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(ASOneToOneVideoMatchException.class)
    public ResponseEntity<Object> handleOneToOneVideoMatchException(ASOneToOneVideoMatchException ex, WebRequest request){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo(ex.getMessage());
        param.setCode(ResCode.RES_REQUEST_PARAM_ERROR.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex,
            WebRequest request) {
        HttpStatus status = HttpStatus.REQUEST_ENTITY_TOO_LARGE;
        HttpHeaders headers = new HttpHeaders();
        final ResponseParam param = new ResponseParam();
        param.setErrorInfo("upload avatar too large.");
        param.setCode(ResCode.RES_UPLOAD_AVATAR_TOO_LARGE.getCode());

        return handleExceptionInternal(ex, param, headers, status, request);
    }
}
