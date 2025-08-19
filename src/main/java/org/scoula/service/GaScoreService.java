package org.scoula.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.GaScoreDTO;
import org.scoula.dto.swagger.GaScore.SwaggerGaScoreRequest;
import org.scoula.mapper.AccountMapper;
import org.scoula.mapper.GaScoreMapper;
import org.scoula.mapper.UserMapper;
import org.scoula.security.util.JwtProcessor;
import org.scoula.util.TokenUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Log4j2
public class GaScoreService {

    private final GaScoreMapper gaScoreMapper;
    private final UserMapper userMapper;
    private final AccountMapper accountMapper;
    private final TokenUtils tokenUtils;
    private final JwtProcessor jwtProcessor;

    public GaScoreDTO saveGaScore(SwaggerGaScoreRequest requestDto, HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String accessToken = tokenUtils.extractAccessToken(bearerToken);

        String userId = jwtProcessor.getUsername(accessToken);
        int userIdx = userMapper.findUserIdxByUserId(userId);

        LocalDate birthDate = LocalDate.parse(requestDto.getBirthDate());
        LocalDate weddingDate = parseYearMonth(requestDto.getWeddingDate());
        LocalDate disposalDate = parseYearMonth(requestDto.getDisposalDate());

        int age = Period.between(birthDate, LocalDate.now()).getYears();

        int noHousePeriod;
        int noHouseScore;

        LocalDate thirtiethBirthday = birthDate.plusYears(30);
        LocalDate baseDate = null;


        if (requestDto.getHouseOwner() == 1) {
            noHousePeriod = 0;
            noHouseScore = 0;
        }
        else if (age < 30 && requestDto.getMaritalStatus() == 0 && disposalDate == null) {
            noHousePeriod = 0;
            noHouseScore = 0;
        }
        else {
            if (age < 30) {
                if (requestDto.getMaritalStatus() == 1 && weddingDate != null) {
                    baseDate = (disposalDate != null)
                            ? Stream.of(weddingDate, disposalDate).max(LocalDate::compareTo).get()
                            : weddingDate;
                } else {
                    baseDate = (disposalDate != null)
                            ? Stream.of(thirtiethBirthday, disposalDate).max(LocalDate::compareTo).get()
                            : thirtiethBirthday;
                }
            } else {
                baseDate = (disposalDate != null)
                        ? Stream.of(thirtiethBirthday, disposalDate).max(LocalDate::compareTo).get()
                        : thirtiethBirthday;
            }

            noHousePeriod = Math.max(0, (int) ChronoUnit.YEARS.between(baseDate, LocalDate.now()));
            noHouseScore = calculateNoHouseScore(noHousePeriod);
        }

        int dependentsScore = Math.min((requestDto.getDependentsNm() + 1) * 5, 35);
        int paymentPeriod = calculatePaymentPeriod(userIdx);
        int paymentPeriodScore = calculatePaymentPeriodScore(paymentPeriod);
        int totalScore = noHouseScore + dependentsScore + paymentPeriodScore;

        GaScoreDTO responseDTO = GaScoreDTO.builder()
                .noHousePeriod(noHousePeriod)
                .noHouseScore(noHouseScore)
                .dependentsNm(requestDto.getDependentsNm())
                .dependentsScore(dependentsScore)
                .headOfHousehold(requestDto.getHeadOfHousehold())
                .houseOwner(requestDto.getHouseOwner())
                .houseDisposal(requestDto.getHouseDisposal())
                .disposalDate(disposalDate != null ?
                        disposalDate.format(DateTimeFormatter.ofPattern("yyyy-MM")) : null)
                .maritalStatus(requestDto.getMaritalStatus())
                .weddingDate(weddingDate != null ?
                        weddingDate.format(DateTimeFormatter.ofPattern("yyyy-MM")) : null)
                .birthDate(requestDto.getBirthDate())
                .residenceStartDate(requestDto.getResidenceStartDate())
                .paymentPeriod(paymentPeriod)
                .paymentPeriodScore(paymentPeriodScore)
                .totalGaScore(totalScore)
                .build();

        gaScoreMapper.insertGaScore(responseDTO, userIdx);
        log.info("사용자 {} 청약 가점: totalScore={}", userIdx, totalScore);

        return responseDTO;
    }

    public GaScoreDTO getGaScore(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        String accessToken = tokenUtils.extractAccessToken(bearerToken);

        String userId = jwtProcessor.getUsername(accessToken);
        int userIdx = userMapper.findUserIdxByUserId(userId);

        GaScoreDTO dto = gaScoreMapper.findGaScoreByUserIdx(userIdx);

        if (dto != null) {
            log.info("사용자 {} 청약 가점 조회 완료: totalScore={}", userIdx, dto.getTotalGaScore());
        } else {
            log.warn("사용자 {} 청약 가점 조회 실패 - 데이터 없음", userIdx);
        }

        return dto;
    }

    private LocalDate parseYearMonth(String yearMonth) {
        if (yearMonth == null || yearMonth.isEmpty()) return null;
        return YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM")).atDay(1);
    }

    private int calculatePaymentPeriod(int userIdx) {
        LocalDate startDate = accountMapper.findAccountStartDate(userIdx);
        if (startDate == null) return 0;
        return (int) ChronoUnit.MONTHS.between(startDate, LocalDate.now());
    }

    private int calculatePaymentPeriodScore(int months) {
        if (months < 6) return 1;
        if (months < 12) return 2;
        int years = months / 12;
        return Math.min(years + 2, 17);
    }

    private int calculateNoHouseScore(int years) {
        if (years == 0) return 0;
        if (years < 1) return 2;
        return Math.min(years * 2, 32);
    }
}
