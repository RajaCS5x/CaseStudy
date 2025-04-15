package com.movie.movie_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movie.movie_service.DTOs.CinemaHallDTO;
import com.movie.movie_service.exceptions.UnauthorizedAccessException;
import com.movie.movie_service.service.CinemaHallServiceImpl;
import com.movie.movie_service.utility.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cinemahalls")
public class CinemaHallController {

  private final CinemaHallServiceImpl cinemaHallService;
  private final JwtUtil jwtTokenUtil;

  public CinemaHallController(CinemaHallServiceImpl cinemaHallService, JwtUtil jwtTokenUtil) {
    this.cinemaHallService = cinemaHallService;
    this.jwtTokenUtil = jwtTokenUtil;
  }

  @PostMapping
  public ResponseEntity<CinemaHallDTO> addCinemaHall(
      @RequestBody @Valid CinemaHallDTO cinemaHallDTO,
      @RequestHeader("Authorization") String authHeader) {

    validateMerchantRole(authHeader);

    String token = authHeader.substring(7);
    String username = jwtTokenUtil.extractUsername(token);

    CinemaHallDTO createdHall = cinemaHallService.createCinemaHall(cinemaHallDTO, username);
    return new ResponseEntity<>(createdHall, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<CinemaHallDTO>> getAllCinemaHalls() {
    List<CinemaHallDTO> halls = cinemaHallService.getAllCinemaHalls();
    return ResponseEntity.ok(halls);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CinemaHallDTO> getCinemaHallById(@PathVariable Long id) {
    CinemaHallDTO hall = cinemaHallService.getCinemaHallById(id);
    return ResponseEntity.ok(hall);
  }

  @GetMapping("/venue/{venueId}")
  public ResponseEntity<List<CinemaHallDTO>> getCinemaHallsByVenue(@PathVariable Long venueId) {
    List<CinemaHallDTO> halls = cinemaHallService.getCinemaHallsByVenue(venueId);
    return ResponseEntity.ok(halls);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CinemaHallDTO> updateCinemaHall(
      @PathVariable Long id,
      @RequestBody @Valid CinemaHallDTO cinemaHallDTO,
      @RequestHeader("Authorization") String authHeader) {

    validateMerchantRole(authHeader);

    String token = authHeader.substring(7);
    String username = jwtTokenUtil.extractUsername(token);

    // Ensure the ID in the path matches the DTO

    CinemaHallDTO updatedHall = cinemaHallService.updateCinemaHall(id, cinemaHallDTO, username);
    return ResponseEntity.ok(updatedHall);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCinemaHall(
      @PathVariable Long id,
      @RequestHeader("Authorization") String authHeader) {

    validateMerchantRole(authHeader);

    String token = authHeader.substring(7);
    String username = jwtTokenUtil.extractUsername(token);

    cinemaHallService.deleteCinemaHall(id, username);
    return ResponseEntity.noContent().build();
  }

  private void validateMerchantRole(String authHeader) {
    String token = authHeader.substring(7);
    String role = jwtTokenUtil.extractRole(token);

    if (!"MERCHANT".equals(role)) {
      throw new UnauthorizedAccessException("Only merchants can manage cinema halls");
    }
  }
}
