package com.movie.movie_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.movie.movie_service.model.CinemaHall;

public interface CinemaHallRepository extends JpaRepository<CinemaHall, Long> {
  List<CinemaHall> findByVenueId(Long venueId);
}
