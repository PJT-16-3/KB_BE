package org.scoula.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.domain.AlarmType;
import org.scoula.dto.AlarmDetailDTO;
import org.scoula.dto.AlarmListDTO;
import org.scoula.mapper.AccountMapper;
import org.scoula.mapper.AlarmMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmMapper alarmMapper;
    private final AccountMapper accountMapper;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    /* ============ 생성 ============ */

    private AlarmDetailDTO base(String title, String content, Integer usersIdx) {
        return AlarmDetailDTO.builder()
                .title(Objects.requireNonNull(title, "title"))
                .content(Objects.requireNonNull(content, "content"))
                .usersIdx(Objects.requireNonNull(usersIdx, "usersIdx"))
                .alarmDate(LocalDate.now(KST))
                .alarmTime(LocalTime.now(KST))
                .isRead(false)   // 기본 미읽음
                .build();
    }

    /** 1) 새로운 청약 공고 알람: isRouting=false, link 옵션 */
    @Transactional
    public Long createNewNotice(String title, String content, String link,String houseType ,Integer usersIdx) {
        var dto = base(title, content, usersIdx);
        dto.setAlarmType(AlarmType.NEW_NOTICE);
        dto.setRouting(false);
        dto.setLink(link); // null 허용
        dto.setHouseType(houseType);
        alarmMapper.insertDetail(dto);
        log.info("[NEW_NOTICE] DB Insert 완료,{}", dto);
        return dto.getAlarmIdx();
    }

    /** 2) 청약 접수 시작 알람: isRouting=true, link 권장 */
    @Transactional
    public Long createApplicationStart(String title, String content, String link,String houseType ,Integer usersIdx) {
        AlarmDetailDTO dto = base(title, content, usersIdx);
        dto.setAlarmType(AlarmType.APPLICATION_START);
        dto.setRouting(true);
        dto.setLink(link);
        dto.setHouseType(houseType);// null 가능하나 라우팅이면 넣는 것을 권장
        alarmMapper.insertDetail(dto);
        log.info("[APPLICATION_START] DB Insert 완료, {}", dto);
        return dto.getAlarmIdx();
    }

    /** 3) 예치금 미납 알람: isRouting=false, link=null 고정 */
    @Transactional
    public Long createDepositUnpaid(String title, String content, Integer usersIdx) {
        var dto = base(title, content, usersIdx);
        dto.setAlarmType(AlarmType.DEPOSIT_UNPAID);
        dto.setRouting(false);
        dto.setLink(null);
        alarmMapper.insertDetail(dto);
        log.info("[DEPOSIT_UNPAID] DB Insert 완료, alarmIdx={}", dto);
        return dto.getAlarmIdx();
    }

    /* ============ 조회 ============ */

    /** 목록 조회 (onlyUnread=true면 미읽음만) */
    @Transactional(readOnly = true)
    public List<AlarmListDTO> getAlarmList(Integer usersIdx, boolean onlyUnread) {
        return alarmMapper.getAlarmList(usersIdx, onlyUnread);
    }

    /** 상세 조회 */
    @Transactional(readOnly = true)
    public AlarmDetailDTO getAlarmDetail(Long alarmIdx, Integer usersIdx) {
        return alarmMapper.getAlarmDetail(alarmIdx, usersIdx);
    }

    /* ============ 상태 변경/삭제 ============ */

    /** 단건 읽음 처리 */
    @Transactional
    public boolean markRead(Long alarmIdx, Integer usersIdx) {
        return alarmMapper.markRead(alarmIdx, usersIdx) > 0;
    }

    /** 전체 읽음 처리 */
    @Transactional
    public int markAllRead(Integer usersIdx) {
        return alarmMapper.markAllRead(usersIdx);
    }

    /** 단건 삭제 */
    @Transactional
    public boolean delete(Long alarmIdx, Integer usersIdx) {
        return alarmMapper.delete(alarmIdx, usersIdx) > 0;
    }

    /** 사용자 전체 알람 삭제 */
    @Transactional
    public int deleteAllByUser(Integer usersIdx) {
        return alarmMapper.deleteAllByUser(usersIdx);
    }

    public void saveFcmToken(Integer userIdx, String fcmToken) {
        if (userIdx == null || userIdx <= 0) {
            throw new IllegalArgumentException("userIdx는 필수이며 0보다 커야 합니다.");
        }
        if (fcmToken == null || fcmToken.trim().isEmpty()) {
            throw new IllegalArgumentException("fcmToken은 비어 있을 수 없습니다.");
        }

        try {
            int updatedRows = accountMapper.updateFcmToken(userIdx, fcmToken);
            if (updatedRows == 0) {
                throw new IllegalStateException("해당 userIdx에 해당하는 사용자가 없습니다.");
            }

        } catch (Exception e) {
            log.error("FCM 토큰 저장 중 오류 발생: userIdx={}, token={}", userIdx, fcmToken, e);
            throw new RuntimeException("FCM 토큰 저장 중 오류가 발생했습니다.", e);
        }
    }
}
