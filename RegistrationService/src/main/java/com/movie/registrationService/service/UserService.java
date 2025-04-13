package com.movie.registrationService.service;

import org.springframework.http.ResponseEntity;

import com.movie.registrationService.dto.UserRequest;

public interface UserService {
	
	ResponseEntity<String> registerUser(UserRequest request) ;
}
