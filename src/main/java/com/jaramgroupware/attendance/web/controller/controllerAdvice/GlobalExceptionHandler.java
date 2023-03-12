package com.jaramgroupware.attendance.web.controller.controllerAdvice;

import com.jaramgroupware.attendance.dto.others.controllerDto.ErrorResponseDto;
import com.jaramgroupware.attendance.utlis.exception.controllerExecption.ControllerException;
import com.jaramgroupware.attendance.utlis.exception.serviceException.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ MissingRequestCookieException.class,HttpRequestMethodNotSupportedException.class , HttpMediaTypeNotSupportedException.class,HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class })
    protected ResponseEntity<ErrorResponseDto> handleBadRequestsException(Exception exception, WebRequest request) {
        log.info("Path : {} -> Throw {}",request.getContextPath(),exception.getClass().getName());
        return ResponseEntity.badRequest().body(ErrorResponseDto.builder()
                .type("None")
                .status(HttpStatus.BAD_REQUEST)
                .title("잘못된 요청입니다.")
                .detail("클라이언트의 요청이 잘못되었습니다.")
                .instance(request.getContextPath())
                .build());
    }

    @ExceptionHandler({ Exception.class })
    protected ResponseEntity<ErrorResponseDto> handleServerException(Exception exception, WebRequest request) {
        log.info("Path : {} -> Throw {} / Message {}",request.getContextPath(),exception.getClass().getSimpleName(),exception.getMessage());

        return ResponseEntity.internalServerError().body(ErrorResponseDto.builder()
                .type("None")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .title("서버에 오류가 발생했습니다.")
                .detail("서버에 알 수 없는 오류가 발생했습니다. 잠시후 다시 시도해주세요.")
                .instance(request.getContextPath())
                .build());
    }

    @ExceptionHandler({ ControllerException.class })
    protected ResponseEntity<ErrorResponseDto> handleControllerException(ControllerException exception, WebRequest request) {
        log.info("Path : {} -> Throw {} / Message {}",request.getContextPath(),exception.getClass().getSimpleName(),exception.getErrorCode().getDetail());

        return ResponseEntity.status(exception.getErrorCode().getHttpStatus()).body(ErrorResponseDto.builder()
                .type(exception.getErrorCode().getType())
                .status(exception.getErrorCode().getHttpStatus())
                .title(exception.getErrorCode().getTitle())
                .detail(exception.getErrorCode().getDetail())
                .instance(request.getContextPath())
                .build());
    }

    @ExceptionHandler({ ServiceException.class })
    protected ResponseEntity<ErrorResponseDto> handleServiceException(ServiceException exception, WebRequest request) {
        log.info("Path : {} -> Throw {} / Message {}",request.getContextPath(),exception.getClass().getSimpleName(),exception.getErrorCode().getDetail());

        return ResponseEntity.status(exception.getErrorCode().getHttpStatus()).body(ErrorResponseDto.builder()
                .type(exception.getErrorCode().getType())
                .status(exception.getErrorCode().getHttpStatus())
                .title(exception.getErrorCode().getTitle())
                .detail(exception.getErrorCode().getDetail())
                .instance(request.getContextPath())
                .build());
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class })
    protected ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception, WebRequest request) {
        log.info("Path : {} -> Throw {}",request.getContextPath(),exception.getClass().getSimpleName());

        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return ResponseEntity.badRequest().body(ErrorResponseDto.builder()
                .type("None")
                .status(HttpStatus.BAD_REQUEST)
                .title("요청에 잘못된 값이 존재합니다.")
                .detail(errors.toString())
                .instance(request.getContextPath())
                .build());
    }

}