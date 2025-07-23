package org.scoula.security.account.mapper;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.scoula.config.RootConfig;

import org.scoula.security.config.SecurityConfig;
import org.scoula.security.dto.AuthDTO;
import org.scoula.security.dto.MemberDTO;
import org.scoula.security.dto.RefreshTokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { RootConfig.class, SecurityConfig.class })
@Log4j2
public class UserDetailsMapperTest {
    @Autowired
    private UserDetailsMapper mapper;

    @Test
    public void get() {
        MemberDTO member=mapper.get("admin");
        log.info(member);
        for(AuthDTO auth : member.getAuthList()) {
            log.info(auth);
        }

    }

    @Test
    void updateRefreshToken() {
        RefreshTokenDTO refreshTokenDTO = new RefreshTokenDTO();
        refreshTokenDTO.setUser_id("admin");
        refreshTokenDTO.setJwt_refresh_token("test refresh token");
        mapper.updateRefreshToken(refreshTokenDTO);
        log.info("refresh token update success");
    }


    @Test
    void getRefreshToken() {
        String refreshToken=mapper.getRefreshToken("admin");
        log.info("refresh token : " + refreshToken);
        if(refreshToken==null) {
            log.info("refresh token is null");
        }else {
            log.info("refresh token is not null");
        }
    }

    @Test
    void clearRefreshToken() {
        mapper.clearRefreshToken("admin");
        log.info("refresh token clear success");
    }

    @Test
    void testUpdateRefreshToken() {
    }
}
