package org.scoula.service;

import lombok.RequiredArgsConstructor;
import org.scoula.domain.User;
import org.scoula.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;

    public User getUser(String email) {
        return userMapper.findById(email);
    }

    public List<User> getAllUsers() {
        return userMapper.findAll();
    }

    public void addUser(User user) {
        userMapper.insertUser(user);
    }

    public void updateUser(User user) {
        userMapper.updateUser(user);
    }

    public void deleteUser(Long id) {
        userMapper.deleteUser(id);
    }

    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }
}

