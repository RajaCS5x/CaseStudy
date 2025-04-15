package com.movie.movie_service.DTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDTO {
  private Long id;

  @NotBlank(message = "Title is required")
  private String title;

  @Size(max = 2000, message = "Description cannot exceed 2000 characters")
  private String description;

  private String genre;
  private String director;

  @Min(value = 1, message = "Duration must be at least 1 minute")
  private int durationMinutes;

  private String language;

  @NotNull(message = "Release date is required")
  private LocalDate releaseDate;

  private String posterUrl;
  private double rating;
}
