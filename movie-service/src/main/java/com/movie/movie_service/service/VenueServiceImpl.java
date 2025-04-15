package com.movie.movie_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.movie.movie_service.DTOs.VenueDTO;
import com.movie.movie_service.exceptions.*;
import com.movie.movie_service.model.Venue;
import com.movie.movie_service.repository.VenueRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VenueServiceImpl {

  private final VenueRepository venueRepository;

  public VenueServiceImpl(VenueRepository venueRepository) {
    this.venueRepository = venueRepository;
  }

  public List<VenueDTO> getAllVenues() {
    return venueRepository.findAll().stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public List<VenueDTO> getVenuesByCity(String city) {
    return venueRepository.findByCity(city).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public List<VenueDTO> getVenuesByMerchant(String username) {
    return venueRepository.findByMerchantUsername(username).stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  public VenueDTO getVenueById(Long id) {
    Venue venue = venueRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + id));
    return convertToDTO(venue);
  }

  public VenueDTO createVenue(VenueDTO venueDTO, String merchantUsername) {
    Venue venue = convertToEntity(venueDTO);
    venue.setMerchantUsername(merchantUsername);
    Venue savedVenue = venueRepository.save(venue);
    return convertToDTO(savedVenue);
  }

  public VenueDTO updateVenue(Long id, VenueDTO venueDTO, String merchantUsername) {
    Venue existingVenue = venueRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + id));

    // Check if the merchant is the owner of this venue
    if (!existingVenue.getMerchantUsername().equals(merchantUsername)) {
      throw new UnauthorizedAccessException("You don't have permission to update this venue");
    }

    // Update fields
    BeanUtils.copyProperties(venueDTO, existingVenue, "id", "merchantUsername", "cinemaHalls");

    Venue updatedVenue = venueRepository.save(existingVenue);
    return convertToDTO(updatedVenue);
  }

  public void deleteVenue(Long id, String merchantUsername) {
    Venue venue = venueRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + id));

    // Check if the merchant is the owner of this venue
    if (!venue.getMerchantUsername().equals(merchantUsername)) {
      throw new UnauthorizedAccessException("You don't have permission to delete this venue");
    }

    venueRepository.delete(venue);
  }

  private VenueDTO convertToDTO(Venue venue) {
    VenueDTO venueDTO = new VenueDTO();
    BeanUtils.copyProperties(venue, venueDTO);
    return venueDTO;
  }

  private Venue convertToEntity(VenueDTO venueDTO) {
    Venue venue = new Venue();
    BeanUtils.copyProperties(venueDTO, venue);
    return venue;
  }
}
