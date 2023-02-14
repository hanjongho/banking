package com.nb.banking.global.config.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nb.banking.global.error.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenProvider tokenProvider;

    // doFilter의 역할? -> 토큰의 인증정보를 SecurityContext에 저장하는 역할
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        //Spring Security 에 저장되어 있지 않으면 헤더에서 jwt 토큰을 가지고옴
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String jwt = resolveToken(request); //request에서 jwt 토큰을 꺼낸다.

                if (jwt == null) {
                    // log.error("jwt 값을 가져올 수 없습니다");
                    request.setAttribute("exception", ErrorCode.NO_TOKEN.getCode());
                    filterChain.doFilter(request, response);
                    return;
                }

                if (StringUtils.isNotBlank(jwt) && tokenProvider.validateToken(jwt)) {
                    Authentication authentication = tokenProvider.getAuthentication(jwt); //authentication 획득

                    //Security 세션에서 계속 사용하기 위해 SecurityContext에 Authentication 등록
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    if (StringUtils.isBlank(jwt)) {
                        request.setAttribute("exception", ErrorCode.NO_TOKEN.getCode());
                    }

                    tokenProvider.validateToken(request, jwt);
                }
            } catch (Exception ex) {
                logger.error("Security Context에 해당 토큰을 등록할 수 없습니다", ex);
            }
        }
        else {
            log.debug("SecurityContextHolder not populated with security token, as it already contained: '{}'",
                    SecurityContextHolder.getContext().getAuthentication());
        }

        filterChain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보를 꺼내오기 위함의 resolveToken
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.isNotBlank(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}
