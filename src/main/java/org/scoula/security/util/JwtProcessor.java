package org.scoula.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProcessor {
    private static final long ACCESS_TOKEN_VALID_MILLISECOND = 1000L * 60 * 5; // 5분
    private static final long REFRESH_TOKEN_VALID_MILLISECOND = 1000L * 60 * 60 * 24 * 7; // 7일

    private String secretKey = "충분히 긴 임의의(랜덤한) 비밀키 문자열 배정";
    private Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    //private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    //JWT Access 토큰 생성
    public String generateAccessToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime()+ACCESS_TOKEN_VALID_MILLISECOND))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //RefreshToken 설치
    public String generateRefreshToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_MILLISECOND))
                .signWith(key)
                .compact();
    }


    //JWT(JSON Web Token)에서 사용자 이름(username)을 추출하는 기능을 수행합니다.
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // 서명 검증을 위한 키 설정
                .build()  // JWT 파서 생성
                .parseClaimsJws(token) // JWT 토큰 파싱
                .getBody() // JWT의 payload(내용) 부분 가져오기
                .getSubject();// subject 클레임 값 추출

    }

    // JWT 검증(유효기간검증)-해석불가인경우예외발생
    public boolean validateToken(String token) {
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return true;
    }

}
