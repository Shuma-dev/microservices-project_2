package org.example.service;

import org.example.entity.User;

import java.util.List;

public interface UserService {
    User createUser(String name, String email, Integer age);

    User findById(Long id);

    List<User> findAll();

    User updateUser(Long id, String name, String email, Integer age);

    void deleteUser(Long id);
}
