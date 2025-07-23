package org.scoula.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scoula.config.MailConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MailConfig.class, EmailService.class})
@Log4j2
class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void sendVerificationCode() {
        String testEmail = "du123kim@naver.com";
        String code = emailService.sendVerificationCode(testEmail);
        log.info("발송된 인증 코드: {}", code);
        assertNotNull(code);
    }
}
