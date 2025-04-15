package com.movie.movie_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.movie.movie_service.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {
  List<Movie> findByMerchantUsername(String merchantUsername);
}
