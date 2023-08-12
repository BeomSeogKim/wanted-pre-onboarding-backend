package com.wanted.internship.repository;

import com.wanted.internship.entity.RefreshToken;
import com.wanted.internship.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUser(User user);
}
