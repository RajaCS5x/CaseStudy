package com.movie.registrationService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.registrationService.dto.UserRequest;
import com.movie.registrationService.service.UserServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class UserController {

	@Autowired
	private UserServiceImpl userServiceImpl;

	@GetMapping("/")
	public String show() {
		return "Hello";
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody UserRequest request) {
		System.out.println("Incoming Register Request: " + request.getEmail());
		return ResponseEntity.ok(userServiceImpl.registerUser(request));
	}
}
