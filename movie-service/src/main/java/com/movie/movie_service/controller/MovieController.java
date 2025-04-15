package com.movie.movie_service.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.movie.movie_service.DTOs.MovieDTO;
import com.movie.movie_service.exceptions.UnauthorizedAccessException;
import com.movie.movie_service.service.MovieServiceImpl;
import com.movie.movie_service.utility.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

  private final MovieServiceImpl movieService;
  private final JwtUtil jwtTokenUtil;

  public MovieController(MovieServiceImpl movieService, JwtUtil jwtTokenUtil) {
    this.movieService = movieService;
    this.jwtTokenUtil = jwtTokenUtil;
  }

  @PostMapping
  public ResponseEntity<MovieDTO> addMovie(
      @RequestBody @Valid MovieDTO movieDTO,
      @RequestHeader("Authorization") String authHeader) {

    // Extract and validate JWT token
    String token = authHeader.substring(7); // Remove "Bearer " prefix
    String username = jwtTokenUtil.extractUsername(token);
    String role = jwtTokenUtil.extractRole(token);

    // Check if user is a merchant
    if (!"MERCHANT".equals(role)) {
      throw new UnauthorizedAccessException("Only merchants can add movies");
    }

    MovieDTO createdMovie = movieService.createMovie(movieDTO, username);
    return new ResponseEntity<>(createdMovie, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<MovieDTO>> getAllMovies() {
    List<MovieDTO> movies = movieService.getAllMovies();
    return ResponseEntity.ok(movies);
  }

  @GetMapping("/{id}")
  public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long id) {
    MovieDTO movie = movieService.getMovieById(id);
    return ResponseEntity.ok(movie);
  }

  @PutMapping("/{id}")
  public ResponseEntity<MovieDTO> updateMovie(
      @PathVariable Long id,
      @RequestBody @Valid MovieDTO movieDTO,
      @RequestHeader("Authorization") String authHeader) {

    // Extract and validate JWT token
    String token = authHeader.substring(7);
    String username = jwtTokenUtil.extractUsername(token);
    String role = jwtTokenUtil.extractRole(token);

    // Check if user is a merchant
    if (!"MERCHANT".equals(role)) {
      throw new UnauthorizedAccessException("Only merchants can update movies");
    }

    // Ensure the ID in the path matches the DTO

    MovieDTO updatedMovie = movieService.updateMovie(id, movieDTO, username);
    return ResponseEntity.ok(updatedMovie);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteMovie(
      @PathVariable Long id,
      @RequestHeader("Authorization") String authHeader) {

    // Extract and validate JWT token
    String token = authHeader.substring(7);
    String username = jwtTokenUtil.extractUsername(token);
    String role = jwtTokenUtil.extractRole(token);

    // Check if user is a merchant
    if (!"MERCHANT".equals(role)) {
      throw new UnauthorizedAccessException("Only merchants can delete movies");
    }

    movieService.deleteMovie(id, username);
    return ResponseEntity.noContent().build();
  }
}