package com.movie.login_serivce.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.movie.login_serivce.dto.LoginRequest;
import com.movie.login_serivce.dto.LoginResponse;
import com.movie.login_serivce.exceptions.UserAlreadyExistsException;
import com.movie.login_serivce.model.User;
import com.movie.login_serivce.repository.UserRepository;
import com.movie.login_serivce.util.JwtUtil;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtUtil jwtUtil;

  @Override
  public LoginResponse login(LoginRequest request) {
    User user = userRepository.findByUsername(request.getUsername())
        .or(() -> userRepository.findByEmail(request.getUsername()))
        .or(() -> userRepository.findByMobileNumber(request.getUsername()))
        .orElseThrow(() -> new UserAlreadyExistsException(
            "User not found with username or email or mobile number: " + request.getUsername()));

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new UserAlreadyExistsException("Invalid password. Try again.");
    }

    String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
    // Set the token in the response header (if needed)
    return new LoginResponse(token, user.getUsername(), "Login successful", user.getRole());
  }

}
