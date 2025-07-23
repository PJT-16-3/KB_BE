package org.scoula.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.scoula.domain.User;

import java.util.List;

@Mapper
public interface UserMapper {

    int findUserIdxByUserId(@Param("userId") String userId);

    User findById(@Param("userId") String userId);
    List<User> findAll();
    void insertUser(User user);
    void updateUser(User user);
    void deleteUser(Long id);
    User findByUsername(@Param("username") String username);
    int countUserByIdx(@Param("usersIdx") Long usersIdx);  //  users_auth 테이블에 해당 유저 정보가 저장되어있는지 확인
    void insertUserAuth(@Param("usersIdx") Long usersIdx);
}