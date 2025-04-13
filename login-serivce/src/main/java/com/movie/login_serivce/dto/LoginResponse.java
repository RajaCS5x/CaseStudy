package com.movie.login_serivce.dto;

import com.movie.login_serivce.model.Role;

public class LoginResponse {
  private String token;
  private String username;
  private String message;
  private Role role;

  public LoginResponse() {
  }

  public LoginResponse(String token, String username, String message, Role role) {
    this.token = token;
    this.username = username;
    this.message = message;
    this.role = role;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public Role getRole() {
    return role;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setRole(Role role) {
    this.role = role;
  }
}
