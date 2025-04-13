package com.movie.login_serivce.service;

import com.movie.login_serivce.dto.LoginRequest;
import com.movie.login_serivce.dto.LoginResponse;

public interface AuthService {
  LoginResponse login(LoginRequest request);

}
