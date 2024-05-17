package com.example.todo.todoapi.api;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // 요청에 맞는 json만 줌
@RequiredArgsConstructor
@RequestMapping("/api/todos")
@Slf4j
public class TodoController {

    private final TodoService todoService;

    // 할 일 등록하기
    @PostMapping
    public ResponseEntity<?> createTodo(
            @Validated @RequestBody TodoCreateRequestDTO requestDTO,
            BindingResult result
    ) {
        log.info("/api/todos GET! - dto: {}", requestDTO);
        ResponseEntity<List<FieldError>> validatedResult = getValidatedResult(result);
        if (validatedResult != null) return validatedResult;

        try {
            TodoListResponseDTO responseDTO = todoService.create(requestDTO);
            return ResponseEntity
                    .ok()
                    .body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(TodoListResponseDTO.builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    // 할 일 목록 요청
    @GetMapping
    public ResponseEntity<?> retrieveTodoList() {
        log.info("/api/todos GET request!");
        try {
            TodoListResponseDTO responseDTO = todoService.findAll();
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(TodoListResponseDTO.builder()
                            .error(e.getMessage())
                            .build());
        }
    }

    // 할 일 삭제 요청
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(
            @PathVariable("id") String todoId
    ) {
        log.info("/api/todos/{} DELETE request!", todoId);

        if (todoId == null || todoId.trim().equals("")) {
            return ResponseEntity.badRequest().body("ID를 전달해 주세요.");
        }
        try {
            TodoListResponseDTO responseDTO = todoService.delete(todoId);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 할 일 수정하기
    @PatchMapping
    public ResponseEntity<?> updateTodo(
            @Validated @RequestBody TodoModifyRequestDTO requestDTO,
            BindingResult result
    ) {
        ResponseEntity<List<FieldError>> validatedResult = getValidatedResult(result);
        if (validatedResult != null) return validatedResult;

        try {
           return ResponseEntity.ok().body(todoService.update(requestDTO));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }



    // 입력값 검증(Validation)의 결과를 처리해 주는 전역 메서드
    private static ResponseEntity<List<FieldError>> getValidatedResult(BindingResult result) {
        if (result.hasErrors()) { // 입력값 검증 단계에서 문제가 있었다면 true
            List<FieldError> fieldErrors = result.getFieldErrors();
            fieldErrors.forEach(err -> {
                log.warn("invalid client data - {}", err.toString());
            });
            return ResponseEntity
                    .badRequest()
                    .body(fieldErrors);
        }
        return null;
    }

}















