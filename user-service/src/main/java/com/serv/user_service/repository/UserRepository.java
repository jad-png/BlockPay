package com.serv.user_service.repository;

import com.serv.user_service.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
}
