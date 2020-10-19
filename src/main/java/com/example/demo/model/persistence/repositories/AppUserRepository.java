package com.example.demo.model.persistence.repositories;

import com.example.demo.model.persistence.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
	AppUser findByUsername(String username);
}
