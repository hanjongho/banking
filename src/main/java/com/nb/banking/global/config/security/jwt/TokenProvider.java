package com.nb.banking.global.config.security.jwt;

import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import com.nb.banking.global.config.redis.RedisService;
import com.nb.banking.global.config.security.dto.TokenResponseDto;
import com.nb.banking.global.error.ErrorCode;
import com.nb.banking.global.error.exception.BadRequestException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider implements InitializingBean {
    private final RedisService redisService;

    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    /** 토큰 유효 시간 (ms) */
    private static final long JWT_EXPIRATION_MS = 1000L * 60 * 40; //40분
    private static final long REFRESH_TOKEN_EXPIRATION_MS = 1000L * 60 * 60 * 24 * 7; //7일
    private static final String AUTHORITIES_KEY = "auth";

    @Value("${jwt.secret}")
    private String secret;

    private Key key;

    // Initializing을 implements를 해서 afterPropertiesSet을 override한 이유?
    // 빈이 생성이 되고 주입을 받은 후 secret값을 Base64 Decode해서 key 변수에 할당
    @Override
    public void afterPropertiesSet() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // accessToken
    public String generateAccessToken(String email) {

        //권한 가져오기
        final Date now = new Date();
        final Date accessTokenExpiresIn = new Date(now.getTime() + JWT_EXPIRATION_MS);

        return Jwts.builder()
                .setIssuedAt(now) // 생성일자 지정(현재)
                .setSubject(email) // 사용자(principal => email)
                .claim(AUTHORITIES_KEY, "ROLE_USER") //권한 설정
                .setExpiration(accessTokenExpiresIn) // 만료일자
                .signWith(key, SignatureAlgorithm.HS512) // signature에 들어갈 secret 값 세팅
                .compact();
    }

    public String generateRefreshToken(String email) {
        final Date now = new Date();
        final Date refreshTokenExpiresIn = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_MS);

        final String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        //redis에 해당 userId 의 리프레시 토큰 등록
        redisService.setValues(
                email,
                refreshToken,
                Duration.ofMillis(REFRESH_TOKEN_EXPIRATION_MS)
        );

        return refreshToken;
    }

    // 로그인시 토큰 생성
    public TokenResponseDto generateToken(String email)
            throws HttpServerErrorException.InternalServerError {
        //권한 가져오기
        final String accessToken = generateAccessToken(email);
        final String refreshToken = generateRefreshToken(email);

        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // Token을 이용한 Authentication 객체 리턴
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 토큰의 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException ex) {
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    /**
     * JWT 유효성 검사 오버로딩, 에러 커스텀을 위한 함수
     * @param token 검사하려는 JWT 토큰
     * @returns boolean
     * */
    public boolean validateToken(HttpServletRequest request, String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException ex) {
            log.error("잘못된 JWT 서명입니다");
            request.setAttribute("exception", ErrorCode.INVALID_SIGNATURE.getCode());
        } catch (ExpiredJwtException ex) {
            log.error("만료된 JWT 토큰입니다");
            request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN.getCode());
        } catch (UnsupportedJwtException ex) {
            log.error("지원하지 않는 JWT 토큰입니다");
            request.setAttribute("exception", ErrorCode.UNSUPPORTED_TOKEN.getCode());
        } catch (IllegalArgumentException ex) {
            log.error("JWT 토큰이 비어있습니다");
            request.setAttribute("exception", ErrorCode.NO_TOKEN.getCode());
        }
        return false;
    }

    /** Redis Memory 의 RefreshToken 과
     * User 의 RefreshToken 이 일치하는지 확인
     * @param email 검증하려는 유저 이메일
     * @param refreshToken 검증하려는 리프레시 토큰
     * @return
     */
    public boolean validateRefreshToken(String email, String refreshToken) {
        String redisRt = redisService.getValues(email);
        if (!refreshToken.equals(redisRt)) {
            throw new BadRequestException(ErrorCode.EXPIRED_TOKEN);
        }
        return true;
    }

    /**
     * 토큰 예외 중 만료 상황만 검증 함수
     * @param token 검사하려는 JWT 토큰
     * @returns boolean
     * */
    public boolean validateTokenExceptExpiration(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch(ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * JWT 토큰에서 claims 추출
     * @param accessToken 추출하고 싶은 AccessToken (JWT)
     * @return Claims
     */
    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
