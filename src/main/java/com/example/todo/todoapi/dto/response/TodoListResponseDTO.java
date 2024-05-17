package com.example.todo.todoapi.dto.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class TodoListResponseDTO {

    // 페이징에 필요한 정보 등도 함께 담아서 클라이언트로 리턴할 용도로 생성하는 DTO

    private String error; // 에러 발생 시 에러 메세지를 담을 필드
    private List<TodoDetailResponseDTO> todos; // 할 일 목록들

}
