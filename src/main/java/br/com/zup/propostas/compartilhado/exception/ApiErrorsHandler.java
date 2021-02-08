package br.com.zup.propostas.compartilhado.exception;

import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApiErrorsHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<ObjectError> globalErrors = e.getBindingResult().getGlobalErrors();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();

        return buildApiErrors(globalErrors, fieldErrors);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors bindExceptionHandler(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<ObjectError> globalErrors = e.getBindingResult().getGlobalErrors();

        return buildApiErrors(globalErrors, fieldErrors);
    }

    @ExceptionHandler(FeignException.FeignClientException.class)
    public ResponseEntity<ApiErrors> feignClientExceptionHandler(FeignException.FeignClientException e) {
        ApiErrors apiErrors = new ApiErrors();
        apiErrors.addGlobalError(e.getMessage());
        return ResponseEntity.status(e.status()).body(apiErrors);
    }

    @ExceptionHandler(FeignException.FeignServerException.class)
    public ResponseEntity<ApiErrors> feignServerExceptionHandler(FeignException.FeignServerException e) {
        ApiErrors apiErrors = new ApiErrors();
        apiErrors.addGlobalError(e.getMessage());
        return ResponseEntity.status(e.status()).body(apiErrors);
    }

    private ApiErrors buildApiErrors(List<ObjectError> globalErrors, List<FieldError> fieldErrors) {
        ApiErrors apiErrors = new ApiErrors();
        globalErrors.forEach(error -> apiErrors.addGlobalError(getMessage(error)));
        fieldErrors.forEach(error -> apiErrors.addFieldError(error.getField(), getMessage(error)));
        return apiErrors;
    }

    private String getMessage(ObjectError error) {
        return messageSource.getMessage(error, LocaleContextHolder.getLocale());
    }
}