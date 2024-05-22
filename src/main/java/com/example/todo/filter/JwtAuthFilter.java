package com.example.todo.filter;
import com.example.todo.auth.TokenProvider;
import com.example.todo.auth.TokenUserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
// 클라이언트가 전송한 토큰을 검사하는 필터
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    // 필터가 해야 할 작업을 기술
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = parseBearerToken(request);
            log.info("JWT Token Filter is running... - token: {}", token);
            // 토큰 위조검사 및 인증 완료 처리
            if (token != null) {
                // 토큰 서명 위조 검사와 토큰을 파싱해서 클레임을 얻어내는 작업.
                TokenUserInfo tokenUserInfo = tokenProvider.validateAndGetTokenUserInfo(token);
                // spring security에게 전달할 인가 정보 리스트를 생성.
                // 권한이 여러 개 존재할 경우 리스트로 권한 체크에 사용할 필드를 add
                // 우리는 Role 타입의 필드 하나만으로 권한을 체크하기 때문에 하나만 add, 여러개라면 여러 개 add 하세요.
                List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
                authorityList.add(new SimpleGrantedAuthority("ROLE_" + tokenUserInfo.getRole().toString()));
                log.info("{}", authorityList.get(0).toString());

                // 인증 완료 처리
                // spring security에게 인증정보를 전달해서 전역적으로 어플리케이션 내에서
                // 인증 정보를 활용할 수 있게 설정.
                AbstractAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        tokenUserInfo, // 컨트롤러에서 활용할 유저 정보
                        null, // 인증된 사용자의 비밀번호 - 보통 null값
                        authorityList // 인가 정보 (권한 정보)
                );
                // 인증 완료 처리 시 클라이언트의 요청 정보를 세팅
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 스프링 시큐리티 컨테이너에 인증 정보 객체를 등록
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("서명이 일치하지 않습니다! 토큰이 위조 되었습니다!");
        }
        // 필터 체인에 내가 만든 필터 실행 명령
        filterChain.doFilter(request, response);
    }
    private String parseBearerToken(HttpServletRequest request) {
        // 요청 헤더에서 토큰 꺼내오기
        // -- content-type: application/json
        // -- Authorization: Bearer aslkdblk2dnkln34kl52...
        String bearerToken = request.getHeader("Authorization");
        // 요청 헤더에서 가져온 토큰은 순수 토큰 값이 아닌
        // 앞에 Bearer가 붙어있으니 이것을 제거하는 작업.
        if (StringUtils.hasText(bearerToken)
                && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}