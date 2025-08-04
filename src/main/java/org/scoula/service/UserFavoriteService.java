package org.scoula.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.scoula.dto.HouseListDTO;
import org.scoula.dto.UserFavoriteDTO;
import org.scoula.mapper.UserFavoriteMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class UserFavoriteService {
    private final UserFavoriteMapper mapper;

    /** 즐겨찾기 추가 */
    public boolean addFavorite(int usersIdx, String houseType, String pblancNo) {
        try {
            boolean exists;

            if ("APT".equals(houseType) || "신혼희망타운".equals(houseType)) {
                exists = mapper.countByUsersIdxAndAptPblanc(usersIdx, pblancNo) > 0;
            } else if ("오피스텔".equals(houseType) || "도시형생활주택".equals(houseType)) {
                exists = mapper.countByUsersIdxAndOfficePblanc(usersIdx, pblancNo) > 0;
            } else {
                throw new IllegalArgumentException("지원하지 않는 houseType입니다: " + houseType);
            }

            if (exists) {
                log.info("{} 즐겨찾기가 이미 존재합니다. usersIdx={}, pblancNo={}", houseType, usersIdx, pblancNo);
                return false;
            }

            UserFavoriteDTO fav = new UserFavoriteDTO();
            fav.setUsersIdx(usersIdx);

            if ("APT".equals(houseType) || "신혼희망타운".equals(houseType)) {
                fav.setAptPblanc(pblancNo);
            } else {
                fav.setOfficePblanc(pblancNo);
            }

            return mapper.insertUserFavorite(fav) == 1;

        } catch (IllegalArgumentException e) {
            throw e; // 명확히 던짐
        } catch (Exception e) {
            log.error("즐겨찾기 추가 실패: {}", e.getMessage(), e);
            return false;
        }
    }

    /** 즐겨찾기 해제 */
    public boolean deleteFavorite(int usersIdx, String houseType, String pblancNo) {
        try {
            if ("APT".equals(houseType) || "신혼희망타운".equals(houseType)) {
                return mapper.deleteByUsersIdxAndAptPblanc(usersIdx, pblancNo) == 1;
            }

            if ("오피스텔".equals(houseType) || "도시형생활주택".equals(houseType)) {
                return mapper.deleteByUsersIdxAndOfficePblanc(usersIdx, pblancNo) == 1;
            }

            // 그 외 유형은 예외 발생
            throw new IllegalArgumentException("지원하지 않는 houseType입니다: " + houseType);

        } catch (IllegalArgumentException e) {
            throw e; // 명시적 예외 처리
        } catch (Exception e) {
            log.error("즐겨찾기 삭제 실패: {}", e.getMessage(), e);
            return false;
        }
    }

    public List<HouseListDTO> getFavoriteHouses(int usersIdx) {
        List<UserFavoriteDTO> favorites = mapper.findFavoritesByUsersIdx(usersIdx);
        List<HouseListDTO> result = new ArrayList<>();

        for (UserFavoriteDTO fav : favorites) {
            if (fav.getAptPblanc() != null) {
                List<HouseListDTO> aptList = mapper.findAptHouseByPblancNo(fav.getAptPblanc());
                for (HouseListDTO dto : aptList) {
                    dto.setFavoriteCount(mapper.countFavoritesByPblancNo(dto.getPblancNo()));
                }
                result.addAll(aptList);
            }
            if (fav.getOfficePblanc() != null) {
                List<HouseListDTO> offList = mapper.findOfficetelHouseByPblancNo(fav.getOfficePblanc());
                for (HouseListDTO dto : offList) {
                    dto.setFavoriteCount(mapper.countFavoritesByPblancNo(dto.getPblancNo()));
                }
                result.addAll(offList);
            }
        }

        return result;
    }

}
