package com.example.lostfound.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.lostfound.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByRegisterNumber(String registerNumber);
    Optional<User> findByEmail(String email);
}
