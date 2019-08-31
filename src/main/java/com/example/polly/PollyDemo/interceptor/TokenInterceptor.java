package com.example.polly.PollyDemo.interceptor;

import com.example.polly.PollyDemo.component.JwtFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("tokenInterceptor")
@RequiredArgsConstructor
public class TokenInterceptor extends HandlerInterceptorAdapter {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String MEMBER_ID = "memberId";

    private final JwtFactory jwtFactory;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Long memberId = jwtFactory.decodeToken(request.getHeader(AUTHORIZATION_HEADER)).orElse(null);
        if (memberId == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        request.setAttribute(MEMBER_ID, memberId);
        return true;
    }
}

