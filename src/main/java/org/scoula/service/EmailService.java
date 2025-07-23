package org.scoula.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailService {

    private final JavaMailSender mailSender;

    public String sendVerificationCode(String email) {
        String code = generateCode();

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email); // 수신자
            message.setSubject("[Zibi] 이메일 인증 코드");
            message.setText(buildMessageBody(code));
            message.setFrom("zibi_official@naver.com");

            mailSender.send(message);
            log.info("인증 코드 전송 완료");

        } catch (Exception e) {
            log.error("메일 전송 실패");
            throw new RuntimeException("메일 전송 실패", e);
        }

        return code;
    }

    private String buildMessageBody(String code) {
        return String.format(
                "안녕하세요.\n\n요청하신 인증 코드는 [%s] 입니다.\n5분 이내에 입력해 주세요.\n\n감사합니다😊.", code
        );
    }

    private String generateCode() {
        // UUID 대신 숫자+문자 조합 6자리 랜덤 코드
        return Long.toHexString(Double.doubleToLongBits(Math.random())).substring(0, 6).toUpperCase();
    }
}
