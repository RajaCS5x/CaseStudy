package com.movie.movie_service.service;

import com.movie.movie_service.model.Movie;
import java.util.List;

public interface MovieService {
  Movie addMovie(Movie movie, String token);

  List<Movie> getAllMovies();

  Movie getMovieById(Long id);

  Movie updateMovie(Long id, Movie movie, String token);

  void deleteMovie(Long id, String token);
}
