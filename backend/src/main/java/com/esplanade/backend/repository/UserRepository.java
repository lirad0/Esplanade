package com.esplanade.backend.repository;

import com.esplanade.backend.model.UserData;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserData, String> {
    Optional<UserData> findByUsername(String username);

    boolean existsByUsername(String username);
}