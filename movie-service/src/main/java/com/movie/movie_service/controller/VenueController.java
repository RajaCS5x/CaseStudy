package com.movie.movie_service.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.movie.movie_service.DTOs.VenueDTO;
import com.movie.movie_service.exceptions.UnauthorizedAccessException;
import com.movie.movie_service.service.VenueServiceImpl;
import com.movie.movie_service.utility.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueServiceImpl venueService;
    private final JwtUtil jwtTokenUtil;

    public VenueController(VenueServiceImpl venueService, JwtUtil jwtTokenUtil) {
        this.venueService = venueService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping
    public ResponseEntity<VenueDTO> addVenue(
            @RequestBody @Valid VenueDTO venueDTO,
            @RequestHeader("Authorization") String authHeader) {

        validateMerchantRole(authHeader);

        String token = authHeader.substring(7);
        String username = jwtTokenUtil.extractUsername(token);

        VenueDTO createdVenue = venueService.createVenue(venueDTO, username);
        return new ResponseEntity<>(createdVenue, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VenueDTO>> getAllVenues() {
        List<VenueDTO> venues = venueService.getAllVenues();
        return ResponseEntity.ok(venues);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueDTO> getVenueById(@PathVariable Long id) {
        VenueDTO venue = venueService.getVenueById(id);
        return ResponseEntity.ok(venue);
    }

    @GetMapping("/search")
    public ResponseEntity<List<VenueDTO>> searchVenuesByLocation(
            @RequestParam String city) {
        List<VenueDTO> venues = venueService.getVenuesByCity(city);
        return ResponseEntity.ok(venues);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VenueDTO> updateVenue(
            @PathVariable Long id,
            @RequestBody @Valid VenueDTO venueDTO,
            @RequestHeader("Authorization") String authHeader) {

        validateMerchantRole(authHeader);

        String token = authHeader.substring(7);
        String username = jwtTokenUtil.extractUsername(token);

        // Ensure the ID in the path matches the DTO

        VenueDTO updatedVenue = venueService.updateVenue(id, venueDTO, username);
        return ResponseEntity.ok(updatedVenue);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {

        validateMerchantRole(authHeader);

        String token = authHeader.substring(7);
        String username = jwtTokenUtil.extractUsername(token);

        venueService.deleteVenue(id, username);
        return ResponseEntity.noContent().build();
    }

    private void validateMerchantRole(String authHeader) {
        String token = authHeader.substring(7);
        String role = jwtTokenUtil.extractRole(token);

        if (!"MERCHANT".equals(role)) {
            throw new UnauthorizedAccessException("Only merchants can manage venues");
        }
    }
}