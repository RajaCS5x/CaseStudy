package com.movie.movie_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.movie_service.model.Showtime;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
  List<Showtime> findByMovieId(Long movieId);

  List<Showtime> findByCinemaHallId(Long cinemaHallId);

  List<Showtime> findByStartTimeAfter(LocalDateTime dateTime);

  List<Showtime> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

  List<Showtime> findByMovieIdAndStartTimeBetween(Long movieId, LocalDateTime startTime, LocalDateTime endTime);

  List<Showtime> findByIsActive(boolean isActive);
}
