package org.scoula.service.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.scoula.domain.User;
import org.scoula.dto.oauth.KakaoUserInfoDto;
import org.scoula.mapper.UserMapper;
import org.scoula.security.util.JwtProcessor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
//import org.scoula.domain.user.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOauthService {
    private final JwtProcessor jwtProcessor;
    private final RestTemplate restTemplate = new RestTemplate();   // Spring에서 제공하는 HTTP 통신용 클라이언트 클래스, Rest API 서버와 GET, POST, PUT DELETE 등 요청을 주고 받을때 사용
    private final ObjectMapper objectMapper = new ObjectMapper();   // Java 객체 ↔ JSON 문자열 변환을 담당
    private final UserMapper userMapper;

    @Value("${kakao.rest_key}")
    private String REST_API_KEY;

    @Value("${kakao.redirect_url}")
    private String REDIRECT_URL;

    public KakaoUserInfoDto processKakaoLogin(String code) {
        String accessToken = this.getAccessToken(code);
        KakaoUserInfoDto userInfo = this.getUserInfo(accessToken);

        User user = this.processKakaoUser(userInfo);

        // JWT 발급 (JwtProcessor 사용)
        String jwtToken = jwtProcessor.generateAccessToken(user.getUserId());
        userInfo.setToken(jwtToken);

        return userInfo;
    }


    public String getAccessToken(String authorizationCode) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();    // HTTP 요청/응답 헤더를 다루기 위한 객체
        headers.setContentType((MediaType.APPLICATION_FORM_URLENCODED));    // 서버에 데이터를 보낼때, 데이터가 어떤형식인지 알려주는 것,
                                                                            // 브라우저에서 <form>을 사용해 데이터를 전송할때, application/x-www-form-urlencoded 방식이 사용됨
                                                                            // application/x-www-form-urlencoded는 key=value&key2=value2 이 형식이라는 의미
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>(); // MultiValueMap은 하나의 key에 여러 개의 value를 저장할 수 있는 자료구조
        params.add("grant_type", "authorization_code");     // 인가 코드 방식을 사용한다는 의미
        params.add("client_id", REST_API_KEY);
        params.add("redirect_uri", REDIRECT_URL);
        params.add("code", authorizationCode);              // 카카오가 준 1회성 인가 코드, 이 코드를 사용해 Access Token 교환

        // 전체 요정 URL 확인용
        String fullUrl = tokenUrl + "?" +
                "grant_type=authorization_code" +
                "&client_id=" + REST_API_KEY +
                "&redirect_uri=" + REDIRECT_URL +
                "&code=" + authorizationCode;

        log.info("카카오 토큰 요청 전체 URL: {}", fullUrl);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);  // HttpEntity는 HTTP 요청/응답의 body(params)와 headers를 함께 담는 객체
                                                                                                // params: 폼 데이터 (key=value&key2=value2 형식), headers: 요청 헤더 (Content-Type, Authorization 등)

        // postForEntity()는 HTTP POST 요청을 보내고 응답을 받아오는 함수, ResponseEntity(응답 상태코드, 헤더, 본문 포함)를 반환
        //postForEntity(요청URL, 요청객체, 응답타입)
        ResponseEntity<String> response = restTemplate.postForEntity(
                tokenUrl, request, String.class
        );

        log.info("카카오 토큰 요청에 대한 전체 응답: {}", response.getBody());

        try {
            JsonNode root = objectMapper.readTree(response.getBody());  // objectMapper.readTree()는 응답 JSON 문자열을 JsonNode 형태로 변환, root는 트리구조로 접근 가능
            return root.get("access_token").asText();                   // "access_token" 키를 찾아 값을 추출
        } catch (Exception e) {
            log.error("카카오 토큰 요청 실패", e);
            throw new RuntimeException("카카오 토큰 요청 실패");
        }
    }

    public KakaoUserInfoDto getUserInfo(String accessToken) {
        String userUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                userUrl,
                HttpMethod.POST,
                request,
                String.class
        );

        log.info("카카오 토큰 요청에 대한 전체 응답: {}", response.getBody());

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            Long kakaoId = root.get("id").asLong();

            JsonNode kakaoAccount = root.get("kakao_account");
            String email = kakaoAccount.get("email").asText(null);

            JsonNode profile = kakaoAccount.get("profile");
            String nickname = profile.get("nickname").asText(null);
            String profileImageUrl = profile.get("profile_image_url").asText(null);

            return new KakaoUserInfoDto(kakaoId, email, nickname, profileImageUrl, null);
        } catch (Exception e) {
            log.error("카카오 사용자 정보 요청 실패", e);
            throw new RuntimeException("카카오 사용자 정보 요청 실패");
        }
    }

    // MyBatis로 사용자 DB 처리
    public User processKakaoUser(KakaoUserInfoDto userInfo) {
        User existingUser = userMapper.findById(userInfo.getEmail());

        if (existingUser != null) {
            int count = userMapper.countUserByIdx(existingUser.getUsersIdx());
            if (count == 0) {
                userMapper.insertUserAuth(existingUser.getUsersIdx());
            }
            return existingUser;
        }

        User kakaoUser = new User();
        kakaoUser.setUserId(userInfo.getEmail());
        kakaoUser.setUserName(userInfo.getNickname());
        kakaoUser.setPassword(null);

        userMapper.insertUser(kakaoUser);
        userMapper.insertUserAuth(kakaoUser.getUsersIdx());
        return kakaoUser;
    }
}