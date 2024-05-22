package com.example.todo.todoapi.service;
import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.entity.Todo;
import com.example.todo.todoapi.repository.TodoRepository;
import com.example.todo.userapi.entity.Role;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoService {
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoListResponseDTO create(
            final TodoCreateRequestDTO requestDTO,
            final String userId) {

        // 이제는 할 일 등록은 회원만 할 수 있도록 세팅하기 때문에
        // toEntity의 매개값으로 User 엔터티도 함께 전달해야 합니다. -> userId로 회원 엔터티를 조회해야 함.
        User user = getUser(userId);

        // 권한에 따른 글쓰기 제한 처리
        // 일반 회원이 일정을 5개 초과해서 작성하면 예외를 발생.
        if (user.getRole() == Role.COMMON
                && todoRepository.countByUser(user) >= 5) {
            throw new IllegalArgumentException("일반회원은 더 이상 일정을 등록할 수 없습니다.");
        }

        todoRepository.save(requestDTO.toEntity(user));
        log.info("할 일 저장 완료! 제목: {}", requestDTO.getTitle());
        // 할 일 저장이 끝나면 목록을 불러오는데, 지금까지는 그냥 전부 다 갖고 왔어도 된다.
        // 이제는 회원별로 할 일을 등록하기 때문에, 방금 할 일을 추가한 그 회원의 목록을 가져와야 합니다.
        return retrieve(userId);
    }


    // 할 일 목록 가져오기
    public TodoListResponseDTO retrieve(String userId) {

        // 로그인 한 유저의 정보를 데이터베이스에서 조회
        User user = getUser(userId);
        List<Todo> entityList = todoRepository.findAllByUser(user);
        List<TodoDetailResponseDTO> dtoList = entityList.stream()
//                .map(entity -> new TodoDetailResponseDTO(entity))
                .map(TodoDetailResponseDTO::new)
                .collect(Collectors.toList());
        return TodoListResponseDTO.builder()
                .todos(dtoList)
                .build();
    }
    public TodoListResponseDTO delete(final String todoId, final String userId) throws Exception {
        Todo todo = todoRepository.findById(todoId).orElseThrow(
                () -> {
                    log.error("id가 존재하지 않아 삭제에 실패했습니다. - ID: {}", todoId);
                    throw new RuntimeException("id가 존재하지 않아 삭제에 실패했습니다.");
                }
        );
        todoRepository.deleteById(todoId);
        return retrieve(userId);
    }
    public TodoListResponseDTO update(final TodoModifyRequestDTO requestDTO, final String userId) throws Exception {
        Optional<Todo> targetEntity
                = todoRepository.findById(requestDTO.getId());
        targetEntity.ifPresent(todo -> {
            todo.setDone(requestDTO.isDone());
            todoRepository.save(todo);
        });
        return retrieve(userId);
    }
    private User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );
        return user;
    }
}