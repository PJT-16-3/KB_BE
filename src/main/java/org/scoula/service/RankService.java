package org.scoula.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.AptDetailDTO;
import org.scoula.dto.ChungyakAccountDTO;
import org.scoula.dto.GaScoreDTO;
import org.scoula.dto.swagger.Rank.RankAreaResponseDTO;
import org.scoula.mapper.AccountMapper;
import org.scoula.mapper.AptMapper;
import org.scoula.mapper.GaScoreMapper;
import org.scoula.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class RankService {

    private final AptMapper aptMapper;
    private final AccountMapper accountMapper;
    private final UserMapper userMapper;
    private final GaScoreMapper gaScoreMapper;

    public RankAreaResponseDTO calculateApartmentRank(int userIdx, String pblancNo) {
        // 1. 공고 정보 조회
        AptDetailDTO aptDetail = aptMapper.getAptDetails(pblancNo);
        if (aptDetail == null) {
            throw new IllegalArgumentException("해당 공고를 찾을 수 없습니다.");
        }

        String houseType = aptDetail.getHouseDtlSecdNm(); // "민영" or "국민"
        String specltRdnEarthAt = aptDetail.getSpecltRdnEarthAt(); // "Y" or "N"
        String subscrptArea = aptDetail.getSubscrptAreaCodeNm();

        // 2. 사용자 계좌 정보 조회
        ChungyakAccountDTO account = accountMapper.findAccountByUserIdx(userIdx);
        if (account == null) {
            throw new IllegalStateException("사용자 청약 계좌 정보가 없습니다.");
        }
        int resFinalRoundNo = Integer.parseInt(account.getResFinalRoundNo());
        int resAccountBalance = Integer.parseInt(account.getAccountBalance());

        // 3. 사용자 가점 정보 조회
        GaScoreDTO gaScore = gaScoreMapper.findGaScoreByUserIdx(userIdx);
        if (gaScore == null) {
            throw new IllegalStateException("사용자 가점 정보가 없습니다.");
        }

        Integer paymentPeriod = gaScore.getPaymentPeriod();
        LocalDate residenceStartDate = null;
        if (gaScore.getResidenceStartDate() != null) {
            YearMonth ym = YearMonth.parse(
                    gaScore.getResidenceStartDate(),
                    DateTimeFormatter.ofPattern("yyyy-MM")
            );
            residenceStartDate = ym.atDay(1);
        }
        String address = userMapper.findUserRegionByIdx(userIdx);

        // 4. 면적별 순위 계산
        Map<String, String> rankMap = new LinkedHashMap<>();
        rankMap.put("85 이하", checkRank(85, houseType, specltRdnEarthAt, subscrptArea,
                resFinalRoundNo, resAccountBalance, paymentPeriod, residenceStartDate, address));
        rankMap.put("102 이하", checkRank(102, houseType, specltRdnEarthAt, subscrptArea,
                resFinalRoundNo, resAccountBalance, paymentPeriod, residenceStartDate, address));
        rankMap.put("135 이하", checkRank(135, houseType, specltRdnEarthAt, subscrptArea,
                resFinalRoundNo, resAccountBalance, paymentPeriod, residenceStartDate, address));
        rankMap.put("모든 면적", checkRank(999, houseType, specltRdnEarthAt, subscrptArea,
                resFinalRoundNo, resAccountBalance, paymentPeriod, residenceStartDate, address));

        return RankAreaResponseDTO.builder()
                .rankByArea(rankMap)
                .build();
    }

    private String checkRank(int areaLimit, String houseType, String specltRdnEarthAt, String subscrptArea,
                             int resFinalRoundNo, int resAccountBalance, Integer paymentPeriod,
                             LocalDate residenceStartDate, String address) {

        int requiredDeposit = getRequiredDeposit(address, areaLimit);

        if ("민영".equals(houseType)) {
            if (resAccountBalance < requiredDeposit) return "2순위";

            if ("Y".equals(specltRdnEarthAt)) {
                if (paymentPeriod != null && paymentPeriod >= 24) {
                    if (address != null && address.startsWith(subscrptArea) &&
                            residenceStartDate != null &&
                            Period.between(residenceStartDate, LocalDate.now()).getYears() >= 2) {
                        return "1순위";
                    }
                    return "2순위";
                }
                return "2순위";
            } else {
                if ((subscrptArea.equals("서울") || subscrptArea.equals("경기") || subscrptArea.equals("인천"))
                        && paymentPeriod != null && paymentPeriod >= 12) {
                    return "1순위";
                }
                if (paymentPeriod != null && paymentPeriod >= 6) {
                    return "1순위";
                }
                return "2순위";
            }
        } else if ("국민".equals(houseType)) {
            if ("Y".equals(specltRdnEarthAt)) {
                return (paymentPeriod != null && paymentPeriod >= 24 && resFinalRoundNo >= 24) ? "1순위" : "2순위";
            } else {
                if ((subscrptArea.equals("서울") || subscrptArea.equals("경기") || subscrptArea.equals("인천"))) {
                    return (paymentPeriod != null && paymentPeriod >= 12 && resFinalRoundNo >= 2) ? "1순위" : "2순위";
                } else {
                    return (paymentPeriod != null && paymentPeriod >= 6 && resFinalRoundNo >= 6) ? "1순위" : "2순위";
                }
            }
        }
        return "2순위";
    }

    private int getRequiredDeposit(String region, int homeSize) {
        if (homeSize <= 85) {
            if (region.startsWith("서울") || region.startsWith("부산")) return 3000000;
            if (isMetropolitan(region)) return 2500000;
            return 2000000;
        } else if (homeSize <= 102) {
            if (region.startsWith("서울") || region.startsWith("부산")) return 6000000;
            if (isMetropolitan(region)) return 4000000;
            return 3000000;
        } else if (homeSize <= 135) {
            if (region.startsWith("서울") || region.startsWith("부산")) return 10000000;
            if (isMetropolitan(region)) return 7000000;
            return 4000000;
        } else {
            if (region.startsWith("서울") || region.startsWith("부산")) return 15000000;
            if (isMetropolitan(region)) return 10000000;
            return 5000000;
        }
    }

    private boolean isMetropolitan(String region) {
        return region != null && (region.startsWith("서울") || region.startsWith("부산") ||
                region.startsWith("인천") || region.startsWith("광주") ||
                region.startsWith("대구") || region.startsWith("대전") ||
                region.startsWith("울산"));
    }
}
