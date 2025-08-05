package org.scoula.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.scoula.dto.AptDetailDTO;
import org.scoula.dto.ChungyakAccountDTO;
import org.scoula.dto.GaScoreDTO;
import org.scoula.dto.swagger.Rank.RankAreaResponseDTO;
import org.scoula.mapper.AccountMapper;
import org.scoula.mapper.AptMapper;
import org.scoula.mapper.GaScoreMapper;
import org.scoula.mapper.UserMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class RankServiceTest {

    @Mock
    private AptMapper aptMapper;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private UserMapper userMapper;
    @Mock
    private GaScoreMapper gaScoreMapper;

    @InjectMocks
    private RankService rankService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculateApartmentRank() {
        int userIdx = 1;
        String pblancNo = "2025000306";

        // apt detail mock
        AptDetailDTO aptDetail = new AptDetailDTO();
        aptDetail.setHouseDtlSecdNm("민영");
        aptDetail.setSpecltRdnEarthAt("N");
        aptDetail.setSubscrptAreaCodeNm("서울");
        when(aptMapper.getAptDetails(pblancNo)).thenReturn(aptDetail);

        // account mock
        ChungyakAccountDTO account = new ChungyakAccountDTO();
        account.setResFinalRoundNo("12");
        account.setAccountBalance("1000");
        when(accountMapper.findAccountByUserIdx(userIdx)).thenReturn(account);

        // gaScore mock
        GaScoreDTO gaScore = new GaScoreDTO();
        gaScore.setPaymentPeriod(15);
        gaScore.setResidenceStartDate("2020-01");
        when(gaScoreMapper.findGaScoreByUserIdx(userIdx)).thenReturn(gaScore);

        // user mock
        when(userMapper.findUserRegionByIdx(userIdx)).thenReturn("서울 강남구");

        // when
        RankAreaResponseDTO result = rankService.calculateApartmentRank(userIdx, pblancNo);

        // then
        assertNotNull(result);
        assertEquals("3순위", result.getRankByArea().get("85 이하"));
    }
}
