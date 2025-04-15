package com.movie.movie_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.movie.movie_service.DTOs.CinemaHallDTO;
import com.movie.movie_service.exceptions.ResourceNotFoundException;
import com.movie.movie_service.exceptions.UnauthorizedAccessException;
import com.movie.movie_service.model.CinemaHall;
import com.movie.movie_service.model.Venue;
import com.movie.movie_service.repository.CinemaHallRepository;
import com.movie.movie_service.repository.VenueRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CinemaHallServiceImpl {

  private final CinemaHallRepository cinemaHallRepository;
  private final VenueRepository venueRepository;

  public CinemaHallServiceImpl(CinemaHallRepository cinemaHallRepository, VenueRepository venueRepository) {
    this.cinemaHallRepository = cinemaHallRepository;
    this.venueRepository = venueRepository;
  }

  public List<CinemaHallDTO> getAllCinemaHalls() {
    return cinemaHallRepository.findAll().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public List<CinemaHallDTO> getCinemaHallsByVenue(Long venueId) {
    return cinemaHallRepository.findByVenueId(venueId).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public CinemaHallDTO getCinemaHallById(Long id) {
    CinemaHall cinemaHall = cinemaHallRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cinema hall not found with id: " + id));
    return convertToDTO(cinemaHall);
  }

  public CinemaHallDTO createCinemaHall(CinemaHallDTO cinemaHallDTO, String merchantUsername) {
    // Find venue and check ownership
    Venue venue = venueRepository.findById(cinemaHallDTO.getVenueId())
        .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + cinemaHallDTO.getVenueId()));

    if (!venue.getMerchantUsername().equals(merchantUsername)) {
      throw new UnauthorizedAccessException("You don't have permission to add cinema halls to this venue");
    }

    CinemaHall cinemaHall = convertToEntity(cinemaHallDTO);
    cinemaHall.setVenue(venue);
    CinemaHall savedCinemaHall = cinemaHallRepository.save(cinemaHall);

    return convertToDTO(savedCinemaHall);
  }

  public CinemaHallDTO updateCinemaHall(Long id, CinemaHallDTO cinemaHallDTO, String merchantUsername) {
    CinemaHall existingCinemaHall = cinemaHallRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cinema hall not found with id: " + id));

    // Check if the merchant is the owner of the venue this cinema hall belongs to
    if (!existingCinemaHall.getVenue().getMerchantUsername().equals(merchantUsername)) {
      throw new UnauthorizedAccessException("You don't have permission to update this cinema hall");
    }

    // If venue is being changed, check ownership of new venue
    if (!cinemaHallDTO.getVenueId().equals(existingCinemaHall.getVenue().getId())) {
      Venue newVenue = venueRepository.findById(cinemaHallDTO.getVenueId())
          .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + cinemaHallDTO.getVenueId()));

      if (!newVenue.getMerchantUsername().equals(merchantUsername)) {
        throw new UnauthorizedAccessException(
            "You don't have permission to move this cinema hall to the specified venue");
      }

      existingCinemaHall.setVenue(newVenue);
    }

    // Update fields
    existingCinemaHall.setName(cinemaHallDTO.getName());
    existingCinemaHall.setTotalSeats(cinemaHallDTO.getTotalSeats());
    existingCinemaHall.setHallType(cinemaHallDTO.getHallType());

    CinemaHall updatedCinemaHall = cinemaHallRepository.save(existingCinemaHall);
    return convertToDTO(updatedCinemaHall);
  }

  public void deleteCinemaHall(Long id, String merchantUsername) {
    CinemaHall cinemaHall = cinemaHallRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cinema hall not found with id: " + id));

    // Check if the merchant is the owner of the venue this cinema hall belongs to
    if (!cinemaHall.getVenue().getMerchantUsername().equals(merchantUsername)) {
      throw new UnauthorizedAccessException("You don't have permission to delete this cinema hall");
    }

    cinemaHallRepository.delete(cinemaHall);
  }

  private CinemaHallDTO convertToDTO(CinemaHall cinemaHall) {
    CinemaHallDTO cinemaHallDTO = new CinemaHallDTO();
    BeanUtils.copyProperties(cinemaHall, cinemaHallDTO);
    cinemaHallDTO.setVenueId(cinemaHall.getVenue().getId());
    return cinemaHallDTO;
  }

  private CinemaHall convertToEntity(CinemaHallDTO cinemaHallDTO) {
    CinemaHall cinemaHall = new CinemaHall();
    BeanUtils.copyProperties(cinemaHallDTO, cinemaHall, "venueId");
    return cinemaHall;
  }
}
