package org.scoula.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.swagger.Auth.SwaggerPasswordResetRequestDTO;
import org.scoula.mapper.UserMapper;
import org.scoula.security.dto.AuthDTO;
import org.scoula.security.dto.MemberDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public MemberDTO getUser(String email) {
        return userMapper.findById(email);
    }

    public List<MemberDTO> getAllUsers() {
        return userMapper.findAll();
    }

    @Transactional
    public void signUp(MemberDTO memberDTO) {

        String originalPassword = memberDTO.getPassword();
        String newPassword = passwordEncoder.encode(originalPassword);
        memberDTO.setPassword(newPassword);
        userMapper.insertUser(memberDTO);
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsersIdx(memberDTO.getUsersIdx());
        authDTO.setAuth("ROLE_MEMBER");
        userMapper.insertAuth(authDTO);
        userMapper.insertUserInfo(memberDTO.getUsersIdx());
    }

//    @Transactional
    public void updateUser(MemberDTO user) {
        // usersIdx가 0 이하이거나 비어있으면 예외
        if (user.getUsersIdx() <= 0) {
            throw new IllegalArgumentException("usersIdx는 필수입니다.");
        }

        // userId가 null 이거나 비어있으면 예외
        if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("userId는 필수입니다.");
        }

        // 필수 값 유효성 검사
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("userName은 필수 입력값입니다.");
        }
        if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("address는 필수 입력값입니다.");
        }
        if (user.getBirthdate() == null) {
            throw new IllegalArgumentException("birthdate는 필수 입력값입니다.");
        }

        userMapper.updateUser(user);
    }


    public void deleteUser(String userId, String inputPassword) {
        // 1. DB에서 회원 정보 조회
        MemberDTO user = userMapper.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("해당 사용자가 존재하지 않습니다.");
        }

        // 2. 입력한 비밀번호와 암호화된 비밀번호 비교
        if (!passwordEncoder.matches(inputPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        // 3. 비밀번호가 맞으면 삭제
        userMapper.deleteUser(userId);
        log.info("회원 탈퇴 완료 - userId: {}", userId);
    }

    public MemberDTO findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public Integer findUserIdxByUserId (String usersId) {
        return userMapper.findUserIdxByUserId(usersId);
    }

    public void updatePassword(String userId, String oldPassword, String newPassword) {
        // 1. 기존 사용자 조회
        MemberDTO member = userMapper.findById(userId);
        if (member == null) {
            throw new IllegalStateException("비밀번호 변경 실패: 해당 사용자가 존재하지 않음");
        }

        // 2. 기존 비밀번호 비교
        if (!passwordEncoder.matches(oldPassword, member.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        // 3. 새 비밀번호가 기존 비밀번호와 동일한지 확인
        if (passwordEncoder.matches(newPassword, member.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호와 새 비밀번호가 동일합니다.");
        }

        // 4. 새 비밀번호 암호화 후 업데이트
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        member.setPassword(encodedNewPassword);

        int updated = userMapper.updatePasswordByUsername(member);
        if (updated == 0) {
            throw new IllegalStateException("비밀번호 변경 실패: 업데이트 실패");
        }
    }

    public void resetPassword(SwaggerPasswordResetRequestDTO request) {
        try {
            String encodedPassword = passwordEncoder.encode(request.getNewPassword());
            request.setNewPassword(encodedPassword);

            int updated = userMapper.resetPassword(request);
            if (updated == 0) {
                throw new IllegalStateException("해당 사용자 ID를 찾을 수 없습니다.");
            }

            log.info("비밀번호 초기화 완료 - userId: {}, newPassword: {}", request.getUserId(), request.getNewPassword());

        } catch (Exception e) {
            log.error("비밀번호 초기화 중 오류 발생 - userId: {}", request.getUserId(), e);
            throw new RuntimeException("비밀번호 초기화 처리 중 서버 오류가 발생했습니다.");
        }
    }



}


