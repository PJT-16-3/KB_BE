package org.scoula.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.scoula.dto.HouseListDTO;
import org.scoula.dto.UserFavoriteDTO;

import java.util.List;

@Mapper
public interface UserFavoriteMapper {
    /** 단일 즐겨찾기 추가 */
    int insertUserFavorite(UserFavoriteDTO favorite);

    /** APT 공고번호 기준 중복 체크 */
    int countByUsersIdxAndAptPblanc(@Param("usersIdx")   int usersIdx, @Param("aptPblanc") String aptPblanc);

    /** 오피스텔 공고번호 기준 중복 체크 */
    int countByUsersIdxAndOfficePblanc(@Param("usersIdx")      int usersIdx, @Param("officePblanc") String officePblanc);

    int deleteByUsersIdxAndAptPblanc(@Param("usersIdx") int usersIdx, @Param("aptPblanc") String aptPblanc);

    int deleteByUsersIdxAndOfficePblanc(@Param("usersIdx") int usersIdx,@Param("officePblanc") String officePblanc);

    List<HouseListDTO> findAptHouseByPblancNo(@Param("pblancNo") String pblancNo);

    List<HouseListDTO> findOfficetelHouseByPblancNo(@Param("pblancNo") String pblancNo);

    List<UserFavoriteDTO> findFavoritesByUsersIdx(@Param("usersIdx") int usersIdx);

    int countFavoritesByPblancNo(@Param("pblancNo") String pblancNo);

}
