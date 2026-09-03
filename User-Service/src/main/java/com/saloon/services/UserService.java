package com.saloon.services;

import com.saloon.models.User;

import java.util.List;

public interface UserService {
    User createUser(User user);
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);
    User updateUser(User user, Long id);
    void deleteUser(Long id);
    User getUserByJwt(String jwt);
}
