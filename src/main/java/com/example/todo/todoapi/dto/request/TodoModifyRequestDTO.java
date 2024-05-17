package com.example.todo.todoapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class TodoModifyRequestDTO {

    @NotBlank
    private String id;

    private boolean done;
}
