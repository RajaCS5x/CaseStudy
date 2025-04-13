package com.movie.login_serivce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.login_serivce.dto.LoginRequest;
import com.movie.login_serivce.dto.LoginResponse;
import com.movie.login_serivce.service.AuthServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api")
public class LoginController {

  @Autowired
  private AuthServiceImpl authServiceImpl;

  @RequestMapping("/")
  public String hello() {
    return "Hello from Login Service!";
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
    LoginResponse loginResponse = authServiceImpl.login(loginRequest);
    return ResponseEntity.ok(loginResponse);
  }

}
