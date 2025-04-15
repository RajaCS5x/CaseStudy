package com.movie.movie_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.movie.movie_service.DTOs.MovieDTO;
import com.movie.movie_service.exceptions.*;
import com.movie.movie_service.model.Movie;
import com.movie.movie_service.repository.MovieRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl {

  private final MovieRepository movieRepository;

  public MovieServiceImpl(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
  }

  public List<MovieDTO> getAllMovies() {
    return movieRepository.findAll().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public List<MovieDTO> getMoviesByMerchant(String username) {
    return movieRepository.findByMerchantUsername(username).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public MovieDTO getMovieById(Long id) {
    Movie movie = movieRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));
    return convertToDTO(movie);
  }

  public MovieDTO createMovie(MovieDTO movieDTO, String merchantUsername) {
    Movie movie = convertToEntity(movieDTO);
    movie.setMerchantUsername(merchantUsername);
    Movie savedMovie = movieRepository.save(movie);
    return convertToDTO(savedMovie);
  }

  public MovieDTO updateMovie(Long id, MovieDTO movieDTO, String merchantUsername) {
    Movie existingMovie = movieRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

    // Check if the merchant is the owner of this movie
    if (!existingMovie.getMerchantUsername().equals(merchantUsername)) {
      throw new UnauthorizedAccessException("You don't have permission to update this movie");
    }

    // Update fields
    BeanUtils.copyProperties(movieDTO, existingMovie, "id", "merchantUsername");

    Movie updatedMovie = movieRepository.save(existingMovie);
    return convertToDTO(updatedMovie);
  }

  public void deleteMovie(Long id, String merchantUsername) {
    Movie movie = movieRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + id));

    // Check if the merchant is the owner of this movie
    if (!movie.getMerchantUsername().equals(merchantUsername)) {
      throw new UnauthorizedAccessException("You don't have permission to delete this movie");
    }

    movieRepository.delete(movie);
  }

  private MovieDTO convertToDTO(Movie movie) {
    MovieDTO movieDTO = new MovieDTO();
    BeanUtils.copyProperties(movie, movieDTO);
    return movieDTO;
  }

  private Movie convertToEntity(MovieDTO movieDTO) {
    Movie movie = new Movie();
    BeanUtils.copyProperties(movieDTO, movie);
    return movie;
  }
}