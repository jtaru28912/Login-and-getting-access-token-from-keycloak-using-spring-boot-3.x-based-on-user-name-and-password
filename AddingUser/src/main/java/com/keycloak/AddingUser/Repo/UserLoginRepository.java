package com.keycloak.AddingUser.Repo;

import com.keycloak.AddingUser.Model.LoginRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginRepository extends JpaRepository<LoginRequest, Long> {
}
