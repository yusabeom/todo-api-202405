package com.example.todo.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
//@ControllerAdvice
// RestController에서 발생되는 예외를 전역적으로 처리할 수 있게 하는 아노테이션.
// 예외 상황에 따른 응답을 REST 방식으로 클라이언트에게 전달할 수 있다.
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*
    @RestControllerAdvice로 등록된 전역 예외 처리 방식은 Controller에서 발생된 예외만 처리합니다.
    Service, Repository, Filter에서 발생하는 예외는 처리하지 못합니다.
    ExpiredJwtException 처럼 security filter 단에서 발생하는 예외 타입은 애초에 요청 자체가
    Controller에 닿지 못하기 때문에 처리할 수 없습니다.
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleRuntimeException(ExpiredJwtException e) {
        log.info("ExpiredJwtException 발생!");
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    */

    @ExceptionHandler({RuntimeException.class, NoRegisteredArgumentException.class})
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleRuntimeException(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleRuntimeException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}