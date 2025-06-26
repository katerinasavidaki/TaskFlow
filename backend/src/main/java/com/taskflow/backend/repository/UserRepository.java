package com.taskflow.backend.repository;

import com.taskflow.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);
    Optional<User> findByUuid(String uuid);
    Optional<User> findByAfm(String afm);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Boolean existsByUsername(String username);
}
