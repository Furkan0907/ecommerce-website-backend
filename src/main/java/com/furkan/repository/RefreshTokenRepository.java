package com.furkan.repository;

import com.furkan.model.RefreshToken;
import com.furkan.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    void deleteAllByUser(User user);
}
