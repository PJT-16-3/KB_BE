package org.scoula.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailService {

    private final JavaMailSender mailSender;
    private final CacheManager cacheManager;

    public String sendVerificationCode(String email) {
        String code = generateCode();

        try {
            // 이메일 전송
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("[Zibi] 이메일 인증 코드");
            message.setText(buildMessageBody(code));
            message.setFrom("zibi_official@naver.com");
            mailSender.send(message);
            log.info("인증 코드 전송 완료: {}", email);

            // 캐시에 인증 코드 저장
            Cache cache = cacheManager.getCache("emailVerificationCache");
            if (cache != null) {
                cache.put(email, code);
                log.info("[sendVerificationCode] 캐시에 인증 코드 저장 완료: {}", code);
            } else {
                log.error("[sendVerificationCode] 캐시 emailVerificationCache를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            log.error("[sendVerificationCode] 메일 전송 실패", e);
            throw new RuntimeException("메일 전송 실패", e);
        }

        return code;
    }

    public String getCachedCode(String email) {
        Cache cache = cacheManager.getCache("emailVerificationCache");
        if (cache != null) {
            String cachedCode = cache.get(email, String.class);
            if (cachedCode != null) {
                log.info("[getCachedCode] 캐시에서 인증 코드 조회 성공: {}", cachedCode);
                return cachedCode;
            } else {
                log.warn("[getCachedCode] 캐시에 저장된 코드가 없습니다: {}", email);
            }
        } else {
            log.error("[getCachedCode] 캐시 emailVerificationCache를 찾을 수 없습니다.");
        }
        return null;
    }

    public void clearVerificationCode(String email) {
        Cache cache = cacheManager.getCache("emailVerificationCache");
        if (cache != null) {
            cache.evict(email);
            log.info("[clearVerificationCode] 캐시에서 인증 코드 삭제 완료: {}", email);
        } else {
            log.error("[clearVerificationCode] 캐시 emailVerificationCache를 찾을 수 없습니다.");
        }
    }

    private String buildMessageBody(String code) {
        return String.format(
                "안녕하세요.\n\n요청하신 인증 코드는 [%s] 입니다.\n5분 이내에 입력해 주세요.\n\n감사합니다.", code
        );
    }

    public String generateCode() {
        return Long.toHexString(Double.doubleToLongBits(Math.random()))
                .substring(0, 6).toUpperCase();
    }
}
