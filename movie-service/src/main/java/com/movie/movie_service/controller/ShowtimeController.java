package com.movie.movie_service.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.movie.movie_service.DTOs.ShowtimeDTO;
import com.movie.movie_service.exceptions.UnauthorizedAccessException;
import com.movie.movie_service.service.ShowtimeServiceImpl;
import com.movie.movie_service.utility.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {

  private final ShowtimeServiceImpl showtimeService;
  private final JwtUtil jwtTokenUtil;

  public ShowtimeController(ShowtimeServiceImpl showtimeService, JwtUtil jwtTokenUtil) {
    this.showtimeService = showtimeService;
    this.jwtTokenUtil = jwtTokenUtil;
  }

  @PostMapping
  public ResponseEntity<ShowtimeDTO> addShowtime(
      @RequestBody @Valid ShowtimeDTO showtimeDTO,
      @RequestHeader("Authorization") String authHeader) {

    validateMerchantRole(authHeader);

    String token = authHeader.substring(7);
    String username = jwtTokenUtil.extractUsername(token);

    ShowtimeDTO createdShowtime = showtimeService.createShowTime(showtimeDTO, username);
    return new ResponseEntity<>(createdShowtime, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<ShowtimeDTO>> getAllShowtimes() {
    List<ShowtimeDTO> showtimes = showtimeService.getAllShowTimes();
    return ResponseEntity.ok(showtimes);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ShowtimeDTO> getShowtimeById(@PathVariable Long id) {
    ShowtimeDTO showtime = showtimeService.getShowTimeById(id);
    return ResponseEntity.ok(showtime);
  }

  @GetMapping("/movie/{movieId}")
  public ResponseEntity<List<ShowtimeDTO>> getShowtimesByMovie(@PathVariable Long movieId) {
    List<ShowtimeDTO> showtimes = showtimeService.getShowTimesByMovie(movieId);
    return ResponseEntity.ok(showtimes);
  }

  @GetMapping("/cinemahall/{cinemaHallId}")
  public ResponseEntity<List<ShowtimeDTO>> getShowtimesByCinemaHall(@PathVariable Long cinemaHallId) {
    List<ShowtimeDTO> showtimes = showtimeService.getShowTimesByCinemaHall(cinemaHallId);
    return ResponseEntity.ok(showtimes);
  }

  @GetMapping("/date")
  public ResponseEntity<List<ShowtimeDTO>> getShowtimesByDate(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime date) {
    List<ShowtimeDTO> showtimes = showtimeService.getShowtimesByDate(date);
    return ResponseEntity.ok(showtimes);
  }

  @GetMapping("/active")
  public ResponseEntity<List<ShowtimeDTO>> getActiveShowtimes() {
    List<ShowtimeDTO> showtimes = showtimeService.getActiveShowtimes();
    return ResponseEntity.ok(showtimes);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ShowtimeDTO> updateShowtime(
      @PathVariable Long id,
      @RequestBody @Valid ShowtimeDTO showtimeDTO,
      @RequestHeader("Authorization") String authHeader) {

    validateMerchantRole(authHeader);

    String token = authHeader.substring(7);
    String username = jwtTokenUtil.extractUsername(token);

    // Ensure the ID in the path matches the DTO

    ShowtimeDTO updatedShowtime = showtimeService.updateShowTime(id, showtimeDTO, username);
    return ResponseEntity.ok(updatedShowtime);
  }

  @PatchMapping("/{id}/activate")
  public ResponseEntity<ShowtimeDTO> activateShowtime(
      @PathVariable Long id,
      @RequestHeader("Authorization") String authHeader) {

    validateMerchantRole(authHeader);

    String token = authHeader.substring(7);
    String username = jwtTokenUtil.extractUsername(token);

    ShowtimeDTO activatedShowtime = showtimeService.setShowtimeStatus(id, true, username);
    return ResponseEntity.ok(activatedShowtime);
  }

  @PatchMapping("/{id}/deactivate")
  public ResponseEntity<ShowtimeDTO> deactivateShowtime(
      @PathVariable Long id,
      @RequestHeader("Authorization") String authHeader) {

    validateMerchantRole(authHeader);

    String token = authHeader.substring(7);
    String username = jwtTokenUtil.extractUsername(token);

    ShowtimeDTO deactivatedShowtime = showtimeService.setShowtimeStatus(id, false, username);
    return ResponseEntity.ok(deactivatedShowtime);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteShowtime(
      @PathVariable Long id,
      @RequestHeader("Authorization") String authHeader) {

    validateMerchantRole(authHeader);

    String token = authHeader.substring(7);
    String username = jwtTokenUtil.extractUsername(token);

    showtimeService.deleteShowTime(id, username);
    return ResponseEntity.noContent().build();
  }

  private void validateMerchantRole(String authHeader) {
    String token = authHeader.substring(7);
    String role = jwtTokenUtil.extractRole(token);

    if (!"MERCHANT".equals(role)) {
      throw new UnauthorizedAccessException("Only merchants can manage showtimes");
    }
  }
}
