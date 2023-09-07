package com.example.beertapdispenserwithspringboot.exception;

import com.example.beertapdispenserwithspringboot.constants.MSExceptionEnum;
import com.example.beertapdispenserwithspringboot.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MSException.class)
    public ResponseEntity<Object> generalMSExceptionHandler(MSException ex) {
        log.info("General Exception Handler catch a MSException : {} ", ex.getErrorCode());
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setErrorCode(ex.getErrorCode());
        errorDTO.setErrorMessage(ex.getMessage());
        errorDTO.setResource(ex.getResource());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setErrorCode(MSExceptionEnum.INVALID_INPUT.getErrorCode());
        errorDTO.setErrorMessage(MSExceptionEnum.INVALID_INPUT.getErrorMessage());
        errorDTO.setResource(ex.getBindingResult().getFieldErrors().stream().findFirst().get().getField());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }
}
