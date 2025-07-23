package org.scoula.security.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scoula.config.RootConfig;
import org.scoula.security.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RootConfig.class, SecurityConfig.class })
@Log4j2
class JwtProcessorTest {
    @Autowired
    JwtProcessor jwtProcessor;

    @Test
    void generateAccessToken() {
        String username ="admin";
        String token = jwtProcessor.generateAccessToken(username);
        log.info("token: " + token);
    }

    @Test
    void generateRefreshToken() {
        String username ="admin";
        String token = jwtProcessor.generateRefreshToken(username);
        log.info("token: " + token);
    }

    @Test
    void testGetUsername() {

        String username = "user_id";
        String accessToken = jwtProcessor.generateAccessToken(username);
        String refreshToken = jwtProcessor.generateRefreshToken(username);

        String extractedFromAccessToken = jwtProcessor.getUsername(accessToken);
        String extractedFromRefreshToken = jwtProcessor.getUsername(refreshToken);


        assertEquals(username, extractedFromAccessToken);
        assertEquals(username, extractedFromRefreshToken);

        System.out.println("Access Token 에서 추출한 username: " + extractedFromAccessToken);
        System.out.println("Refresh Token 에서 추출한 username: " + extractedFromRefreshToken);
    }


    @Test
    void testValidateToken() {

        String username = "user_id";
        String accessToken = jwtProcessor.generateAccessToken(username);
        String refreshToken = jwtProcessor.generateRefreshToken(username);

        boolean accessTokenValid = jwtProcessor.validateToken(accessToken);
        boolean refreshTokenValid = jwtProcessor.validateToken(refreshToken);

        System.out.println("accessTokenValid: " + accessTokenValid);
        System.out.println("refreshTokenValid: " + refreshTokenValid);
    }

    @Test
    void isTokenExpired() throws InterruptedException {
        // 짧은 생명주기 2초 로 생성
        String username = "test@email.com";
        String secretKey = "충분히 긴 임의의(랜덤한) 비밀키 문자열 배정";
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        JwtProcessor shortLivedProcessor = new JwtProcessor() {
            @Override
            public String generateAccessToken(String subject) {
                return Jwts.builder()
                        .setSubject(subject)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 1000)) // 1초
                        .signWith(key)
                        .compact();
            }
        };

        String shortAccessToken = shortLivedProcessor.generateAccessToken(username);
        // 바로 만료 안 됨
        assertFalse(shortLivedProcessor.isTokenExpired(shortAccessToken), "토큰이 아직 만료되지 않아야 함");
        // 2초 대기 후 만료 체크
        Thread.sleep(2000); // 2초 대기
        assertTrue(shortLivedProcessor.isTokenExpired(shortAccessToken), "토큰이 만료되어야 함");
    }
}