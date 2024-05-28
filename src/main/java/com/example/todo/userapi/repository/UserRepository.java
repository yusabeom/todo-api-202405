package com.example.todo.userapi.repository;
import com.example.todo.userapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, String> {
    // 이메일 중복 체크
//    @Query("SELECT COUNT(*) FROM User u WHERE u.email =: email") -> JPQL
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);

    // 리프레시 토큰으로 사용자 정보 조회하기
    Optional<User> findByRefreshToken(String refreshToken);

}