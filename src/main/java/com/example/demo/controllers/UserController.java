package com.example.demo.controllers;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.AppUser;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.AppUserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	private AppUserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	static final Logger log = LoggerFactory.getLogger(UserController.class);

	@GetMapping("/id/{id}")
	public ResponseEntity<AppUser> findById(@PathVariable Long id, Authentication authentication) {
		Optional<AppUser> optionalAppUser = Optional.ofNullable(userRepository.findById(id)).orElseThrow(RuntimeException::new);
		if(!authentication.getName().equals(optionalAppUser.get().getUsername())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		return ResponseEntity.of(optionalAppUser);
	}


	@GetMapping("/{username}")
	public ResponseEntity<AppUser> findByUserName(@PathVariable String username, Authentication authentication) {
		if(!authentication.getName().equals(username)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		AppUser user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}

	@PostMapping("/create")
	public ResponseEntity<AppUser> createUser(@RequestBody CreateUserRequest createUserRequest) {
	    // init  user
		AppUser user = new AppUser();
		// set username
		user.setUsername(createUserRequest.getUsername());
		// init cart
		Cart cart = new Cart();
		// save cart
		cartRepository.save(cart);
		// set cart to user
		user.setCart(cart);
		// validate password length and that confirm password matches
		if(createUserRequest.getPassword().length() < 7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			// log warning due to password mismatch
			log.warn("Error with user password. Cannot create user {}", createUserRequest.getUsername());
			// return bad request
			return ResponseEntity.badRequest().build();
		}
		// after password validation set password
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
		// save user and init saved user
		AppUser savedUser = userRepository.save(user);
		// if id comes back incorrect there was an issue saving the user
		if(savedUser.getId() <= 0) {
		    // therefore we will log the issue as a warning
			log.warn("Error creating user {}", createUserRequest.getUsername());
			// and return bad request
			return ResponseEntity.badRequest().build();
		} else {
		    // otherwise we were successful in creating the new user
			log.trace("Created user {}", createUserRequest.getUsername());
		}
		// finish off with returning user entity
		return ResponseEntity.ok(user);
	}
	
}
