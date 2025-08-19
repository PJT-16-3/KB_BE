package org.scoula.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.scoula.dto.*;
import org.scoula.mapper.*;
import org.scoula.security.util.JwtProcessor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class RecommendationService {
    private final PythonApiService pythonApiService;
    private final JwtProcessor jwtProcessor;
    private final UserMapper userMapper;
    private final UserInfoMapper userInfoMapper;
    private final HouseListMapper houseListMapper;
    private final AptMapper aptMapper;
    private final SelectedMapper selectedMapper;

    /** 정렬을 위해 확률(정수 %)을 key 로 사용 */
    public final Map<Integer, RecommendationListDTO> wintProbability = new HashMap<>();

    public List<RecommendationListDTO> getRecommendationList(Integer usersIdx) {
        long t0 = System.currentTimeMillis();
        log.info("[REC] getRecommendationList start usersIdx={}", usersIdx);

        if (usersIdx == null) {
            log.error("[REC] usersIdx is null");
            return List.of();
        }

        UserInfoDTO userInfo = userInfoMapper.getUserInfoByUsersIdx(usersIdx);
        if (userInfo == null) {
            log.warn("[REC] userInfo not found for usersIdx={}", usersIdx);
            return List.of();
        }
        log.debug("[REC] userInfo={}", userInfo);

        PreferenceDTO preference = getPreferenceInfo(usersIdx, userInfo);
        if (preference == null) {
            log.error("[REC] preference is null for usersIdx={}", usersIdx);
            return List.of();
        }
        List<RegionDTO> siGungu = Optional.ofNullable(preference.getSiGunGu()).orElseGet(List::of);
        log.info("[REC] preference regions={} homeTypes={}", siGungu.size(), preference.getHomeType());

        wintProbability.clear();

        // 모든 선호 지역 순회
        for (RegionDTO region : siGungu) {
            if (region == null) continue;
            log.info("[REC] region loop si={}, sigungu={}", region.getSi(), region.getGunGu());

            // APT
            try {
                if (preference.getHomeType() != null && preference.getHomeType().contains("APT")) {
                    List<RecommendationListDTO> aptHouseList = houseListMapper.getAptRecommendationList(region, preference);
                    log.info("[REC] APT candidates={}", aptHouseList == null ? 0 : aptHouseList.size());
                    getAptReqDTO(usersIdx, Optional.ofNullable(aptHouseList).orElseGet(List::of), userInfo);
                }
            } catch (Exception e) {
                log.error("[REC] APT list failed for region={} error={}", region, e.toString(), e);
            }

            // 오피스텔
            try {
                if (preference.getHomeType() != null && preference.getHomeType().contains("오피스텔")) {
                    List<RecommendationListDTO> officetelHouseList = houseListMapper.getOfficetelRecommendationList(region, preference);
                    log.info("[REC] Officetel candidates={}", officetelHouseList == null ? 0 : officetelHouseList.size());
                    getOfficetelReqDTO(usersIdx, Optional.ofNullable(officetelHouseList).orElseGet(List::of), userInfo);
                }
            } catch (Exception e) {
                log.error("[REC] Officetel list failed for region={} error={}", region, e.toString(), e);
            }
        }

        // 정렬
        Map<Integer, RecommendationListDTO> sortedMap = new TreeMap<>(Collections.reverseOrder());
        sortedMap.putAll(wintProbability);

        // ✅ 순위 설정 추가
        int rank = 1;
        for (RecommendationListDTO dto : sortedMap.values()) {
            dto.setRank(rank++);
        }

        // 로그 출력
        for (Map.Entry<Integer, RecommendationListDTO> entry : sortedMap.entrySet()) {
            Integer key = entry.getKey();
            RecommendationListDTO house = entry.getValue();
            log.info("[REC] sortedMap key={} rank={} housePblancNo={} houseType={}",
                    key, house.getRank(), house.getPblancNo(), house.getHouseType());
        }

        List<RecommendationListDTO> winProbabilityList = new ArrayList<>(sortedMap.values());
        log.info("[REC] done. totalSelected={} elapsedMs={}", winProbabilityList.size(), (System.currentTimeMillis() - t0));
        return winProbabilityList;
    }

    public void getAptReqDTO(int usersIdx, List<RecommendationListDTO> aptHouseList, UserInfoDTO userInfo) {
        log.debug("[REC/APT] start items={}", aptHouseList.size());

        for (RecommendationListDTO house : aptHouseList) {
            if (house == null) continue;
            String pblancNo = house.getPblancNo();
            log.debug("[REC/APT] house pblancNo={}", pblancNo);

            try {
                PythonAptRequestDTO aptReqDTO = new PythonAptRequestDTO();

                int aptIdx = aptMapper.findAptIdxByPblancNo(pblancNo);

                aptReqDTO.setGnsplyHshldco(aptMapper.getAptSuplyHshldco(aptIdx));
                aptReqDTO.setSpsplyHshldco(aptMapper.getAptspsplyHshldco(aptIdx));
                aptReqDTO.setSi(house.getSi());
                aptReqDTO.setSigungu(house.getSigungu());
                int total = aptMapper.getAptSuplyHshldco(aptIdx) + aptMapper.getAptspsplyHshldco(aptIdx);
                aptReqDTO.setTotSuplyHshldco(total);
                aptReqDTO.setSuplyHshldco(total);

                Integer rank = selectedMapper.getUserRankByUserInfoIdx(userInfo.getUserInfoIdx());
                aptReqDTO.setHouseRank(rank);
                aptReqDTO.setScore(userInfo.getTotalGaScore());

                String userRegion = userMapper.findUserRegionByIdx(usersIdx);
                String aptRegion = aptMapper.findRegionByAptPblancNo(pblancNo);
                int resideSecd = (userRegion != null && aptRegion != null && userRegion.startsWith(aptRegion)) ? 1 : 2;
                aptReqDTO.setResideSecd(resideSecd);

                log.debug("[REC/APT] req={}", aptReqDTO);

                Map<String, Object> resp = pythonApiService.requestPrediction(aptReqDTO);
                Object probabilityObj = (resp == null) ? null : resp.get("probability");

                Integer probKey = toPercentInt(probabilityObj);
                if (probKey == null) {
                    log.warn("[REC/APT] probability null/invalid. pblancNo={} resp={}", pblancNo, resp);
                    continue;
                }
                wintProbability.put(probKey, house);
                log.info("[REC/APT] put prob={} pblancNo={}", probKey, pblancNo);

            } catch (Exception e) {
                log.error("[REC/APT] failed pblancNo={} usersIdx={} err={}", pblancNo, usersIdx, e.toString(), e);
                // continue next item
            }
        }
    }

    public void getOfficetelReqDTO(int usersIdx, List<RecommendationListDTO> officetelHouseList, UserInfoDTO userInfo) {
        log.debug("[REC/OFFI] start items={}", officetelHouseList.size());

        for (RecommendationListDTO house : officetelHouseList) {
            if (house == null) continue;
            String pblancNo = house.getPblancNo();
            log.debug("[REC/OFFI] house pblancNo={}", pblancNo);

            try {
                PythonOfficetelRequestDTO officetelReqDTO = new PythonOfficetelRequestDTO();

                int officetelIdx = aptMapper.findOfficetelIdxByPblancNo(pblancNo);
                OfficetelDetailDTO officetelDetail = aptMapper.getOfficetelDetails(pblancNo);
                OfficetelTypeDTO officeType = aptMapper.getOfficetelType(officetelIdx);
                if (officetelDetail == null || officeType == null) {
                    log.warn("[REC/OFFI] detail/type null. pblancNo={} officetelIdx={}", pblancNo, officetelIdx);
                    continue;
                }

                officetelReqDTO.setTotSuplyHshldco(safeInt(officetelDetail.getTotSuplyHshldco()));
                officetelReqDTO.setSuplyHshldco(safeInt(officeType.getSuplyHshldco()));
                officetelReqDTO.setSi(house.getSi());
                officetelReqDTO.setSigungu(house.getSigungu());

                String userRegion = userMapper.findUserRegionByIdx(usersIdx);
                String offiRegion = aptMapper.findRegionByOfficetelPblancNo(pblancNo);
                int resideSecd = (userRegion != null && offiRegion != null && userRegion.startsWith(offiRegion)) ? 1 : 2;
                officetelReqDTO.setResideSecd(resideSecd);

                log.debug("[REC/OFFI] req={}", officetelReqDTO);

                Map<String, Object> resp = pythonApiService.requestOfficetelPrediction(officetelReqDTO);
                Object probabilityObj = (resp == null) ? null : resp.get("probability");

                Integer probKey = toPercentInt(probabilityObj);
                if (probKey == null) {
                    log.warn("[REC/OFFI] probability null/invalid. pblancNo={} resp={}", pblancNo, resp);
                    continue;
                }
                wintProbability.put(probKey, house);
                log.info("[REC/OFFI] put prob={} pblancNo={}", probKey, pblancNo);

            } catch (Exception e) {
                log.error("[REC/OFFI] failed pblancNo={} usersIdx={} err={}", pblancNo, usersIdx, e.toString(), e);
            }
        }
    }

    public PreferenceDTO getPreferenceInfo (int usersIdx, UserInfoDTO userInfo) {
        try {
            int userInfoIdx = userInfo.getUserInfoIdx();
            List<RegionDTO> siGunGu = userInfoMapper.findSelectedSiGunGuByUserInfoIdx(userInfoIdx);
            Integer minHomeSize = userInfoMapper.findSelectedMinHomeSizeByUserInfoIdx(userInfoIdx);
            Integer maxHomeSize = userInfoMapper.findSelectedMaxHomeSizeByUserInfoIdx(userInfoIdx);
            List<String> selectedHomeType = userInfoMapper.findSelectedHomeTypeByUserInfoIdx(userInfoIdx);

            PreferenceDTO preference = new PreferenceDTO();
            preference.setUsersIdx(usersIdx);
            preference.setSiGunGu(Optional.ofNullable(siGunGu).orElseGet(List::of));
            preference.setMinHomeSize(minHomeSize == null ? 0 : minHomeSize);
            preference.setMaxHomeSize(maxHomeSize == null ? Integer.MAX_VALUE : maxHomeSize);
            preference.setHopeMaxPrice(userInfo.getHopeMaxPrice());
            preference.setHopeMinPrice(userInfo.getHopeMinPrice());
            preference.setHomeType(Optional.ofNullable(selectedHomeType).orElseGet(List::of));
            preference.setMaritalStatus(userInfo.getMaritalStatus());
            preference.setTotalGaScore(userInfo.getTotalGaScore());

            log.debug("[REC] preference={}", preference);
            return preference;
        } catch (Exception e) {
            log.error("[REC] getPreferenceInfo failed usersIdx={} err={}", usersIdx, e.toString(), e);
            return null;
        }
    }

    // ===== Helper =====

    /** Double/BigDecimal/Integer/String 모두 안전 변환 → 0~100 정수(%) */
    private Integer toPercentInt(Object v) {
        if (v == null) return null;
        try {
            double d;
            if (v instanceof Number) {
                d = ((Number) v).doubleValue();
            } else {
                d = Double.parseDouble(String.valueOf(v));
            }
            // 이미 0~1 이면 0~100으로, 이미 0~100이면 반올림만
            if (d <= 1.0) d = d * 100.0;
            if (d < 0) d = 0;
            if (d > 100) d = 100;
            return (int) Math.round(d);
        } catch (Exception e) {
            log.warn("[REC] toPercentInt invalid value={} err={}", v, e.toString());
            return null;
        }
    }

    private int safeInt(Integer v) { return v == null ? 0 : v; }
}