package com.example.demo.model.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.AppUser;

public interface CartRepository extends JpaRepository<Cart, Long> {
	Cart findByUser(AppUser user);
}
