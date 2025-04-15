package com.movie.movie_service.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CinemaHallDTO {
  private Long id;

  @NotBlank(message = "Cinema hall name is required")
  private String name;

  @Min(value = 1, message = "Total seats must be at least 1")
  private int totalSeats;

  private String hallType;

  @NotNull(message = "Venue ID is required")
  private Long venueId;
}
