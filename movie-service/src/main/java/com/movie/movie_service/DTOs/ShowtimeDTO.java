package com.movie.movie_service.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeDTO {
  private Long id;

  @NotNull(message = "Movie ID is required")
  private Long movieId;

  @NotNull(message = "Cinema hall ID is required")
  private Long cinemaHallId;

  @NotNull(message = "Start time is required")
  @Future(message = "Start time must be in the future")
  private LocalDateTime startTime;

  private LocalDateTime endTime;

  @Min(value = 0, message = "Ticket price cannot be negative")
  private double ticketPrice;

  @Min(value = 0, message = "Available seats cannot be negative")
  private int availableSeats;

  private boolean isActive = true;
}
