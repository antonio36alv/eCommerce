package com.example.demo.controllers;

import java.util.List;

import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.AppUser;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.AppUserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	
	@Autowired
	private AppUserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	final static Logger log = LoggerFactory.getLogger(OrderController.class);

	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username, Authentication authentication) {
	    // check that auth username is the same as the username in the request
		if(!authentication.getName().equals(username)) {
			log.warn("Request parameter contained a different user than the one requested.\nAuthenticated user: {}\nUser requested for: {}", authentication.getName(), username);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		AppUser user = userRepository.findByUsername(username);
		if(user == null) {
			log.error("Could not find {} within database.", username);
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		UserOrder savedOrder = orderRepository.save(order);
		if(savedOrder != null) {
			log.info("{} successfully placed an order.", authentication.getName());
		} else {
			log.warn("{}'s order was not placed.", authentication.getName());
			return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
		}
		return ResponseEntity.ok(savedOrder);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username, Authentication authentication) {
		if(!authentication.getName().equals(username)) {
			log.warn("Request parameter contained a different user than the one requested.\nAuthenticated user: {}\nUser requested for: {}", authentication.getName(), username);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		AppUser user = userRepository.findByUsername(username);
		if(user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}

}
