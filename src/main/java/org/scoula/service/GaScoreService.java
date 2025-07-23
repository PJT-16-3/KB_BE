package org.scoula.service;

import org.scoula.dto.GaScoreDTO;
import org.scoula.mapper.AccountMapper;
import org.scoula.mapper.GaScoreMapper;
import org.scoula.mapper.UserMapper;
import org.scoula.security.util.JwtProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class GaScoreService {

    @Autowired
    private JwtProcessor jwtProcessor;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GaScoreMapper gaScoreMapper;

    @Autowired
    private AccountMapper accountMapper;

    public void processAndSaveScore(String token, GaScoreDTO gaScoreDTO) {
        String userId = jwtProcessor.getUsername(token.replace("Bearer ", ""));
        int userIdx = userMapper.findUserIdxByUserId(userId);

        int noHouseScore = calculateNoHouseScore(gaScoreDTO.getNoHousePeriod());
        int dependentsScore = calculateDependentsScore(gaScoreDTO.getDependentsNm());
        int paymentPeriod = calculatePaymentPeriod(userIdx);

        gaScoreDTO.setNoHouseScore(noHouseScore);
        gaScoreDTO.setDependentsScore(dependentsScore);
        gaScoreDTO.setPaymentPeriod(paymentPeriod);

        gaScoreMapper.updateScore(userIdx, gaScoreDTO);
    }

    public int calculateTotalScore(String token) {
        String userId = jwtProcessor.getUsername(token.replace("Bearer ", ""));
        int userIdx = userMapper.findUserIdxByUserId(userId);

        GaScoreDTO gaScoreDTO = gaScoreMapper.getScoresByUserIdx(userIdx);

        return gaScoreDTO.getNoHouseScore() +
                gaScoreDTO.getDependentsScore() +
                gaScoreDTO.getPaymentPeriod();
    }

    private int calculateNoHouseScore(int noHousePeriod) {
        return Math.min(noHousePeriod * 2, 20);
    }

    private int calculateDependentsScore(int dependentsNm) {
        return Math.min(dependentsNm * 5, 35);
    }

    private int calculatePaymentPeriod(int userIdx) {
        String startDateStr = accountMapper.findEarliestAccountStartDate(userIdx);
        if (startDateStr == null) return 0;
        LocalDate startDate = LocalDate.parse(startDateStr);
        return (int) ChronoUnit.MONTHS.between(startDate, LocalDate.now());
    }
}
