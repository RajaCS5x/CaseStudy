package com.movie.movie_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie.movie_service.model.Venue;

public interface VenueRepository extends JpaRepository<Venue, Long> {
  List<Venue> findByMerchantUsername(String merchantUsername);

  List<Venue> findByCity(String city);

}
