package com.movie.movie_service.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "movies")
public class Movie {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Movie title is required")
  private String title;

  @NotBlank(message = "Genre is required")
  private String genre;

  @NotBlank(message = "Language is required")
  private String language;

  @Min(value = 30, message = "Duration must be at least 30 minutes")
  private int durationInMinutes;

  @NotBlank(message = "Description is required")
  private String description;

  @NotBlank(message = "POSTER URL is required")
  private String posterUrl;

  @DecimalMin(value = "0.0", inclusive = true, message = "Rating cannot be negative")
  @DecimalMax(value = "10.0", inclusive = true, message = "Rating cannot be more than 10")
  private double rating;

  @NotNull(message = "Release date is required")
  private LocalDate releaseDate;

}
